package org.metabrainz.mobile.presentation.features.tagger

import android.net.Uri

interface OnItemCLickListener {
    fun onItemClicked(audioFile: AudioFile?,uri: Uri?)
}
