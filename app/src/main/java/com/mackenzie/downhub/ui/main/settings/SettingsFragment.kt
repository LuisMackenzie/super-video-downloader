package com.mackenzie.downhub.ui.main.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import com.mackenzie.downhub.R
import com.mackenzie.downhub.databinding.FragmentSettingsBinding
import com.mackenzie.downhub.ui.main.base.BaseFragment
import com.mackenzie.downhub.ui.main.home.MainActivity
import com.mackenzie.downhub.util.FileUtil
import com.mackenzie.downhub.util.IntentUtil
import com.mackenzie.downhub.util.SystemUtil
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    @Inject
    lateinit var fileUtil: FileUtil

    @Inject
    lateinit var intentUtil: IntentUtil

    @Inject
    lateinit var systemUtil: SystemUtil

    private val mainActivity get() = requireActivity() as MainActivity

    private lateinit var dataBinding: FragmentSettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    private var lastSavedRegularThreadsCount = -1

    /**
     * Launches the system folder picker (SAF) and handles the result.
     * Takes persistent read/write permissions and converts the tree URI
     * to a filesystem path for the download pipeline.
     */
    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { treeUri ->
        if (treeUri != null) {
            // Take persistent permissions so the app retains access across reboots
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            requireContext().contentResolver.takePersistableUriPermission(treeUri, takeFlags)

            val filePath = FileUtil.treeUriToFilePath(treeUri)
            if (filePath != null) {
                val dir = File(filePath)
                if (dir.exists() && dir.canWrite() || dir.mkdirs()) {
                    settingsViewModel.setDownloadsFolderCustom(filePath)
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.custom_path_invalid),
                        Toast.LENGTH_LONG
                    ).show()
                    revertRadioToCurrentStorage()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.custom_path_invalid),
                    Toast.LENGTH_LONG
                ).show()
                revertRadioToCurrentStorage()
            }
        } else {
            // User cancelled the picker — revert radio button selection
            revertRadioToCurrentStorage()
        }
    }

    private val tresholdCallback = object : Observable.OnPropertyChangedCallback() {
        @SuppressLint("SetTextI18n")
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (!isAdded) {
                return
            }
            val currentTreshold = settingsViewModel.videoDetectionTreshold.get()
            val readableSize = FileUtil.Companion.getFileSizeReadable(currentTreshold.toDouble())
            dataBinding.adsTresholdText.text =
                getString(R.string.ads_detection_treshold) + " $readableSize"
        }
    }

    private val storageTypeCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (!isAdded) return
            val newCheckId = when (settingsViewModel.storageType.get()) {
                StorageType.SD -> R.id.option_sd_card
                StorageType.HIDDEN -> R.id.option_hidden_folder
                StorageType.HIDDEN_SD -> R.id.option_sd_app_folder
                StorageType.CUSTOM -> R.id.option_custom_path
                else -> -1
            }
            if (newCheckId != -1 && dataBinding.storageOptions.checkedRadioButtonId != newCheckId) {
                dataBinding.storageOptions.check(newCheckId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = mainActivity.settingsViewModel
        dataBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        dataBinding.viewModel = settingsViewModel
        dataBinding.lifecycleOwner = viewLifecycleOwner

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSeekBarListeners()
        setupRadioGroupListener()
        setupTextUpdateCallbacks()
        setupCustomPathClickListener()
        handleUIEvents()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        settingsViewModel.start()
    }

    override fun onDestroyView() {
        settingsViewModel.stop()
        tresholdCallback.let {
            settingsViewModel.videoDetectionTreshold.removeOnPropertyChangedCallback(
                it
            )
        }
        storageTypeCallback.let { settingsViewModel.storageType.removeOnPropertyChangedCallback(it) }
        super.onDestroyView()
    }

    private fun setupSeekBarListeners() {
        dataBinding.seekBarRegular.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setRegularThreadsCount(progress)

                    if (lastSavedRegularThreadsCount == 1 && progress > 1) {
                        context?.let { showDownloadWarningDialog(it) }
                    }
                    lastSavedRegularThreadsCount = progress
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { settingsViewModel.setRegularThreadsCount(it.progress) }
            }
        })

        dataBinding.seekBarM3u8.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setM3u8ThreadsCount(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { settingsViewModel.setM3u8ThreadsCount(it.progress) }
            }
        })

        dataBinding.seekBarAdsTreshold.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    settingsViewModel.setVideoDetectionTreshold(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { settingsViewModel.setVideoDetectionTreshold(it.progress) }
            }
        })
    }

    private fun setupRadioGroupListener() {
        storageTypeCallback.let { settingsViewModel.storageType.addOnPropertyChangedCallback(it) }
        storageTypeCallback.onPropertyChanged(null, 0)
    }

    private fun setupTextUpdateCallbacks() {
        settingsViewModel.videoDetectionTreshold.addOnPropertyChangedCallback(tresholdCallback)
        tresholdCallback.onPropertyChanged(null, 0)
    }

    /**
     * Allows the user to tap on the currently displayed custom path
     * to re-launch the folder picker and choose a different directory.
     */
    private fun setupCustomPathClickListener() {
        dataBinding.customPathDisplay.setOnClickListener {
            launchFolderPicker()
        }
    }

    private fun handleUIEvents() {
        settingsViewModel.clearCookiesEvent.observe(viewLifecycleOwner) {
            systemUtil.clearCookies(context)
        }
        settingsViewModel.openVideoFolderEvent.observe(viewLifecycleOwner) {
            intentUtil.openVideoFolder(context, fileUtil.folderDir.path)
        }
        settingsViewModel.showCustomPathPickerEvent.observe(viewLifecycleOwner) {
            launchFolderPicker()
        }
    }

    /**
     * Opens the system folder picker (SAF ACTION_OPEN_DOCUMENT_TREE).
     */
    private fun launchFolderPicker() {
        folderPickerLauncher.launch(null)
    }

    /**
     * Reverts the radio group selection to match the currently active storage type
     * in the ViewModel. Used when the folder picker is cancelled or the selected
     * path is invalid.
     */
    private fun revertRadioToCurrentStorage() {
        storageTypeCallback.onPropertyChanged(null, 0)
    }

    private fun showDownloadWarningDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Download Warning")
            .setMessage("Some downloads may be corrupted in multi-thread downloading, if you experience some issues, switch back to single thread download!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
