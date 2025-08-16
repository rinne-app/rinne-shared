package com.rinne.libraries.content.generator.core

interface RinneContentGenerator {
    fun generate()
}

sealed interface RinneContentGeneratorConfig {
    data class Gpt(val model: String) : RinneContentGeneratorConfig
}

data class RinneContentGeneratorAction(
    val instructions: String,
    val message: String,
)

data class RinneContentGeneratorRequest(
    val action: RinneContentGeneratorAction,
    val history: List<RinneContentGeneratorAction>,
    val config: RinneContentGeneratorConfig,
)