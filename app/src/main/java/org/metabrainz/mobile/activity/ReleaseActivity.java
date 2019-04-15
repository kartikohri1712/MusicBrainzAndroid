package org.metabrainz.mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import org.metabrainz.mobile.App;
import org.metabrainz.mobile.R;
import org.metabrainz.mobile.adapter.ReleasePagerAdapter;
import org.metabrainz.mobile.api.data.obsolete.Artist;
import org.metabrainz.mobile.api.data.obsolete.Release;
import org.metabrainz.mobile.api.data.obsolete.ReleaseArtist;
import org.metabrainz.mobile.api.data.obsolete.ReleaseSearchResult;
import org.metabrainz.mobile.api.data.obsolete.Tag;
import org.metabrainz.mobile.api.data.obsolete.UserData;
import org.metabrainz.mobile.api.webservice.BarcodeNotFoundException;
import org.metabrainz.mobile.async.CollectionEditLoader;
import org.metabrainz.mobile.async.result.AsyncEntityResult;
import org.metabrainz.mobile.async.result.AsyncResult;
import org.metabrainz.mobile.config.Configuration;
import org.metabrainz.mobile.presentation.features.artist.ArtistActivity;
import org.metabrainz.mobile.dialog.BarcodeNotFoundDialog;
import org.metabrainz.mobile.dialog.BarcodeNotFoundDialog.BarcodeNotFoundCallback;
import org.metabrainz.mobile.dialog.CollectionAddDialog;
import org.metabrainz.mobile.dialog.CollectionAddDialog.AddToCollectionCallback;
import org.metabrainz.mobile.dialog.ReleaseSelectionDialog;
import org.metabrainz.mobile.dialog.ReleaseSelectionDialog.ReleaseSelectionCallbacks;
import org.metabrainz.mobile.fragment.EditFragment;
import org.metabrainz.mobile.fragment.contracts.EntityTab;
import org.metabrainz.mobile.intent.IntentFactory.Extra;
import org.metabrainz.mobile.string.StringFormat;
import org.metabrainz.mobile.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity which retrieves and displays information about a release.
 * <p>
 * This Activity initiates lookups given three sources that generally result in
 * display of release information. The intent must contain either a barcode, a
 * release MBID or a release group MBID.
 */
