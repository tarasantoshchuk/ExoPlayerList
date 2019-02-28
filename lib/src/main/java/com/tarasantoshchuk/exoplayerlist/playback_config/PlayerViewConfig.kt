package com.tarasantoshchuk.exoplayerlist.playback_config

import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter
import com.tarasantoshchuk.exoplayerlist.ViewProvider

class PlayerViewConfig<P: ExoPlayer, VH> : ExoPlayerAdapter.PlaybackConfig<P, VH>
        where VH : RecyclerView.ViewHolder, VH: ViewProvider<PlayerView> {

    override fun onPlaybackGained(player: P, viewHolder: VH) {
        viewHolder.playerView().player = player
    }

    override fun onPlaybackLost(player: P, viewHolder: VH) {
        viewHolder.playerView().player = null
    }

    override fun onSwitchPlayback(player: P, viewHolder: VH, newViewHolder: VH) {
        PlayerView.switchTargetView(player, viewHolder.playerView(), newViewHolder.playerView())
    }
}