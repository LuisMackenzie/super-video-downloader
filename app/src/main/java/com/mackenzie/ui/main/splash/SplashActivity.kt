package com.mackenzie.ui.main.splash

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
//import com.allVideoDownloaderXmaster.OpenForTesting
import com.mackenzie.R
import com.mackenzie.databinding.ActivitySplashBinding
import com.mackenzie.ui.main.base.BaseActivity
import com.mackenzie.ui.main.home.MainActivity
import javax.inject.Inject

//@OpenForTesting
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var splashViewModel: SplashViewModel

    private lateinit var dataBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashViewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        dataBinding.viewModel = splashViewModel

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 3000)
    }
}