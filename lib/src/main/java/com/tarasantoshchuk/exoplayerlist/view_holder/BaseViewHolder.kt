package com.tarasantoshchuk.exoplayerlist.view_holder

import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter
import com.tarasantoshchuk.exoplayerlist.cast

abstract class BaseViewHolder<P: ExoPlayer, PV: Any>(itemView: View): ExoPlayerAdapter.ViewHolder<P>(itemView) {
    abstract fun getPlayerView(): PV

    final override fun attachUiToPlayer(player: P) {
        attachUiToPlayer(player, getPlayerView())
        super.attachUiToPlayer(player)
    }

    final override fun detachUiFromPlayer(player: P) {
        super.detachUiFromPlayer(player)
        detachUiFromPlayer(player, getPlayerView())
    }

    final override fun switchPlayerUi(player: P, previousPlaybackHolder: ExoPlayerAdapter.ViewHolder<P>) {
        previousPlaybackHolder.onPlaybackLost(player)

        switchPlayerUi(player, cast(previousPlaybackHolder).getPlayerView(), getPlayerView())

        onPlaybackGained(player)
    }

    abstract fun attachUiToPlayer(player: P, playerView: PV)
    abstract fun detachUiFromPlayer(player: P, playerView: PV)

    open fun switchPlayerUi(player: P, previousPlayerView: PV, playerView: PV) {
        detachUiFromPlayer(player, previousPlayerView)
        attachUiToPlayer(player, playerView)
    }
}