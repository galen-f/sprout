package com.example.sprout.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val showArchivedKey = booleanPreferencesKey("show_archived_plants")
    private val hasSeenOnboardingKey = booleanPreferencesKey("has_seen_onboarding")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val plantListViewKey = stringPreferencesKey("plant_list_view")
    private val plantSortOrderKey = stringPreferencesKey("plant_sort_order")

    val showArchivedPlants: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[showArchivedKey] ?: false }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[hasSeenOnboardingKey] ?: false }

    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val reminderMinuteKey = intPreferencesKey("reminder_minute")
    private val repeatRemindersKey = booleanPreferencesKey("repeat_reminders")

    val themeMode: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[themeModeKey] ?: "system" }

    val plantListView: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[plantListViewKey] ?: "grid" }

    val plantSortOrder: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[plantSortOrderKey] ?: "next_due" }

    val reminderHour: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[reminderHourKey] ?: 9 }

    val reminderMinute: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[reminderMinuteKey] ?: 0 }

    val repeatReminders: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[repeatRemindersKey] ?: false }

    suspend fun setShowArchived(show: Boolean) {
        context.dataStore.edit { prefs -> prefs[showArchivedKey] = show }
    }

    suspend fun setHasSeenOnboarding(seen: Boolean) {
        context.dataStore.edit { prefs -> prefs[hasSeenOnboardingKey] = seen }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs -> prefs[themeModeKey] = mode }
    }

    suspend fun setPlantListView(view: String) {
        context.dataStore.edit { prefs -> prefs[plantListViewKey] = view }
    }

    suspend fun setPlantSortOrder(order: String) {
        context.dataStore.edit { prefs -> prefs[plantSortOrderKey] = order }
    }

    suspend fun setReminderHour(hour: Int) {
        context.dataStore.edit { prefs -> prefs[reminderHourKey] = hour }
    }

    suspend fun setReminderMinute(minute: Int) {
        context.dataStore.edit { prefs -> prefs[reminderMinuteKey] = minute }
    }

    suspend fun setRepeatReminders(repeat: Boolean) {
        context.dataStore.edit { prefs -> prefs[repeatRemindersKey] = repeat }
    }
}
