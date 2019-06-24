package org.metabrainz.mobile.presentation.features.release;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.metabrainz.mobile.R;
import org.metabrainz.mobile.data.sources.api.entities.Media;
import org.metabrainz.mobile.data.sources.api.entities.Track;
import org.metabrainz.mobile.presentation.IntentFactory;
import org.metabrainz.mobile.presentation.features.recording.RecordingActivity;

import java.util.List;

import static android.view.View.GONE;

class ReleaseTrackAdapter extends RecyclerView.Adapter {

    private static final int VIEWTYPE_TRACK = 0;
    private static final int VIEWTYPE_HEADING = 1;
    private final List<Media> mediaList;

    public ReleaseTrackAdapter(List<Media> data) {
        mediaList = data;
    }

    @Override
    public int getItemViewType(int position) {
        /*There are two view types to be displayed in the RecyclerView.
         * 1) The titles of mediums in which the tracks are present.
         * 2) The tracks itself.
         * To find which view type, the following method is used:
         * Check if the position is zero or equal to previous cumulative track count. This would mean
         * the position id for the title of a medium. Repeat this till all mediums are accounted for and
         * return the view type as a track.
         */
        int calculate = 0;
        int flag = VIEWTYPE_TRACK;
        for (Media media : mediaList) {
            if (calculate == position) {
                flag = VIEWTYPE_HEADING;
                break;
            }
            int trackCount = media.getTrackCount();
            calculate += trackCount;
            ++calculate;
        }
        return flag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == VIEWTYPE_TRACK) {
            View view = layoutInflater.inflate(R.layout.item_track, parent, false);
            return new TrackViewHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.item_track_heading, parent, false);
            return new TrackHeadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0)
            ((TrackViewHolder) holder).bind(position);
        else
            ((TrackHeadingViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        /*
         * Total count of items in recycler view is equal to cumulative track count of the media and
         * number of media because the titles of media of also make up one item each.*/
        int count = 0;
        if (mediaList != null) {
            for (Media medium : mediaList) count += medium.getTrackCount();
            count += mediaList.size();
        }
        return count;
    }

    private void setViewVisibility(String text, TextView view) {
        if (text != null && !text.isEmpty() && !text.equalsIgnoreCase("null")) {
            view.setVisibility(View.VISIBLE);
            view.setText(text);
        } else view.setVisibility(GONE);
    }

    private String getMediumTitle(int position) {
        /*
         * Deduct the track count of each media from the cumulative count. Reduce one from the count
         * for the already displayed medium's title. When the count becomes zero, the next media is
         * the one whose title has to be displayed.
         * */
        for (Media media : mediaList) {
            if (position == 0) {
                String format = media.getFormat();
                if (format == null || format.equalsIgnoreCase("null"))
                    format = "Medium";
                return format + "  " + media.getPosition() + " : " + media.getTitle();
            }
            position = position - media.getTrackCount();
            --position;
        }
        return "";
    }

    private Track getTrack(int position) {
        /*
         * To calculate track position, deduct the track count of all previous media that is till the
         * track count is positive. This way we get the track position in the required media and
         * then return it.*/
        for (Media media : mediaList) {
            --position;
            if (position < media.getTrackCount())
                return media.getTracks().get(position);
            position = position - media.getTrackCount();
        }
        return new Track();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        final TextView trackNumber;
        final TextView trackName;
        final TextView trackArtist;
        final TextView trackDuration;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNumber = itemView.findViewById(R.id.track_number);
            trackArtist = itemView.findViewById(R.id.track_artist);
            trackName = itemView.findViewById(R.id.track_name);
            trackDuration = itemView.findViewById(R.id.track_time);
        }

        void bind(int position) {
            Track item = getTrack(position);
            setViewVisibility(item.getTitle(), trackName);
            setViewVisibility(String.valueOf(item.getPosition()), trackNumber);
            setViewVisibility(item.getDuration(), trackDuration);
            setViewVisibility(item.getRecording().getDisplayArtist(), trackArtist);

            this.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RecordingActivity.class);
                intent.putExtra(IntentFactory.Extra.RECORDING, item.getRecording().getMbid());
                v.getContext().startActivity(intent);
            });
        }
    }

    class TrackHeadingViewHolder extends RecyclerView.ViewHolder {
        final TextView mediumTitle;

        TrackHeadingViewHolder(@NonNull View itemView) {
            super(itemView);
            mediumTitle = itemView.findViewById(R.id.medium_title);
        }

        void bind(int position) {
            setViewVisibility(getMediumTitle(position), mediumTitle);
        }
    }
}
