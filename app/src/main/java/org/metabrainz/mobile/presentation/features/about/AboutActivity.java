package org.metabrainz.mobile.presentation.features.about;

import android.os.Bundle;
import android.view.Menu;

import org.metabrainz.mobile.R;
import org.metabrainz.mobile.databinding.ActivityAboutBinding;
import org.metabrainz.mobile.presentation.MusicBrainzActivity;

import java.util.Objects;

public class AboutActivity extends MusicBrainzActivity {

    private ActivityAboutBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

}
