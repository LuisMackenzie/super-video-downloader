package com.mackenzie.downhub.di.module

import com.mackenzie.downhub.di.interfaces.ICryptographyComponent
import java.security.KeyStore

class CryptographyModule : ICryptographyComponent {
    override fun keyStore(): KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    }
}