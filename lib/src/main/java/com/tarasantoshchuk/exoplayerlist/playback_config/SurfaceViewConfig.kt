package com.tarasantoshchuk.exoplayerlist.playback_config

import android.view.SurfaceView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.exoplayer2.SimpleExoPlayer
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter
import com.tarasantoshchuk.exoplayerlist.ViewProvider

class SurfaceViewConfig<P: SimpleExoPlayer, VH> : ExoPlayerAdapter.PlaybackConfig<P, VH>
        where VH: ViewHolder, VH: ViewProvider<SurfaceView>
{
    override fun onPlaybackGained(player: P, viewHolder: VH) {
        player.setVideoSurfaceView(viewHolder.playerView())
    }

    override fun onPlaybackLost(player: P, viewHolder: VH) {
        player.setVideoSurfaceView(null)
    }
}