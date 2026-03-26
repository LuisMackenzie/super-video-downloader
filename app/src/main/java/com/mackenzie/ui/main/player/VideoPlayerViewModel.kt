package com.mackenzie.ui.main.player

import android.net.Uri
import androidx.databinding.ObservableField
import com.mackenzie.ui.main.base.BaseViewModel
import com.mackenzie.util.SingleLiveEvent
import javax.inject.Inject

class VideoPlayerViewModel @Inject constructor() : BaseViewModel() {

    val videoName = ObservableField("")
    val videoUrl = ObservableField(Uri.EMPTY)
    val videoHeaders = ObservableField(emptyMap<String, String>())

    val stopPlayerEvent = SingleLiveEvent<Void?>()

    override fun start() {
    }

    override fun stop() {
        stopPlayerEvent.call()
    }
}