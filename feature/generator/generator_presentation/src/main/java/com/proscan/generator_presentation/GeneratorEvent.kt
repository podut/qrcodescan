package com.proscan.generator_presentation

sealed class GeneratorEvent {
    data class SelectType(val type: GeneratorType) : GeneratorEvent()
    data class UpdateTextField(val field: String, val value: String) : GeneratorEvent()
    object Generate : GeneratorEvent()
    object Share : GeneratorEvent()
    object CopyToClipboard : GeneratorEvent()
    object ClearGenerated : GeneratorEvent()
}
