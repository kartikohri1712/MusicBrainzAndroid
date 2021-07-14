package org.metabrainz.mobile.presentation.features.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import org.metabrainz.mobile.data.sources.Constants
import org.metabrainz.mobile.data.sources.api.entities.mbentity.MBEntityType
import org.metabrainz.mobile.presentation.features.adapters.ResultItem

class SearchViewModel : ViewModel() {
    private val pagingConfig = PagingConfig(Constants.LIMIT,
            Constants.LIMIT / 5, false)

    fun search(entity: MBEntityType?, query: String?): LiveData<PagingData<ResultItem>> {
        val pager = Pager(pagingConfig, pagingSourceFactory={ SearchPagingSource(entity!!, query!!) })
        return pager.liveData.cachedIn(this.viewModelScope)
    }
}