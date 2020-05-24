package org.metabrainz.mobile.presentation.features.release;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.lifecycle.ViewModelProvider;

<<<<<<< HEAD
import org.metabrainz.mobile.data.sources.Constants;
=======
import org.metabrainz.mobile.R;
>>>>>>> b793e03... Improve usage of live data and reactive patterns.
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Release;
import org.metabrainz.mobile.databinding.ActivityReleaseBinding;
import org.metabrainz.mobile.presentation.MusicBrainzActivity;
import org.metabrainz.mobile.presentation.features.userdata.UserViewModel;

import java.util.Objects;

/**
 * Activity that retrieves and displays information about an artist given an
 * artist MBID.
 */
public class ReleaseActivity extends MusicBrainzActivity {

    public static final String LOG_TAG = "DebugReleaseInfo";

    private ActivityReleaseBinding binding;

    private ReleaseViewModel releaseViewModel;
    private UserViewModel userViewModel;

    private String mbid;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReleaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        releaseViewModel = new ViewModelProvider(this).get(ReleaseViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        mbid = getIntent().getStringExtra(Constants.MBID);
        if (mbid != null && !mbid.isEmpty()) releaseViewModel.setMBID(mbid);

<<<<<<< HEAD
<<<<<<< HEAD
        releaseViewModel.getData().observe(this, this::setRelease);
=======
        releaseViewModel.initializeData().observe(this, this::setRelease);
        releaseViewModel.fetchData();
>>>>>>> de8f646... Refactor lookup repositories to remove redundancy.
    }

    private void setRelease(Release release) {
        if (release != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(release.getTitle());
            userViewModel.setUserData(release);
        }
=======
        releaseViewModel.getData().observe(this, this::setRelease);
    }

    private void setRelease(Release release) {
        if (release != null)
            Objects.requireNonNull(getSupportActionBar()).setTitle(release.getTitle());
>>>>>>> b793e03... Improve usage of live data and reactive patterns.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
