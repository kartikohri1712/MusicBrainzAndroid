package org.metabrainz.mobile

import com.google.gson.Gson
import org.metabrainz.mobile.EntityTestUtils.loadResourceAsString
import org.metabrainz.mobile.data.repository.LookupRepository
import org.metabrainz.mobile.data.sources.api.entities.CoverArt
import org.metabrainz.mobile.data.sources.api.entities.WikiSummary
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Recording
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Release
import org.metabrainz.mobile.util.Resource
import org.metabrainz.mobile.util.Resource.Status.SUCCESS

class MockLookupRepository : LookupRepository {

    override suspend fun fetchData(entity: String, MBID: String, params: String?): Resource<String> {
        return Resource(SUCCESS, loadResourceAsString(entity + "_lookup.json"))
    }

    override suspend fun fetchWikiSummary(string: String, method: Int): Resource<WikiSummary> {
        val response = loadResourceAsString("artist_wiki.json")
        val summary = Gson().fromJson(response, WikiSummary::class.java)
        return Resource(SUCCESS, summary)
    }

    override suspend fun fetchCoverArt(MBID: String): Resource<CoverArt> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchRecordings(query: String?): Resource<List<Recording>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMatchedRelease(MBID: String?): Resource<Release> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAcoustIDResults(duration: Long, fingerprint: String?): Resource<List<Recording>> {
        TODO("Not yet implemented")
    }
}