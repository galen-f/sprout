package com.example.sprout.ui.editplant

data class EditPlantFormState(
    val name: String = "",
    val species: String = "",
    val wateringIntervalDays: Int = 7,
    val enableFertilizer: Boolean = false,
    val fertilizerIntervalDays: Int = 30,
    val notes: String = "",
    val nameError: String? = null,
) {
    val isValid: Boolean get() = name.isNotBlank()
}
