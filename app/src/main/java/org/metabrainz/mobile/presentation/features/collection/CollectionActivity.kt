package org.metabrainz.mobile.presentation.features.collection

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.metabrainz.mobile.R
import org.metabrainz.mobile.data.sources.CollectionUtils.removeCollections
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection
import org.metabrainz.mobile.databinding.ActivityCollectionBinding
import org.metabrainz.mobile.presentation.UserPreferences.privateCollectionsPreference
import org.metabrainz.mobile.presentation.features.base.MusicBrainzActivity
import org.metabrainz.mobile.presentation.features.dashboard.DashboardActivity
import org.metabrainz.mobile.presentation.features.login.LoginActivity
import org.metabrainz.mobile.presentation.features.login.LoginSharedPreferences
import org.metabrainz.mobile.presentation.features.login.LoginSharedPreferences.loginStatus
import org.metabrainz.mobile.presentation.features.login.LoginSharedPreferences.username
import org.metabrainz.mobile.util.Resource
import java.util.*

@AndroidEntryPoint
class CollectionActivity : MusicBrainzActivity() {
    private lateinit var binding: ActivityCollectionBinding
    private var viewModel: CollectionViewModel? = null
    private var adapter: CollectionListAdapter? = null
    private var collections: MutableList<Collection>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar(binding)
        viewModel = ViewModelProvider(this).get(CollectionViewModel::class.java)
        collections = ArrayList()
        adapter = CollectionListAdapter(collections!!)
        if (loginStatus == LoginSharedPreferences.STATUS_LOGGED_IN) {
            binding.loginRequired.visibility = View.GONE
            binding.noResult.root.visibility = View.GONE
            binding.progressSpinner.root.visibility = View.VISIBLE
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            val itemDecoration = DividerItemDecoration(
                    binding.recyclerView.context, DividerItemDecoration.VERTICAL)
            binding.recyclerView.addItemDecoration(itemDecoration)
            binding.recyclerView.visibility = View.GONE
            val getPrivateCollections = (loginStatus == LoginSharedPreferences.STATUS_LOGGED_IN
                    && privateCollectionsPreference)
            viewModel!!.fetchCollectionData(username!!,
                    getPrivateCollections).observe(this, { resource: Resource<MutableList<Collection>>? -> setCollections(resource) })
        } else {
            binding.noResult.root.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            binding.progressSpinner.root.visibility = View.GONE
            binding.loginRequired.visibility = View.GONE
            callAlert()
        }
    }

    override fun onResume() {
        super.onResume()
        if (loginStatus == LoginSharedPreferences.STATUS_LOGGED_OUT) {
            collections!!.clear()
            checkHasResults()
            binding.noResult.root.visibility = View.GONE
        }
    }

    private fun checkHasResults() {
        binding.progressSpinner.root.visibility = View.GONE
        if (adapter!!.itemCount == 0) {
            binding.recyclerView.visibility = View.GONE
            binding.noResult.root.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noResult.root.visibility = View.GONE
        }
    }

    fun callAlert() {
        val builder = AlertDialog.Builder(this, R.style.loginrequiredDialog)
        builder.setTitle("Login Required")
        builder.setMessage("You need to log in to see your collections")
        builder.setPositiveButton("Login") { dialog: DialogInterface?, which: Int ->
            startActivity(Intent(this@CollectionActivity, LoginActivity::class.java))
            finish()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
            startActivity(Intent(this@CollectionActivity, DashboardActivity::class.java))
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dash, menu)
        menu.findItem(R.id.menu_open_website).isVisible = false
        return true
    }

    override fun getBrowserURI(): Uri {
        return Uri.EMPTY
    }

    private fun setCollections(resource: Resource<MutableList<Collection>>?) {
        if (resource != null && resource.status === Resource.Status.SUCCESS) {
            val data = resource.data!!
            removeCollections(data)
            collections!!.clear()
            collections!!.addAll(data)
            adapter!!.notifyDataSetChanged()
        }
        checkHasResults()
    }
}