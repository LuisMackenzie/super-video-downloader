package com.mackenzie.downhub.ui.main.player

import android.net.Uri
import androidx.databinding.ObservableField
import com.mackenzie.downhub.ui.main.base.BaseViewModel
import com.mackenzie.downhub.util.SingleLiveEvent
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