package org.metabrainz.mobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import org.metabrainz.mobile.App;
import org.metabrainz.mobile.R;
import org.metabrainz.mobile.fragment.WelcomeFragment;
import org.metabrainz.mobile.intent.IntentFactory;
import org.metabrainz.mobile.intent.IntentFactory.Extra;
import org.metabrainz.mobile.intent.zxing.IntentIntegrator;
import org.metabrainz.mobile.intent.zxing.IntentResult;
import org.metabrainz.mobile.presentation.features.collection.CollectionActivity;
import org.metabrainz.mobile.view.DashTileView;

public class DashboardActivity extends MusicBrainzActivity implements OnClickListener {

    private static final int COLLECTION_LOGIN_REQUEST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);
        getSupportActionBar().setHomeButtonEnabled(false);
        setupTiles();
    }

    private void setupTiles() {
        setupTile(R.id.dash_scan, R.drawable.dash_scan, R.string.dash_scan);
        setupTile(R.id.dash_collections, R.drawable.dash_collections, R.string.dash_collections);
        setupTile(R.id.dash_donate, R.drawable.dash_donate, R.string.dash_donate);
        setupTile(R.id.dash_about, R.drawable.dash_about, R.string.dash_about);
    }

    private void setupTile(int tileId, int iconId, int stringId) {
        DashTileView scanTile = findViewById(tileId);
        scanTile.setIcon(iconId);
        scanTile.setText(stringId);
        scanTile.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isLoggedIn = App.isUserLoggedIn();
        menu.findItem(R.id.menu_login).setVisible(!isLoggedIn);
        return true;
    }


    public void updateWelcomeText() {
        try {
            WelcomeFragment f = (WelcomeFragment) getSupportFragmentManager().findFragmentById(R.id.welcome_fragment);
            f.updateText();
        } catch (Exception e) {
            // Fragment not attached, nothing to do.
        }
    }

    /*private void logOut() {
        Toast.makeText(this, R.string.toast_logged_out, Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dash_scan:
                IntentIntegrator.initiateScan(this, getString(R.string.zx_title), getString(R.string.zx_message),
                        getString(R.string.zx_pos), getString(R.string.zx_neg), IntentIntegrator.PRODUCT_CODE_TYPES);
                break;
            case R.id.dash_collections:
                Intent intent = new Intent(this, CollectionActivity.class);
                startActivity(intent);
                break;
            case R.id.dash_donate:
                startActivity(IntentFactory.getDonate(getApplicationContext()));
                break;
            case R.id.dash_about:
                startActivity(IntentFactory.getAbout(getApplicationContext()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.BARCODE_REQUEST) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult.getContents() != null) {
                Intent barcodeResult = new Intent(this, ReleaseActivity.class);
                barcodeResult.putExtra(Extra.BARCODE, scanResult.getContents());
                startActivity(barcodeResult);
            }
        } else if (requestCode == COLLECTION_LOGIN_REQUEST && App.isUserLoggedIn()) {
            //startActivity(IntentFactory.getCollectionList(getApplicationContext()));
        }
    }

}