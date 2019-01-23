package com.tarasantoshchuk.exoplayerlist

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource

abstract class ExoPlayerAdapter<P: ExoPlayer, VH : ExoPlayerAdapter.ViewHolder<P>>(val context: Context, val config: Config<P>) :
    RecyclerView.Adapter<VH>() {

    data class Config<P: Player>(
        val pausePlaybackOnScroll: Boolean = false,
        val minVisibilityToContinuePlayback: Int = 30,
        val playerProvider: (RecyclerView) -> P
    )

    private lateinit var player: P
    private var playbackHolder: VH? = null
    private var nextPlaybackHolder: VH? = null
    private var fullScreenViewHolder: VH? = null

    //todo - use
    private lateinit var recyclerView: RecyclerView

    private val switchPlayback = Runnable {
        switchPlayback(nextPlaybackHolder!!)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        player = config.playerProvider(recyclerView)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @Suppress("UNCHECKED_CAST")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var newPlaybackHolder: VH? = null
                var maxVisibilityPercent = 0f

                recyclerView.forEach {
                    val viewHolder = recyclerView.getChildViewHolder(it) as VH

                    if (maxVisibilityPercent < viewHolder.getVisibilityPercent()) {
                        newPlaybackHolder = viewHolder
                        maxVisibilityPercent = viewHolder.getVisibilityPercent()
                    }
                }

                stopCurrentPlayback()
                scheduleNextPlayback(newPlaybackHolder, recyclerView)

                Log.v("AEROL", "onScrolled")
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (config.pausePlaybackOnScroll) {
                    player.playWhenReady = newState == RecyclerView.SCROLL_STATE_IDLE
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE && nextPlaybackHolder != playbackHolder) {
                    cancelSwitchPlayback(recyclerView)
                    switchPlayback.run()
                }

                Log.v("AEROL", "onScrollStateChanged")
            }
        })
    }

    fun isFullscreen(): Boolean {
        return fullScreenViewHolder != null
    }

    fun enterFullScreen(viewHolder: VH = playbackHolder!!, onTransitionToFullScreen: (Player, VH) -> Unit) {
        fullScreenViewHolder = viewHolder

        if (playbackHolder != viewHolder) {
            switchPlayback(viewHolder)
        }

        onTransitionToFullScreen(player, fullScreenViewHolder!!)
    }

    fun exitFullScreen(onTransitionFromFullScreen: (Player, VH) -> Unit) {
        onTransitionFromFullScreen(player, fullScreenViewHolder!!)

        fullScreenViewHolder = null
    }

    private fun scheduleNextPlayback(newPlaybackHolder: VH?, recyclerView: RecyclerView) {
        if (newPlaybackHolder != nextPlaybackHolder) {
            nextPlaybackHolder = newPlaybackHolder
            rescheduleSwitchPlayback(recyclerView)
        }
    }

    private fun stopCurrentPlayback() {
        playbackHolder?.apply {
            if (getVisibilityPercent() < config.minVisibilityToContinuePlayback) {
                detachUiFromPlayer(player)
                playbackHolder = null
            }
        }
    }

    private fun rescheduleSwitchPlayback(recyclerView: RecyclerView) {
        cancelSwitchPlayback(recyclerView)
        scheduleSwitchPlayback(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        cancelSwitchPlayback(recyclerView)

        playbackHolder?.detachUiFromPlayer(player)

        player.release()
    }

    private fun cancelSwitchPlayback(recyclerView: RecyclerView) {
        recyclerView.removeCallbacks(switchPlayback)
    }

    private fun scheduleSwitchPlayback(recyclerView: RecyclerView) {
        recyclerView.postDelayed(switchPlayback, 300L)
    }

    private fun switchPlayback(newPlaybackHolder: VH) {
        player.prepare(newPlaybackHolder.getMediaSource())

        playbackHolder.let {

            if (it == null) {
                newPlaybackHolder.attachUiToPlayer(player)
                player.playWhenReady = true
            } else {
                newPlaybackHolder.switchPlayerUi(player, it)
            }
        }

        playbackHolder = newPlaybackHolder
    }

    abstract class ViewHolder<P: Player>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @CallSuper
        open fun attachUiToPlayer(player: P) {
            onPlaybackGained(player)
        }

        @CallSuper
        open fun detachUiFromPlayer(player: P) {
            onPlaybackLost(player)
        }

        open fun onPlaybackGained(player: P) {
        }

        open fun onPlaybackLost(player: P) {
        }

        open fun switchPlayerUi(player: P, previousPlaybackHolder: ViewHolder<P>) {
            previousPlaybackHolder.detachUiFromPlayer(player)
            attachUiToPlayer(player)
        }

        abstract fun getMediaSource(): MediaSource


        open fun getVisibilityPercent(): Float {
            return itemView.getVisibilityPercent()
        }
    }
}

fun View.getVisibilityPercent(): Float {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.width().toFloat() / width * rect.height() / height * 100f
}