public class ReleaseActivity extends MusicBrainzActivity implements AddToCollectionCallback, ReleaseSelectionCallbacks,
        BarcodeNotFoundCallback, EditFragment.Callback {

    public static final int MESSAGE_NOT_FOUND = 0;
    public static final int MESSAGE_RELEASE_SELECTION = 1;
    private static final int RELEASE_LOADER = 0;
    private static final int RELEASE_GROUP_RELEASE_LOADER = 1;
    private static final int BARCODE_RELEASE_LOADER = 2;
    private static final int COLLECTION_ADD_LOADER = 3;
    private Release release;
    private List<ReleaseSearchResult> releasesInfo;
    private UserData userData;
    private View loading;
    private View error;
    private String releaseMbid;
    private String releaseGroupMbid;
    private String barcode;
    private TextView tagView;
    private RatingBar ratingBar;
    private boolean provideArtistAction = true;
    private ReleasePagerAdapter pagerAdapter;
    private LoaderCallbacks<AsyncResult<Void>> collectionAddCallbacks = new LoaderCallbacks<AsyncResult<Void>>() {

        @Override
        public Loader<AsyncResult<Void>> onCreateLoader(int id, Bundle args) {
            return new CollectionEditLoader(args.getString("collectionMbid"), args.getString("releaseMbid"), true);
        }

        @Override
        public void onLoadFinished(Loader<AsyncResult<Void>> loader, AsyncResult<Void> data) {
            getSupportLoaderManager().destroyLoader(COLLECTION_ADD_LOADER);
            setSupportProgressBarIndeterminateVisibility(false);
            switch (data.getStatus()) {
                case EXCEPTION:
                    Toast.makeText(getApplicationContext(), R.string.collection_add_fail, Toast.LENGTH_LONG).show();
                    break;
                case SUCCESS:
                    Toast.makeText(getApplicationContext(), R.string.collection_add_success, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<AsyncResult<Void>> loader) {
            loader.reset();
        }
    };
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NOT_FOUND:
                    showBarcodeNotFoundDialog();
                    break;
                case MESSAGE_RELEASE_SELECTION:
                    showReleaseSelectionDialog();
            }
        }
    };
    private LoaderCallbacks<AsyncResult<List<ReleaseSearchResult>>> releasesInfoLoader = new LoaderCallbacks<AsyncResult<List<ReleaseSearchResult>>>() {

        @Override
        public Loader<AsyncResult<List<ReleaseSearchResult>>> onCreateLoader(int id, Bundle args) {
            //return new ReleaseGroupReleasesLoader(releaseGroupMbid);
            return null;
        }

        @Override
        public void onLoadFinished(Loader<AsyncResult<List<ReleaseSearchResult>>> loader,
                                   AsyncResult<List<ReleaseSearchResult>> container) {
            switch (container.getStatus()) {
                case EXCEPTION:
                    showConnectionErrorWarning();
                    break;
                case SUCCESS:
                    releasesInfo = container.getData();
                    if (releasesInfo.size() == 1) {
                        ReleaseSearchResult singleRelease = releasesInfo.get(0);
                        releaseMbid = singleRelease.getReleaseMbid();
                        getSupportLoaderManager().initLoader(RELEASE_LOADER, null, releaseLoaderCallbacks);
                    } else {
                        handler.sendEmptyMessage(MESSAGE_RELEASE_SELECTION);
                    }
            }
        }

        @Override
        public void onLoaderReset(Loader<AsyncResult<List<ReleaseSearchResult>>> loader) {
            loader.reset();
        }
    };
    private LoaderCallbacks<AsyncEntityResult<Release>> releaseLoaderCallbacks = new LoaderCallbacks<AsyncEntityResult<Release>>() {

        @Override
        public Loader<AsyncEntityResult<Release>> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case RELEASE_LOADER:
                    //return new ReleaseLoader(releaseMbid);
                case BARCODE_RELEASE_LOADER:
                    //return new BarcodeReleaseLoader(barcode);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<AsyncEntityResult<Release>> loader, AsyncEntityResult<Release> container) {
            switch (container.getStatus()) {
                case EXCEPTION:
                    if (container.getException() instanceof BarcodeNotFoundException) {
                        handler.sendEmptyMessage(MESSAGE_NOT_FOUND);
                    } else {
                        showConnectionErrorWarning();
                    }
                    break;
                case SUCCESS:
                    release = container.getData();
                    userData = container.getUserData();
                    populateLayout();
            }
        }

        @Override
        public void onLoaderReset(Loader<AsyncEntityResult<Release>> loader) {
            loader.reset();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        releaseMbid = getIntent().getStringExtra(Extra.RELEASE_MBID);
        releaseGroupMbid = getIntent().getStringExtra(Extra.RG_MBID);
        barcode = getIntent().getStringExtra(Extra.BARCODE);

        setSupportProgressBarIndeterminateVisibility(false);
        configureLoader();
        setContentView(R.layout.activity_release);
        configurePager();
        findViews();
    }

    private void configureLoader() {
        if (releaseMbid != null) {
            getSupportLoaderManager().initLoader(RELEASE_LOADER, null, releaseLoaderCallbacks);
        } else if (releaseGroupMbid != null) {
            getSupportLoaderManager().initLoader(RELEASE_GROUP_RELEASE_LOADER, null, releasesInfoLoader);
        } else if (barcode != null) {
            getSupportLoaderManager().initLoader(BARCODE_RELEASE_LOADER, null, releaseLoaderCallbacks);
        } else {
            this.finish();
        }
    }

    private void populateLayout() {
        TextView artist = findViewById(R.id.release_artist);
        TextView title = findViewById(R.id.release_name);
        TextView labels = findViewById(R.id.release_label);
        TextView releaseDate = findViewById(R.id.release_name);

        if (isArtistUpAvailable()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        artist.setText(StringFormat.commaSeparateArtists(release.getArtists()));
        title.setText(release.getTitle());
        labels.setText(StringFormat.commaSeparate(release.getLabels()));
        releaseDate.setText(release.getDate());
        tagView.setText(StringFormat.commaSeparateTags(release.getReleaseGroupTags(), this));
        ratingBar.setRating(release.getReleaseGroupRating());

        artist.setSelected(true);
        title.setSelected(true);
        labels.setSelected(true);
        tagView.setSelected(true);

        updateFragments();
        loading.setVisibility(View.GONE);
    }

    private void configurePager() {
        pagerAdapter = new ReleasePagerAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        //TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        //indicator.setViewPager(pager);
        pager.setOffscreenPageLimit(pagerAdapter.getCount() - 1);
    }

    @SuppressWarnings("unchecked")
    private void updateFragments() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            ((EntityTab<Release>) fm.findFragmentByTag(pagerAdapter.makeTag(i))).update(release);
        }
    }

    private void findViews() {
        loading = findViewById(R.id.loading);
        error = findViewById(R.id.error);
        ratingBar = findViewById(R.id.rating);
        tagView = findViewById(R.id.tags);
    }

    private boolean isArtistUpAvailable() {
        ArrayList<ReleaseArtist> releaseArtists = release.getArtists();
        if (releaseArtists.size() != 1) {
            provideArtistAction = false;
        } else {
            ReleaseArtist singleArtist = releaseArtists.get(0);
            for (String id : Artist.SPECIAL_PURPOSE) {
                if (singleArtist.getMbid().equals(id)) {
                    provideArtistAction = false;
                }
            }
        }
        return provideArtistAction;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (App.isUserLoggedIn()) {
            getMenuInflater().inflate(R.menu.release_logged_in, menu);
        } else {
            getMenuInflater().inflate(R.menu.release, menu);
        }
        ShareActionProvider actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(Utils.shareIntent(Configuration.RELEASE_SHARE + releaseMbid));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                if (provideArtistAction) {
                    startActivity(createArtistIntent());
                }
                return true;
            case R.id.action_add_collection:
                DialogFragment collectionDialog = new CollectionAddDialog();
                collectionDialog.show(getSupportFragmentManager(), CollectionAddDialog.TAG);
                return true;
        }
        return false;
    }

    private Intent createArtistIntent() {
        final Intent releaseIntent = new Intent(this, ArtistActivity.class);
        releaseIntent.putExtra(Extra.ARTIST_MBID, release.getArtists().get(0).getMbid());
        return releaseIntent;
    }

    private void showConnectionErrorWarning() {
        error.setVisibility(View.VISIBLE);
        Button retry = error.findViewById(R.id.retry_button);
        retry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                restartLoader();
            }
        });
    }

    private void showBarcodeNotFoundDialog() {
        DialogFragment barcodeNotFound = new BarcodeNotFoundDialog();
        barcodeNotFound.show(getSupportFragmentManager(), BarcodeNotFoundDialog.TAG);
    }

    private void restartLoader() {
        if (releaseMbid != null) {
            getSupportLoaderManager().restartLoader(RELEASE_LOADER, null, releaseLoaderCallbacks);
        } else if (releaseGroupMbid != null) {
            getSupportLoaderManager().restartLoader(RELEASE_GROUP_RELEASE_LOADER, null, releasesInfoLoader);
        } else if (barcode != null) {
            getSupportLoaderManager().restartLoader(BARCODE_RELEASE_LOADER, null, releaseLoaderCallbacks);
        }
    }

    private void showReleaseSelectionDialog() {
        DialogFragment releaseSelection = new ReleaseSelectionDialog();
        releaseSelection.show(getSupportFragmentManager(), ReleaseSelectionDialog.TAG);
    }

    @Override
    public void addReleaseToCollection(String collectionMbid) {
        Bundle args = new Bundle();
        args.putString("collectionMbid", collectionMbid);
        args.putString("releaseMbid", release.getMbid());
        getSupportLoaderManager().initLoader(COLLECTION_ADD_LOADER, args, collectionAddCallbacks);
        setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    public List<ReleaseSearchResult> getReleasesInfo() {
        return releasesInfo;
    }

    @Override
    public void onReleaseSelected(String mbid) {
        Intent releaseIntent = new Intent(App.getContext(), ReleaseActivity.class);
        releaseIntent.putExtra(Extra.RELEASE_MBID, mbid);
        startActivity(releaseIntent);
        finish();
    }

    @Override
    public void addBarcode() {
        Intent barcodeIntent = new Intent(this, BarcodeSearchActivity.class);
        barcodeIntent.putExtra("barcode", barcode);
        startActivity(barcodeIntent);
        finish();
    }

    @Override
    public void showLoading() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public String getMbid() {
        return release.getReleaseGroupMbid();
    }

    @Override
    public UserData getUserData() {
        return userData;
    }

    @Override
    public void updateTags(List<Tag> tags) {
        release.setReleaseGroupTags(tags);
        tagView.setText(StringFormat.commaSeparateTags(tags, this));
        getSupportLoaderManager().destroyLoader(RELEASE_LOADER);
    }

    @Override
    public void updateRating(Float rating) {
        release.setReleaseGroupRating(rating);
        ratingBar.setRating(rating);
        getSupportLoaderManager().destroyLoader(RELEASE_LOADER);
    }

}
