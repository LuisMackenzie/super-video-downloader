package com.mackenzie.downhub.ui.main.bookmarks


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mackenzie.downhub.data.local.room.entity.PageInfo
import com.mackenzie.downhub.databinding.FragmentBookmarksBinding
import com.mackenzie.downhub.ui.component.adapter.BookmarksAdapter
import com.mackenzie.downhub.ui.component.adapter.BookmarksListener
import com.mackenzie.downhub.ui.component.adapter.ReorderableItemTouchHelperCallback
import com.mackenzie.downhub.ui.main.base.BaseFragment
import com.mackenzie.downhub.ui.main.home.MainActivity
import com.mackenzie.downhub.ui.main.home.browser.webTab.WebTabFactory
import com.mackenzie.downhub.ui.main.progress.WrapContentLinearLayoutManager
import javax.inject.Inject

class BookmarksFragment : BaseFragment() {

    private lateinit var dataBinding: FragmentBookmarksBinding

    @Inject
    lateinit var mainActivity: MainActivity

    private lateinit var bookmarksAdapter: BookmarksAdapter

    private var bookmarksCached = mutableListOf<PageInfo>()

    private var hasChanged = false

    companion object {
        @JvmStatic
        fun newInstance() = BookmarksFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val color = getThemeBackgroundColor()
        val mainModel = mainActivity.mainViewModel

        bookmarksAdapter = BookmarksAdapter(mutableListOf(), listener)
        val touchHelperCallback = ReorderableItemTouchHelperCallback(bookmarksAdapter)
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)

        val layoutManager =
            WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        dataBinding = FragmentBookmarksBinding.inflate(inflater, container, false).apply {
            this.bookmarksContainer.setBackgroundColor(color)
            this.mainVModel = mainModel
            this.bookmarksList.layoutManager = layoutManager
            this.bookmarksList.adapter = bookmarksAdapter
            itemTouchHelper.attachToRecyclerView(this.bookmarksList)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }

        return dataBinding.root
    }

    val listener = object : BookmarksListener {
        override fun onBookmarkOpenClicked(view: View, bookmarkItem: PageInfo) {
            mainActivity.mainViewModel.browserServicesProvider?.getOpenTabEvent()?.value =
                WebTabFactory.Companion.createWebTabFromInput(bookmarkItem.link)
            parentFragmentManager.popBackStack()
        }

        override fun onBookmarkMove(bookmarks: MutableList<PageInfo>) {
            bookmarksCached = bookmarks.toMutableList()
            hasChanged = true
        }

        override fun onBookmarkDelete(bookmarks: MutableList<PageInfo>, position: Int) {
            bookmarks.removeAt(position)
            bookmarksCached = bookmarks
            hasChanged = true
        }
    }

    override fun onDestroy() {
        if (hasChanged) {
            mainActivity.mainViewModel.updateBookmarks(bookmarksCached)
        }

        super.onDestroy()
    }
}
