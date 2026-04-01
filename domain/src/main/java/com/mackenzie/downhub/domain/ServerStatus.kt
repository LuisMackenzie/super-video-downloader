package com.mackenzie.downhub.domain

data class ServerStatus(
    val isOffline: Boolean = false,
    val canChargeList: Boolean = false,
    val isFullyFunctional: Boolean = false
)