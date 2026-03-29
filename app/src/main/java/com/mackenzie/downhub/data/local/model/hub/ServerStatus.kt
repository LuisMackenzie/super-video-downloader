package com.mackenzie.downhub.data.local.model.hub

data class ServerStatus(
    val isOffline: Boolean = false,
    val canChargeList: Boolean = false,
    val isFullyFunctional: Boolean = false
)