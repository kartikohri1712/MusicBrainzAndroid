package org.metabrainz.mobile.presentation.features.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import org.metabrainz.mobile.data.repository.CollectionRepository
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection
import org.metabrainz.mobile.data.sources.api.entities.mbentity.MBEntityType
import org.metabrainz.mobile.presentation.features.adapters.ResultItem
import org.metabrainz.mobile.presentation.features.adapters.ResultItemUtils
import org.metabrainz.mobile.util.Resource
import org.metabrainz.mobile.util.Resource.Status.FAILED
import org.metabrainz.mobile.util.Resource.Status.SUCCESS
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(val repository: CollectionRepository) : ViewModel() {

    fun fetchCollectionData(editor: String, fetchPrivate: Boolean): LiveData<Resource<MutableList<Collection>>> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(repository.fetchCollections(editor, fetchPrivate))
        }
    }

    fun fetchCollectionDetails(entity: MBEntityType, id: String): LiveData<Resource<List<ResultItem>>> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val result = repository.fetchCollectionDetails(entity.nameHere, id)
            emit(toResultItemsList(entity, result))
        }
    }

    private fun toResultItemsList(entity: MBEntityType, response: Resource<String>): Resource<List<ResultItem>> {
        return try {
            if (response.status == SUCCESS) {
                val resultItems = ResultItemUtils.getJSONResponseAsResultItemList(response.data, entity)
                return Resource(SUCCESS, resultItems)
            } else
                Resource(FAILED, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource(FAILED, null)
        }
    }

}