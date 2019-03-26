package org.metabrainz.mobile.async.result;

import org.metabrainz.mobile.api.data.obsolete.ArtistSearchResult;
import org.metabrainz.mobile.api.data.obsolete.ReleaseGroupSearchResult;

import java.util.List;

public class SearchResults {

    private final SearchType type;
    private final List<ArtistSearchResult> artistResults;
    private final List<ReleaseGroupSearchResult> releaseGroupResults;

    @SuppressWarnings("unchecked")
    public SearchResults(SearchType type, List<?> results) {
        if (type == SearchType.ARTIST) {
            artistResults = (List<ArtistSearchResult>) results;
            releaseGroupResults = null;
        } else {
            artistResults = null;
            releaseGroupResults = (List<ReleaseGroupSearchResult>) results;
        }
        this.type = type;
    }

    public SearchResults(List<ArtistSearchResult> artistResults, List<ReleaseGroupSearchResult> releaseGroupResults) {
        this.artistResults = artistResults;
        this.releaseGroupResults = releaseGroupResults;
        this.type = SearchType.ALL;
    }

    public SearchType getType() {
        return type;
    }

    public List<ArtistSearchResult> getArtistResults() {
        return artistResults;
    }

    public List<ReleaseGroupSearchResult> getReleaseGroupResults() {
        return releaseGroupResults;
    }

    public enum SearchType {
        ARTIST, RELEASE_GROUP, ALL
    }
}
