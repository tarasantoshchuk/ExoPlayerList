package com.tarasantoshchuk.exoplayerlist.view_holder

import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

abstract class PlayerViewVH<P: ExoPlayer>(itemView: View): BaseViewHolder<P, PlayerView>(itemView) {
    override fun attachUiToPlayer(player: P, playerView: PlayerView) {
        playerView.player = player
    }

    override fun detachUiFromPlayer(player: P, playerView: PlayerView) {
        playerView.player = null
    }

    override fun switchPlayerUi(player: P, previousPlayerView: PlayerView, playerView: PlayerView) {
        PlayerView.switchTargetView(player, previousPlayerView, playerView)
    }
}