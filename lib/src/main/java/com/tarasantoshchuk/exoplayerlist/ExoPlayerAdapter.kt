package com.tarasantoshchuk.exoplayerlist

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource

abstract class ExoPlayerAdapter<P : ExoPlayer, VH : ViewHolder>(val config: Config<P, VH>) :
    RecyclerView.Adapter<VH>() {

    @JvmOverloads
    constructor(
        playerConfig: PlayerConfig<P>,
        playbackConfig: PlaybackConfig<P, VH>,
        fullscreenConfig: FullscreenConfig<P, VH> = FullscreenConfig.unsupported(),
        scrollConfig: ScrollConfig<VH> = ScrollConfig.default()
    ) : this(ConcreteConfig<P, VH>(playerConfig, playbackConfig, fullscreenConfig, scrollConfig))

    interface Config<P : Player, VH : ViewHolder> :
            PlaybackConfig<P, VH>,
            FullscreenConfig<P, VH>,
            PlayerConfig<P>,
            ScrollConfig<VH>

    interface PlaybackConfig<P : Player, VH : ViewHolder> {
        fun onPlaybackGained(player: P, viewHolder: VH)
        fun onPlaybackLost(player: P, viewHolder: VH)
        fun onSwitchPlayback(player: P, viewHolder: VH, newViewHolder: VH) {
            onPlaybackLost(player, viewHolder)
            onPlaybackGained(player, newViewHolder)
        }
    }

    interface FullscreenConfig<P : Player, VH : ViewHolder> {
        private object UNSUPPORTED : FullscreenConfig<Player, ViewHolder> {
            override fun onTransitionToFullScreen(player: Player, viewHolder: ViewHolder) {
                throw UnsupportedOperationException("Fullscreen functionality is not supported")
            }

            override fun onTransitionFromFullScreen(player: Player, viewHolder: ViewHolder) {
                throw UnsupportedOperationException("Fullscreen functionality is not supported")
            }
        }

        companion object {
            @Suppress("UNCHECKED_CAST")
            fun <P: Player, VH: ViewHolder> unsupported(): FullscreenConfig<P, VH> {
                return UNSUPPORTED as FullscreenConfig<P, VH>
            }
        }

        fun onTransitionToFullScreen(player: P, viewHolder: VH)
        fun onTransitionFromFullScreen(player: P, viewHolder: VH)
    }

    interface PlayerConfig<P : Player> {
        fun createPlayer(context: Context): P

        fun init(context: Context) {}
        fun createMediaSource(position: Int): MediaSource
        fun release() {}
    }

    interface ScrollConfig<VH : ViewHolder> {
        private object DEFAULT : ScrollConfig<ViewHolder> {
            override fun getVisibilityPercent(viewHolder: ViewHolder): Float {
                return viewHolder.itemView.getVisibilityPercent()
            }

            override fun minVisibilityToContinuePlayback(): Float {
                return 50f
            }
        }

        companion object {
            @Suppress("UNCHECKED_CAST")
            fun <VH: ViewHolder> default(): ScrollConfig<VH> {
                return DEFAULT as ScrollConfig<VH>
            }
        }

        fun getVisibilityPercent(viewHolder: VH): Float
        fun pausePlaybackOnScroll() = true
        fun minVisibilityToContinuePlayback(): Float
    }

    private class ConcreteConfig<P : Player, VH : ViewHolder>(
        private val playerConfig: PlayerConfig<P>,
        private val playbackConfig: PlaybackConfig<P, VH>,
        private val fullscreenConfig: FullscreenConfig<P, VH>,
        private val scrollConfig: ScrollConfig<VH>) :

        PlayerConfig<P> by playerConfig,
        PlaybackConfig<P, VH> by playbackConfig,
        FullscreenConfig<P, VH> by fullscreenConfig,
        ScrollConfig<VH> by scrollConfig,
        Config<P, VH>


    private lateinit var player: P
    private var playbackHolder: VH? = null
    private var nextPlaybackHolder: VH? = null
    private var fullScreenViewHolder: VH? = null

    private val switchPlayback = Runnable {
        switchPlayback(nextPlaybackHolder!!)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        player = config.createPlayer(recyclerView.context)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @Suppress("UNCHECKED_CAST")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                var newPlaybackHolder: VH? = null
                var maxVisibilityPercent = 0f

                recyclerView.forEach { view ->
                    val viewHolder = recyclerView.getChildViewHolder(view) as VH

                    config.getVisibilityPercent(viewHolder)
                        .takeIf { it > maxVisibilityPercent }
                        ?.let {
                            newPlaybackHolder = viewHolder
                            maxVisibilityPercent = it
                        }
                }

                stopCurrentPlayback()
                scheduleNextPlayback(newPlaybackHolder, recyclerView)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (config.pausePlaybackOnScroll()) {
                    player.playWhenReady = newState == RecyclerView.SCROLL_STATE_IDLE
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE && nextPlaybackHolder != playbackHolder) {
                    cancelSwitchPlayback(recyclerView)
                    switchPlayback.run()
                }
            }
        })
    }

    fun isFullscreen(): Boolean {
        return fullScreenViewHolder != null
    }

    fun enterFullScreen(viewHolder: VH = playbackHolder!!) {
        fullScreenViewHolder = viewHolder

        if (playbackHolder != viewHolder) {
            switchPlayback(viewHolder)
        }

        config.onTransitionToFullScreen(player, fullScreenViewHolder!!)
    }

    fun exitFullScreen() {
        config.onTransitionFromFullScreen(player, fullScreenViewHolder!!)

        fullScreenViewHolder = null
    }

    private fun scheduleNextPlayback(newPlaybackHolder: VH?, recyclerView: RecyclerView) {
        if (newPlaybackHolder != nextPlaybackHolder) {
            nextPlaybackHolder = newPlaybackHolder
            rescheduleSwitchPlayback(recyclerView)
        }
    }

    private fun stopCurrentPlayback() {
        playbackHolder?.let {
            if (config.getVisibilityPercent(it) < config.minVisibilityToContinuePlayback()) {
                config.onPlaybackLost(player, it)
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

        playbackHolder?.let { config.onPlaybackLost(player, it) }

        player.release()
    }

    private fun cancelSwitchPlayback(recyclerView: RecyclerView) {
        recyclerView.removeCallbacks(switchPlayback)
    }

    private fun scheduleSwitchPlayback(recyclerView: RecyclerView) {
        recyclerView.postDelayed(switchPlayback, 300L)
    }

    private fun switchPlayback(newPlaybackHolder: VH) {
        player.prepare(config.createMediaSource(newPlaybackHolder.adapterPosition))

        playbackHolder.let {

            if (it == null) {
                config.onPlaybackGained(player, newPlaybackHolder)
                player.playWhenReady = true
            } else {
                config.onSwitchPlayback(player, it, newPlaybackHolder)
            }
        }

        playbackHolder = newPlaybackHolder
    }
}

fun View.getVisibilityPercent(): Float {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.width().toFloat() / width * rect.height() / height * 100f
}
