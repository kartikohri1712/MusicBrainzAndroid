package org.metabrainz.mobile.presentation

import androidx.preference.PreferenceManager
import org.metabrainz.mobile.App
import org.metabrainz.mobile.presentation.features.login.LoginSharedPreferences

object UserPreferences {

    const val PREFERENCE_CLEAR_SUGGESTIONS = "clear_suggestions"
    const val PREFERENCE_TAGGER_DIRECTORY = "tagger_directory"
    const val PREFERENCE_LISTENBRAINZ_TOKEN = "listenbrainz_user_token"
    const val PREFERENCE_LISTENING_ENABLED = "listening_enabled"
    private const val PREFERENCE_LISTENING_SPOTIFY = "listening_spotify_enabled"
    private const val PREFERENCE_GET_PRIVATE_COLLECTIONS = "private_collections"
    private const val PREFERENCE_RATINGS_TAGS = "ratings_tags"
    private const val PREFERENCE_SYSTEM_LANGUAGE = "use_english"
    private const val PREFERENCE_ONBOARDING = "onboarding"
    private const val PREFERENCE_ADVANCED = "advanced_features"
    private val preferences = PreferenceManager.getDefaultSharedPreferences(App.context)

    fun setOnBoardingCompleted() {
        val editor = LoginSharedPreferences.preferences.edit()
        editor.putBoolean(PREFERENCE_ONBOARDING, true)
        editor.apply()
    }

    val onBoardingStatus = preferences.getBoolean(PREFERENCE_ONBOARDING, false)
    val privateCollectionsPreference = preferences.getBoolean(PREFERENCE_GET_PRIVATE_COLLECTIONS, false)
    val ratingsTagsPreference = preferences.getBoolean(PREFERENCE_RATINGS_TAGS, false)
    val taggerDirectoryPreference = preferences.getString(PREFERENCE_TAGGER_DIRECTORY, App.TAGGER_ROOT_DIRECTORY)
    val systemLanguagePreference = preferences.getBoolean(PREFERENCE_SYSTEM_LANGUAGE, false)

    var advancedFeaturesPreference: Boolean
        get() = preferences.getBoolean(PREFERENCE_ADVANCED, false)
        set(value) {
            val editor = preferences.edit()
            editor.putBoolean(PREFERENCE_ADVANCED, value)
            editor.apply()
        }

    val preferenceListenBrainzToken = preferences.getString(PREFERENCE_LISTENBRAINZ_TOKEN, null)
    var preferenceListeningEnabled: Boolean
        get() = preferences.getBoolean(PREFERENCE_LISTENING_ENABLED, false)
        set(value) {
            val editor = preferences.edit()
            editor.putBoolean(PREFERENCE_LISTENING_ENABLED, value)
            editor.apply()
        }
    val preferenceListeningSpotifyEnabled = preferences.getBoolean(PREFERENCE_LISTENING_SPOTIFY, false)
}