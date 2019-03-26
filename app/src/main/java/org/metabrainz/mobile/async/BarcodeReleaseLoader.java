package org.metabrainz.mobile.async;

import java.io.IOException;

import org.metabrainz.mobile.api.MusicBrainz;
import org.metabrainz.mobile.api.data.obsolete.Release;
import org.metabrainz.mobile.api.data.obsolete.UserData;
import org.metabrainz.mobile.api.webservice.Entity;
import org.metabrainz.mobile.App;
import org.metabrainz.mobile.async.result.AsyncEntityResult;
import org.metabrainz.mobile.async.result.LoaderStatus;

public class BarcodeReleaseLoader extends PersistingAsyncTaskLoader<AsyncEntityResult<Release>> {

    private MusicBrainz client;
    private String barcode;

    public BarcodeReleaseLoader(String barcode) {
        client = App.getWebClient();
        this.barcode = barcode;
    }

    @Override
    public AsyncEntityResult<Release> loadInBackground() {
        try {
            return getAvailableData();
        } catch (Exception e) {
            return new AsyncEntityResult<Release>(LoaderStatus.EXCEPTION, e);
        }
    }

    private AsyncEntityResult<Release> getAvailableData() throws IOException {
        if (App.isUserLoggedIn()) {
            return getReleaseWithUserData();
        } else {
            return getRelease();
        }
    }

    private AsyncEntityResult<Release> getRelease() throws IOException {
        data = new AsyncEntityResult<Release>(LoaderStatus.SUCCESS, client.lookupReleaseUsingBarcode(barcode));
        return data;
    }

    private AsyncEntityResult<Release> getReleaseWithUserData() throws IOException {
        Release release = client.lookupReleaseUsingBarcode(barcode);
        UserData userData = client.lookupUserData(Entity.RELEASE_GROUP, release.getReleaseGroupMbid());
        data = new AsyncEntityResult<Release>(LoaderStatus.SUCCESS, release, userData);
        return data;
    }

}
