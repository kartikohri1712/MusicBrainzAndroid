package org.metabrainz.mobile.data.repository

import org.metabrainz.mobile.data.sources.api.entities.CoverArt
import org.metabrainz.mobile.data.sources.api.entities.WikiSummary
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Recording
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Release
import org.metabrainz.mobile.util.Resource

interface LookupRepository {

    suspend fun fetchData(entity: String, MBID: String, params: String?): Resource<String>

    suspend fun fetchWikiSummary(string: String, method: Int): Resource<WikiSummary>

    suspend fun fetchCoverArt(MBID: String): Resource<CoverArt>

    suspend fun fetchRecordings(query: String?): Resource<List<Recording>>

    suspend fun fetchMatchedRelease(MBID: String?): Resource<Release>

    suspend fun fetchAcoustIDResults(duration: Long, fingerprint: String?): Resource<List<Recording>>

    companion object {
        const val METHOD_WIKIPEDIA_URL = 0
        const val METHOD_WIKIDATA_ID = 1
    }
}