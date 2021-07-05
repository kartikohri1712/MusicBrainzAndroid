package org.metabrainz.mobile.presentation.features.tagger

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import org.metabrainz.mobile.data.sources.Constants
import org.metabrainz.mobile.databinding.FragmentTaggerBinding
import org.metabrainz.mobile.presentation.features.recording.RecordingActivity
import org.metabrainz.mobile.util.Log.d
import org.metabrainz.mobile.util.Resource
import org.metabrainz.mobile.util.Utils
import java.util.concurrent.TimeUnit

class TaggerFragment : Fragment() {

    private lateinit var binding: FragmentTaggerBinding
    private val viewModel: TaggerViewModel by activityViewModels()
    private var recordingMBID: String? = null
    private var releaseMBID: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTaggerBinding.inflate(inflater)

        viewModel.serverFetchedMetadata.observe(viewLifecycleOwner) {resource->
            //Making the required changes if  the results were found
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    setServerFetchedMetadata(resource.data!!)
                }
                Resource.Status.LOADING -> {
                    d("Loading the server data...")
                }
                Resource.Status.FAILED -> {
                    Toast.makeText(context,"Error fetching data from server!",LENGTH_SHORT).show()
                }
            }
        }

        viewModel.taglibFetchedMetadata.observe(viewLifecycleOwner) {
            setTaglibFetchedMetadata(it)
        }

        viewModel.serverCoverArt.observe(viewLifecycleOwner) { resource ->
            //Handling the status of the api call
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    Glide.with(this)
                        .load(resource.data!!.images[0].thumbnails.small)
                        .into(binding.albumArtServer)
                }
                Resource.Status.LOADING -> {
                    d("Loading the cover art...")
                }
                Resource.Status.FAILED -> {
                    Toast.makeText(context,"Error fetching cover art from server!",LENGTH_SHORT).show()
                }
            }
        }

        binding.overwriteTagsButton.setOnClickListener {
            saveMetadata()
        }

        binding.recordingButton.setOnClickListener {
            val intent = Intent(context, RecordingActivity::class.java)
            intent.putExtra(Constants.MBID, recordingMBID)
            context?.startActivity(intent)
        }

        binding.picard.setOnClickListener {
           Utils.sendToPicard(requireContext(),releaseMBID!!)
        }

        return binding.root
    }


    private fun setTaglibFetchedMetadata(metadata: AudioFile?) {
        binding.loadingAnimation.root.visibility = View.VISIBLE
        binding.taglibFetched.originalValue.visibility = View.GONE
        binding.serverFetched.newValue.visibility = View.GONE
        binding.overwriteTagsButton.visibility = View.GONE
        binding.serverFetched.root.visibility = View.GONE
        binding.taglibFetched.root.visibility = View.GONE
        binding.AcoustID.visibility = View.GONE
        binding.AcoustIDHeading.visibility = View.GONE
        binding.MBID.visibility = View.GONE
        binding.MBIDHeading.visibility = View.GONE
        binding.albumArtLocal.visibility = View.GONE
        binding.albumArtServer.visibility = View.GONE
        binding.recordingButton.visibility = View.GONE
        binding.picard.visibility = View.GONE

        reset()

        GlideApp.with(binding.albumArtLocal)
            .load(metadata)
            .into(binding.albumArtLocal)

        binding.taglibFetched.title.setText(metadata!!.allProperties["TITLE"])
        binding.serverFetched.title.setText(metadata.allProperties["TITLE"])

        binding.taglibFetched.track.setText(metadata.allProperties["TRACK"])
        binding.serverFetched.track.setText(metadata.allProperties["TRACK"])

        binding.taglibFetched.disc.setText(metadata.allProperties["DISC"])
        binding.serverFetched.disc.setText(metadata.allProperties["DISC"])

        binding.serverFetched.duration.setText(metadata.allProperties["DURATION"]?.toInt()?.toHms())
        binding.taglibFetched.duration.setText(metadata.allProperties["DURATION"]?.toInt()?.toHms())

        binding.serverFetched.artist.setText(metadata.allProperties["ARTIST"])
        binding.taglibFetched.artist.setText(metadata.allProperties["ARTIST"])

        binding.serverFetched.album.setText(metadata.allProperties["ALBUM"])
        binding.taglibFetched.album.setText(metadata.allProperties["ALBUM"])

        binding.serverFetched.year.setText(metadata.allProperties["DATE"])
        binding.taglibFetched.year.setText(metadata.allProperties["DATE"])
    }

    private fun setServerFetchedMetadata(tagsList: List<TagField>) {
        binding.loadingAnimation.root.visibility = View.GONE
        binding.taglibFetched.originalValue.visibility = View.VISIBLE
        binding.serverFetched.newValue.visibility = View.VISIBLE
        binding.overwriteTagsButton.visibility = View.VISIBLE
        binding.serverFetched.root.visibility = View.VISIBLE
        binding.taglibFetched.root.visibility = View.VISIBLE
        binding.albumArtLocal.visibility = View.VISIBLE
        binding.albumArtServer.visibility = View.VISIBLE
        binding.recordingButton.visibility = View.VISIBLE
        binding.picard.visibility = View.VISIBLE

        for (tags in tagsList) {
            if (tags.newValue.isEmpty()) {
                continue
            }

            when (tags.tagName) {
                "TITLE" -> binding.serverFetched.title.setText(tags.newValue)
                "ARTIST" -> binding.serverFetched.artist.setText(tags.newValue)
                "ALBUM" -> binding.serverFetched.album.setText(tags.newValue)
                "TRACKNUMBER" -> binding.serverFetched.track.setText(tags.newValue)
//                "discnumber" -> binding.serverFetched.disc2.setText(tags.newValue)
                "DATE" -> binding.serverFetched.year.setText(tags.newValue)
                "MUSICBRAINZ_TRACKID" -> {
                    binding.MBID.text = tags.newValue
                    binding.MBID.visibility = View.VISIBLE
                    binding.MBIDHeading.visibility = View.VISIBLE
                }
                "MUSICBRAINZ_RELEASEID"->{
                    releaseMBID = tags.newValue
                }
                "MUSICBRAINZ_RECORDINGID" ->{
                    recordingMBID = tags.newValue
                }
                "acoustid_id" -> {
                    binding.AcoustID.text = tags.newValue
                    binding.AcoustID.visibility = View.VISIBLE
                    binding.AcoustIDHeading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun reset(){
        binding.serverFetched.title.setText("")
        binding.taglibFetched.title.setText("")

        binding.serverFetched.track.setText("")
        binding.taglibFetched.track.setText("")

        binding.serverFetched.disc.setText("")
        binding.taglibFetched.disc.setText("")

        binding.serverFetched.duration.setText("")
        binding.taglibFetched.duration.setText("")

        binding.serverFetched.artist.setText("")
        binding.taglibFetched.artist.setText("")

        binding.serverFetched.album.setText("")
        binding.taglibFetched.album.setText("")

        binding.serverFetched.year.setText("")
        binding.taglibFetched.year.setText("")
    }

    private fun saveMetadata() {
        val newMetadata = HashMap<String, String>()
        if (binding.serverFetched.title.text.toString().isNotEmpty())
            newMetadata["TITLE"] = binding.serverFetched.title.text.toString()

        if (binding.serverFetched.track.text.toString().isNotEmpty())
            newMetadata["TRACK"] = binding.serverFetched.track.text.toString()

        if (binding.serverFetched.disc.text.toString().isNotEmpty())
            newMetadata["DISC"] = binding.serverFetched.disc.text.toString()

        if (binding.serverFetched.album.text.toString().isNotEmpty())
            newMetadata["ALBUM"] = binding.serverFetched.album.text.toString()

        if (binding.serverFetched.artist.text.toString().isNotEmpty())
            newMetadata["ARTIST"] = binding.serverFetched.artist.text.toString()

        if (binding.MBID.text.toString().isNotEmpty())
            newMetadata["MBID"] = binding.MBID.text.toString()

        if (binding.serverFetched.year.text.toString().isNotEmpty())
            newMetadata["DATE"] = binding.serverFetched.year.text.toString()

        if (viewModel.saveMetadataTags(newMetadata))
            Toast.makeText(activity, "Saved Tags Successfully!", LENGTH_SHORT).show()
        else
            Toast.makeText(activity, "Saving Tags Failed!", LENGTH_SHORT).show()
    }

    private fun Int.toHms(defaultValue: String? = null): String {
        if (this == 0 && defaultValue != null) {
            return defaultValue
        }
        val hours = TimeUnit.MILLISECONDS.toHours(this.toLong())
        val minutes = TimeUnit.MILLISECONDS.toMinutes(this.toLong()) % TimeUnit.HOURS.toMinutes(1)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(this.toLong()) % TimeUnit.MINUTES.toSeconds(1)

        return if (hours == 0L) {
            String.format("%2d:%02d", minutes, seconds)
        }
        else {
            String.format("%2d:%02d:%02d", hours, minutes, seconds)
        }
    }

}