package org.metabrainz.mobile.presentation.features.base

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import org.metabrainz.mobile.R
import org.metabrainz.mobile.databinding.LayoutToolbarBinding
import org.metabrainz.mobile.presentation.Configuration
import org.metabrainz.mobile.presentation.IntentFactory.getLogin
import org.metabrainz.mobile.presentation.IntentFactory.getSettings
import org.metabrainz.mobile.presentation.UserPreferences.systemLanguagePreference
import org.metabrainz.mobile.presentation.features.onboarding.FeaturesActivity
import org.metabrainz.mobile.util.Utils.changeLanguage
import org.metabrainz.mobile.util.Utils.emailIntent
import java.util.*

abstract class MusicBrainzActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dash, menu)
        return true
    }

    protected open fun getBrowserURI(): Uri? {
        return Uri.EMPTY
    }
    protected fun setupToolbar(binding: ViewBinding) {
        val toolbarBinding = LayoutToolbarBinding.bind(binding.root)
        setSupportActionBar(toolbarBinding.toolbar)
        Objects.requireNonNull(supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.menu_preferences -> {
                startActivity(getSettings(applicationContext))
            }
            R.id.menu_feedback -> {
                sendFeedback()
            }
            R.id.menu_features -> {
                startActivity(Intent(this,FeaturesActivity::class.java))
            }
            R.id.menu_login -> {
                startActivity(getLogin(applicationContext))
            }
            R.id.menu_open_website -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = getBrowserURI()
                startActivity(intent)
            }
        }
        return false
    }

    override fun attachBaseContext(newBase: Context) {
        if (systemLanguagePreference) {
            val context: Context = changeLanguage(newBase, "en")
            super.attachBaseContext(context)
        } else super.attachBaseContext(newBase)
    }

    private fun sendFeedback() {
        try {
            startActivity(emailIntent(Configuration.FEEDBACK_EMAIL, Configuration.FEEDBACK_SUBJECT))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.toast_feedback_fail, Toast.LENGTH_LONG).show()
        }
    }
}