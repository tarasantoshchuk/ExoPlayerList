package com.tarasantoshchuk.exoplayerlist.playback_config

import android.view.TextureView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.exoplayer2.SimpleExoPlayer
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter
import com.tarasantoshchuk.exoplayerlist.ViewProvider

class TextureViewConfig<P: SimpleExoPlayer, VH> : ExoPlayerAdapter.PlaybackConfig<P, VH>
    where VH: ViewHolder, VH: ViewProvider<TextureView>
{
    override fun onPlaybackGained(player: P, viewHolder: VH) {
        player.setVideoTextureView(viewHolder.playerView())
    }

    override fun onPlaybackLost(player: P, viewHolder: VH) {
        player.setVideoTextureView(null)
    }
}