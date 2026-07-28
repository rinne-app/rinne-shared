package com.rinne.libraries.network.client.core.model


sealed interface RinneHttpMethod {
    data class Custom(val value: String) : RinneHttpMethod

    object Get : RinneHttpMethod
    object Post : RinneHttpMethod
    object Put : RinneHttpMethod

    object Patch : RinneHttpMethod
    object Delete : RinneHttpMethod
    object Head : RinneHttpMethod
    object Options : RinneHttpMethod
}