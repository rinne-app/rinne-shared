package com.rinne.libraries.network.client.cache


interface RinneNetworkCacheProvider {
    suspend fun cache(config: RinneNetworkCacheConfig, data: String)
    suspend fun getAllCaches(): RinneNetworkCacheConfig

    suspend fun getCache(config: RinneNetworkCacheConfig): String
}

data class RinneNetworkCacheConfig(
    val key: String,
//    val request:
)
