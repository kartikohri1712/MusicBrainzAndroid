package org.metabrainz.mobile.presentation.features.collection

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.metabrainz.mobile.data.sources.CollectionUtils.getCollectionEntityType
import org.metabrainz.mobile.data.sources.Constants
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection
import org.metabrainz.mobile.databinding.ItemCollectionBinding
import org.metabrainz.mobile.presentation.features.collection.CollectionListAdapter.CollectionViewHolder

internal class CollectionListAdapter(private val collections: List<Collection>) :
        RecyclerView.Adapter<CollectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return CollectionViewHolder(ItemCollectionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val collection = collections[position]
        holder.bind(collection)
    }

    override fun getItemCount(): Int {
        return collections.size
    }

    internal class CollectionViewHolder(private val binding: ItemCollectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(collection: Collection) {
            binding.collectionName.text = collection.name
            binding.collectionType.text = collection.type
            binding.collectionEntity.text = collection.entityType
            binding.collectionCount.text = collection.count.toString()
            itemView.setOnClickListener { v: View ->
                val intent = Intent(v.context, CollectionDetailsActivity::class.java)
                intent.putExtra(Constants.MBID, collection.mbid)
                intent.putExtra(Constants.TYPE, getCollectionEntityType(collection))
                v.context.startActivity(intent)
            }
        }
    }
}