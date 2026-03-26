package com.mackenzie.downhub.util

import android.content.Context
import android.webkit.CookieManager
import android.widget.Toast
//import com.allVideoDownloaderXmaster.OpenForTesting
import com.mackenzie.downhub.R
import javax.inject.Inject

//@OpenForTesting
class SystemUtil @Inject constructor() {

    fun clearCookies(context: Context?) {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        context?.let {
            Toast.makeText(it, it.getString(R.string.cookies_cleared), Toast.LENGTH_SHORT)
                .show()
        }
    }
}