package org.metabrainz.mobile.presentation.features.onboarding

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.limerse.onboard.OnboardAdvanced
import com.limerse.onboard.OnboardFragment
import com.limerse.onboard.OnboardPageTransformerType
import com.limerse.onboard.model.SliderPage
import org.metabrainz.mobile.R
import org.metabrainz.mobile.presentation.UserPreferences
import org.metabrainz.mobile.presentation.features.dashboard.DashboardActivity

class FeaturesActivity : OnboardAdvanced() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(OnboardFragment.newInstance(
            "Tag",
            "Fix your Audio Metadata",
            resourceId = R.raw.tagger,
            backgroundColor =  resources.getColor(R.color.app_bg),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            SliderPage(
                "Search",
                "Explore MusicBrainz Data",
                resourceId = R.raw.search,
                backgroundColor =  resources.getColor(R.color.app_bg),
                isLottie = true
            )
        ))

        addSlide(OnboardFragment.newInstance(
            "Scan",
            "Barcodes Search",
            resourceId = R.raw.scan,
            backgroundColor =  resources.getColor(R.color.app_bg),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Collections",
            "Explore your MusicBrainz Collection",
            resourceId = R.raw.collections,
            backgroundColor =  resources.getColor(R.color.app_bg),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Cross Platform",
            "Transfer music data across devices",
            resourceId = R.raw.laptop_and_stuff,
            backgroundColor =  resources.getColor(R.color.app_bg),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Listens",
            "Track your music listening habits ",
            resourceId = R.raw.teen,
            backgroundColor =  resources.getColor(R.color.app_bg),
            isLottie = true
        ))

        addSlide(OnboardFragment.newInstance(
            "Critiques",
            "Read and write about an album or event",
            resourceId = R.raw.review,
            backgroundColor =  resources.getColor(R.color.app_bg),
            isLottie = true
        ))

        setTransformer(OnboardPageTransformerType.Parallax())
    }

    public override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Skip?")
        builder.setMessage("Some of the functionalities are specially available through the settings.")
        builder.setPositiveButton("Understood") { dialog: DialogInterface?, which: Int ->
            UserPreferences.setOnBoardingCompleted()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Cancel") {dialog: DialogInterface?, which: Int ->}
        builder.create().show()
    }

    public override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        UserPreferences.setOnBoardingCompleted()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}