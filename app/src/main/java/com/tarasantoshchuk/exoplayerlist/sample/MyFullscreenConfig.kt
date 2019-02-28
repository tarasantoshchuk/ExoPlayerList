package com.tarasantoshchuk.exoplayerlist.sample

import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter

class MyFullscreenConfig(private val fullscreenPlayer: PlayerView): ExoPlayerAdapter.FullscreenConfig<SimpleExoPlayer, Adapter.TestViewHolder> {
    override fun onTransitionToFullScreen(player: SimpleExoPlayer, viewHolder: Adapter.TestViewHolder) {
        PlayerView.switchTargetView(player, viewHolder.playerUI, fullscreenPlayer)
        fullscreenPlayer.visibility = View.VISIBLE
    }

    override fun onTransitionFromFullScreen(player: SimpleExoPlayer, viewHolder: Adapter.TestViewHolder) {
        PlayerView.switchTargetView(player, fullscreenPlayer, viewHolder.playerUI)
        fullscreenPlayer.visibility = View.VISIBLE
    }
}