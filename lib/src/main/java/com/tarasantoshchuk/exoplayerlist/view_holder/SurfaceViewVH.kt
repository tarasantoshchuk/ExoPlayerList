package com.tarasantoshchuk.exoplayerlist.view_holder

import android.view.SurfaceView
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer

abstract class SurfaceViewVH(itemView: View) : BaseViewHolder<SimpleExoPlayer, SurfaceView>(itemView) {
    override fun attachUiToPlayer(player: SimpleExoPlayer, playerView: SurfaceView) {
        player.setVideoSurfaceView(playerView)
    }

    override fun detachUiFromPlayer(player: SimpleExoPlayer, playerView: SurfaceView) {
        player.setVideoSurfaceView(null)
    }
}