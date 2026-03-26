package com.mackenzie.ui.main.player

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.mackenzie.R
import com.mackenzie.ui.main.base.BaseActivity
import com.mackenzie.util.ext.addFragment

class VideoPlayerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_player)

        intent.extras?.let { addFragment(R.id.player_content_frame, it, ::VideoPlayerFragment) }
    }
}