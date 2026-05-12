package com.example.sprout.ui.addplant

import android.net.Uri

data class AddPlantFormState(
    val name: String = "",
    val species: String = "",
    val coverPhotoUri: Uri? = null,
    val wateringIntervalDays: Int = 7,
    val enableFertilizer: Boolean = false,
    val fertilizerIntervalDays: Int = 30,
    val notes: String = "",
    val nameError: String? = null,
) {
    val isValid: Boolean get() = name.isNotBlank()
}
