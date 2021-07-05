package org.metabrainz.mobile.data.repository

import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection
import org.metabrainz.mobile.util.Resource

interface CollectionRepository {

    suspend fun fetchCollectionDetails(entity: String, id: String): Resource<String>

    suspend fun fetchCollections(editor: String, fetchPrivate: Boolean): Resource<MutableList<Collection>>
}