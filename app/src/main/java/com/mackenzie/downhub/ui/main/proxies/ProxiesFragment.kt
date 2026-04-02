package com.mackenzie.downhub.ui.main.proxies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.Observable
import androidx.lifecycle.lifecycleScope
import com.mackenzie.downhub.R
import com.mackenzie.downhub.data.local.model.Proxy
import com.mackenzie.downhub.data.local.model.ProxyType
import com.mackenzie.downhub.databinding.FragmentProxiesBinding
import com.mackenzie.downhub.ui.component.adapter.ProxiesAdapter
import com.mackenzie.downhub.ui.component.adapter.ProxiesListener
import com.mackenzie.downhub.ui.main.base.BaseFragment
import com.mackenzie.downhub.ui.main.home.MainActivity
import com.mackenzie.downhub.util.proxy_utils.CustomProxyController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProxiesFragment : BaseFragment() {

    companion object {
        fun newInstance() = ProxiesFragment()
    }

    @Inject
    lateinit var proxyController: CustomProxyController

    private val mainActivity get() = requireActivity() as MainActivity

    private lateinit var dataBinding: FragmentProxiesBinding

    private lateinit var proxiesViewModel: ProxiesViewModel

    private lateinit var proxiesAdapter: ProxiesAdapter

    private val proxiesListCallback = object :
        Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(
            sender: Observable?,
            propertyId: Int
        ) {
            proxiesViewModel.proxiesList.get()?.let {
                lifecycleScope.launch(Dispatchers.Main) {
                    proxiesAdapter.setData(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        proxiesViewModel = mainActivity.proxiesViewModel

        proxiesAdapter = ProxiesAdapter(mutableListOf(), proxiesListener)

        dataBinding = FragmentProxiesBinding.inflate(inflater, container, false).apply {
            this.viewModel = proxiesViewModel
            this.listener = proxiesListener
            this.proxiesRecyclerView.adapter = proxiesAdapter
            proxiesAdapter.setData(proxiesViewModel.proxiesList.get()?.toList() ?: emptyList())

            this.saveCustomDnsButton.setOnClickListener {
                proxiesViewModel.saveCustomDns()
                Toast.makeText(
                    this@ProxiesFragment.context,
                    getString(R.string.custom_dns_saved_message),
                    Toast.LENGTH_SHORT
                ).show()

            }

            this.addProxyButton.setOnClickListener {
                val host = this.hostEditText.text.toString()
                val port = this.portEditText.text.toString()
                val user = this.loginEditText.text.toString()
                val password = this.passwordEditText.text.toString()

                val selectedType = if (this.httpRadioButton.isChecked) {
                    ProxyType.HTTP
                } else {
                    ProxyType.SOCKS5
                }

                if (isValidHost(host) && isValidPort(port)) {
                    val newProxy = Proxy(
                        id = System.currentTimeMillis().toString(),
                        host = host,
                        port = port,
                        user = user,
                        password = password,
                        type = selectedType
                    )

                    proxiesViewModel.addProxy(newProxy)

                    this.hostEditText.text?.clear()
                    this.portEditText.text?.clear()
                    this.loginEditText.text?.clear()
                    this.passwordEditText.text?.clear()
                    this.httpRadioButton.isChecked = true

                    if (proxiesViewModel.isProxyOn.get() == false) {
                        proxiesViewModel.turnOnProxy()
                    }
                } else {
                    Toast.makeText(
                        this@ProxiesFragment.context, "Invalid host or port", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            val color = getThemeBackgroundColor()
            this.proxiesContainer.setBackgroundColor(color)
        }

        proxiesViewModel.proxiesList.addOnPropertyChangedCallback(proxiesListCallback)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return dataBinding.root
    }

    override fun onDestroyView() {
        proxiesViewModel.proxiesList.removeOnPropertyChangedCallback(proxiesListCallback)
        super.onDestroyView()
    }

    private val proxiesListener = object : ProxiesListener {
        override fun onProxyRemoveClicked(proxy: Proxy) {
            proxiesViewModel.removeProxy(proxy)
        }

        override fun onProxyToggle(isChecked: Boolean) {
            if (isChecked) {
                proxiesViewModel.turnOnProxy()
            } else {
                proxiesViewModel.turnOffProxy()
            }
        }
    }

    private fun isValidPort(port: String): Boolean {
        return try {
            val portNumber = port.toInt()
            portNumber in 1..65535
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isValidHost(host: String): Boolean {
        return host.isNotEmpty()
    }
}
