package org.metabrainz.mobile

import com.google.gson.Gson
import org.metabrainz.mobile.data.repository.CollectionRepository
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection
import org.metabrainz.mobile.util.Resource

class MockCollectionRepository : CollectionRepository {

    override suspend fun fetchCollectionDetails(entity: String, id: String): Resource<String> {
        return Resource(Resource.Status.SUCCESS, EntityTestUtils.loadResourceAsString(entity + "collection_details.json"))
    }

    override suspend fun fetchCollections(editor: String, fetchPrivate: Boolean): Resource<MutableList<Collection>> {
        val response = if (fetchPrivate)
            EntityTestUtils.loadResourceAsString("collections_private.json")
        else
            EntityTestUtils.loadResourceAsString("collections_public.json")

        val collections = Gson().fromJson(response, mutableListOf<Collection>()::class.java)
        return Resource(Resource.Status.SUCCESS, collections)
    }
}