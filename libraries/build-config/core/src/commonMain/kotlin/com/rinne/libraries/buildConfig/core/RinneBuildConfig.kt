package com.rinne.libraries.buildConfig.core


object RinneBuildConfigProvider {
    private var _config: RinneBuildConfig? = null
    val config get() = requireNotNull(_config)

    fun setConfig(config: RinneBuildConfig) {
        _config = config
    }
}

interface RinneBuildConfig {
    val version: String
    val versionCode: String

    val flavor: String
    val buildType: String

    companion object : RinneBuildConfig by RinneBuildConfigProvider.config
}

fun flavor(flavor: String, block: () -> Unit) {}
