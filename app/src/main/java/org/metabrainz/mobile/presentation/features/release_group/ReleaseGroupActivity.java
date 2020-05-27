package org.metabrainz.mobile.presentation.features.release_group;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.lifecycle.ViewModelProvider;

import org.metabrainz.mobile.data.sources.Constants;
import org.metabrainz.mobile.data.sources.api.entities.mbentity.ReleaseGroup;
import org.metabrainz.mobile.databinding.ActivityReleaseGroupBinding;
import org.metabrainz.mobile.presentation.MusicBrainzActivity;
import org.metabrainz.mobile.presentation.features.userdata.UserViewModel;

import java.util.Objects;

public class ReleaseGroupActivity extends MusicBrainzActivity {


    public static final String LOG_TAG = "DebugReleaseGroupInfo";

    private ActivityReleaseGroupBinding binding;

    private ReleaseGroupViewModel releaseGroupViewModel;
    private UserViewModel userViewModel;

    private String mbid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReleaseGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        releaseGroupViewModel = new ViewModelProvider(this).get(ReleaseGroupViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        mbid = getIntent().getStringExtra(Constants.MBID);
        if (mbid != null && !mbid.isEmpty()) releaseGroupViewModel.setMBID(mbid);

        releaseGroupViewModel.getData().observe(this, this::setReleaseGroup);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setReleaseGroup(ReleaseGroup releaseGroup) {
        if (releaseGroup != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(releaseGroup.getTitle());
            userViewModel.setUserData(releaseGroup);
        }
    }
}
