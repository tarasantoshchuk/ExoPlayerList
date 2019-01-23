package com.tarasantoshchuk.exoplayerlist.view_holder

import android.view.TextureView
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer

abstract class TextureViewVH(itemView: View) : BaseViewHolder<SimpleExoPlayer, TextureView>(itemView) {
    override fun attachUiToPlayer(player: SimpleExoPlayer, playerView: TextureView) {
        player.setVideoTextureView(playerView)
    }

    override fun detachUiFromPlayer(player: SimpleExoPlayer, playerView: TextureView) {
        player.setVideoTextureView(null)
    }
}