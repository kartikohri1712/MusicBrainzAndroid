package org.metabrainz.mobile.presentation.features.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.JsonParser
import org.metabrainz.mobile.data.sources.api.MusicBrainzServiceGenerator
import org.metabrainz.mobile.data.sources.api.SearchService
import org.metabrainz.mobile.data.sources.api.entities.mbentity.MBEntityType
import org.metabrainz.mobile.presentation.features.adapters.ResultItem
import org.metabrainz.mobile.presentation.features.adapters.ResultItemUtils

class SearchPagingSource(val entity: MBEntityType, val query: String) : PagingSource<Int, ResultItem>() {

    private val service = MusicBrainzServiceGenerator.createService(SearchService::class.java, true)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultItem> {
        val pageSize: Int = params.loadSize
        val offset = params.key ?: 0
        return try {
            val response = service.searchEntity(entity.display, query, pageSize, offset)?.string()
            var count = LoadResult.Page.COUNT_UNDEFINED
            if (offset == 0) {
                val responseObject = JsonParser.parseString(response)
                count = responseObject.asJsonObject.get("count").asInt
            }
            val itemsAfter = if (count == LoadResult.Page.COUNT_UNDEFINED) count
            else (count - offset - pageSize).coerceAtLeast(0)
            // itemsAfter is required to be at least otherwise the current page will be not loaded

            LoadResult.Page(
                    data = ResultItemUtils.getJSONResponseAsResultItemList(response, entity),
                    prevKey = null,
                    nextKey = pageSize + offset,
                    itemsAfter = itemsAfter
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ResultItem>): Int? {
        TODO("Not yet implemented")
    }
}