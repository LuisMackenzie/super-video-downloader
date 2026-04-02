package com.mackenzie.downhub.di.interfaces

import java.security.KeyStore

interface ICryptographyComponent {

    fun keyStore(): KeyStore
}