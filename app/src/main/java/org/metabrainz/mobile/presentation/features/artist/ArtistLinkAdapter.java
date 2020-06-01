package org.metabrainz.mobile.presentation.features.artist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.metabrainz.mobile.R;
import org.metabrainz.mobile.data.sources.api.entities.Link;
import org.metabrainz.mobile.data.sources.api.entities.LinksClassifier;
import org.metabrainz.mobile.data.sources.api.entities.Url;
import org.metabrainz.mobile.databinding.ItemLinkBinding;

import java.util.List;

class ArtistLinkAdapter extends RecyclerView.Adapter<ArtistLinkAdapter.LinkViewHolder> implements View.OnClickListener {
    private final List<Link> links;
    private final Context context;

    public ArtistLinkAdapter(Context context, List<Link> links) {
        this.links = links;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistLinkAdapter.LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistLinkAdapter.LinkViewHolder holder, int position) {
        String type = links.get(position).getType();
        Drawable drawable = getLinkImage(type);

        holder.binding.linkImage.setImageDrawable(drawable);
        holder.binding.linkText.setText(type.toUpperCase());
        holder.itemView.setTag(R.id.link_image, links.get(position).getUrl());
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    @Override
    public void onClick(View v) {
        Url url = (Url) v.getTag(R.id.link_image);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url.getResource()));
        context.startActivity(intent);
    }

    private Drawable getLinkImage(String type) {
        Resources resources = context.getResources();
        switch (type) {
            case LinksClassifier.YOUTUBE:
                return resources.getDrawable(R.drawable.youtube_logo);
            case LinksClassifier.BANDCAMP:
                return resources.getDrawable(R.drawable.bandcamp_logo);
            case LinksClassifier.DISCOGS:
                return resources.getDrawable(R.drawable.discogs_logo);
            case LinksClassifier.IMDB:
                return resources.getDrawable(R.drawable.imdb_logo);
            case LinksClassifier.VIAF:
                return resources.getDrawable(R.drawable.viaf_logo);
            case LinksClassifier.MYSPACE:
                return resources.getDrawable(R.drawable.myspace_logo);
            default:
                return null;
        }
    }

    private Drawable getGenericLinkIcon(String type) {
        Resources resources = context.getResources();
        switch (type) {
            default:
                return resources.getDrawable(R.drawable.link_generic);
        }
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        ItemLinkBinding binding;

        LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemLinkBinding.bind(itemView);
        }
    }
}
