package org.metabrainz.mobile.presentation.features.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.net.Uri.parse
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import org.metabrainz.mobile.App
import org.metabrainz.mobile.R
import org.metabrainz.mobile.data.sources.Constants
import org.metabrainz.mobile.data.sources.api.entities.mbentity.MBEntityType
import org.metabrainz.mobile.databinding.ActivitySearchBinding
import org.metabrainz.mobile.presentation.IntentFactory
import org.metabrainz.mobile.presentation.features.adapters.ResultItem
import org.metabrainz.mobile.presentation.features.adapters.ResultItemComparator
import org.metabrainz.mobile.presentation.features.adapters.ResultPagingAdapter
import org.metabrainz.mobile.presentation.features.suggestion.SuggestionHelper
import org.metabrainz.mobile.presentation.features.suggestion.SuggestionProvider

class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private var suggestionHelper: SuggestionHelper? = null
    private var suggestionAdapter: CursorAdapter? = null

    private lateinit var binding: ActivitySearchBinding
    private var search_index = 0

    private var viewModel: SearchViewModel? = null
    private var adapter: ResultPagingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.app_bg)))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        setupSearchView()

        suggestionHelper = SuggestionHelper(this)
        suggestionAdapter = suggestionHelper!!.adapter
        binding.searchView.suggestionsAdapter = suggestionAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        binding.cardArtist.setOnClickListener {
            performAction(Constants.ADD_ARTIST)
        }

        binding.cardRelease.setOnClickListener {
            performAction(Constants.ADD_RELEASE)
        }
        binding.cardEvent.setOnClickListener {
            performAction(Constants.ADD_EVENT)
        }
        binding.cardReleaseGroup.setOnClickListener {
            performAction(Constants.ADD_RELEASEGROUP)
        }
        binding.cardLabel.setOnClickListener {
            performAction(Constants.ADD_LABEL)
        }

        binding.cardRecording.setOnClickListener {
            performAction(Constants.ADD_RECORDING)
        }

        val textArray = resources.getStringArray(R.array.searchType)
        for ((index, text) in textArray.withIndex()) {
            val chip = layoutInflater.inflate(R.layout.cat_chip_group_item_choice, binding.chipGroup, false) as Chip
            chip.text = text
            chip.isCloseIconVisible = false
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    search_index = index
                }
            }
            binding.chipGroup.addView(chip)
            if(index==0){
                binding.chipGroup.check(chip.id)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dash, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_login -> {
                startActivity(IntentFactory.getLogin(this))
                true
            }
            R.id.menu_preferences -> {
                startActivity(IntentFactory.getSettings(this))
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_open_website -> {
                performAction(App.WEBSITE_BASE_URL + "search?type=" + searchTypeFromSpinner!!.nameHere + "&query=" + binding.searchView.query.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performAction(url: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = parse(url)
        startActivity(intent)
    }

    private val searchTypeFromSpinner: MBEntityType? get() {
        return when (search_index) {
            0 -> MBEntityType.ARTIST
            1 -> MBEntityType.RELEASE
            2 -> MBEntityType.LABEL
            3 -> MBEntityType.RECORDING
            4 -> MBEntityType.EVENT
            5 -> MBEntityType.RELEASE_GROUP
            6 -> MBEntityType.INSTRUMENT
            else -> null
        }
    }

    private fun setupSearchView() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        binding.searchView.isSubmitButtonEnabled = true
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.setOnQueryTextListener(this)
        binding.searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = suggestionAdapter!!.getItem(position) as Cursor
                val query = cursor.getString(cursor.getColumnIndexOrThrow("display1"))
                binding.searchView.setQuery(query, false)
                return true
            }
        })
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        saveSearchSuggestion(query)

        adapter = ResultPagingAdapter(ResultItemComparator(), searchTypeFromSpinner!!)
        binding.recyclerView.adapter = adapter
        adapter!!.resetAnimation()
        viewModel!!.search(searchTypeFromSpinner, query).observe(this, { pagingData: PagingData<ResultItem> ->
            adapter!!.submitData(lifecycle, pagingData)
        })
        binding.recyclerView.visibility = VISIBLE
        binding.gridView.visibility = GONE

        binding.searchView.clearFocus()
        return true
    }

    private fun saveSearchSuggestion(query: String?) {
        val suggestions = SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
        suggestions.saveRecentQuery(query, null)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if(newText.isEmpty()){
            binding.recyclerView.visibility = GONE
            binding.gridView.visibility = VISIBLE
        }
        else {
            suggestionAdapter!!.changeCursor(suggestionHelper!!.getMatchingEntries(newText))
        }
        return false
    }
}
//1.Add Artist:   https://musicbrainz.org/artist/create?edit-artist2.Add Release: https://musicbrainz.org/release/add3.Add Event: https://musicbrainz.org/event/create?edit-event4.Add Release group:https://musicbrainz.org/release-group/create?edit-release-group5.Add label: https://musicbrainz.org/label/create?edit-label6.Add recording: https://musicbrainz.org/recording/create?edit-recording