package com.tarasantoshchuk.exoplayerlist.sample

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter
import com.tarasantoshchuk.exoplayerlist.getVisibilityPercent
import com.tarasantoshchuk.exoplayerlist.view_holder.PlayerViewVH
import kotlinx.android.synthetic.main.list_item.view.*

class Adapter(context: Context) : ExoPlayerAdapter<SimpleExoPlayer, Adapter.TestViewHolder>(context,
    Config(
        playerProvider = {
            ExoPlayerFactory.newSimpleInstance(it.context)
        }
    )) {
    private val cacheDataSourceFactory: CacheDataSourceFactory by lazy {
        val dataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoExoMple"))
        CacheDataSourceFactory(simpleCache, dataSourceFactory)
    }

    private lateinit var simpleCache: SimpleCache

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.number.text = position.toString()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        simpleCache = SimpleCache(context.filesDir, LeastRecentlyUsedCacheEvictor(40_000_000))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        simpleCache.release()
    }

    private val videoDuration = 15_000_000L

    inner class TestViewHolder(itemView: View) : PlayerViewVH<SimpleExoPlayer>(itemView) {
        val visibility = itemView.findViewById<TextView>(R.id.visibility)
        val number = itemView.findViewById<TextView>(R.id.number)
        val playerUI = itemView.findViewById<PlayerView>(R.id.player)
        val notPlayingWall = itemView.findViewById<View>(R.id.not_playing)
        val bufferingWall = itemView.findViewById<View>(R.id.buffering)

        protected val listener = object : Player.EventListener {
            override fun onLoadingChanged(isLoading: Boolean) {
//                if (isLoading) {
//                    bufferingWall.visibility = View.VISIBLE
//                } else {
//                    bufferingWall.visibility = View.GONE
//                }
            }
        }

        override fun getPlayerView(): PlayerView {
            return playerUI
        }

        override fun onPlaybackGained(player: SimpleExoPlayer) {
            player.addListener(listener)

            Toast.makeText(itemView.context, "onPlaybackGained::$adapterPosition", Toast.LENGTH_SHORT).show()
        }

        override fun onPlaybackLost(player: SimpleExoPlayer) {
            player.removeListener(listener)

            Toast.makeText(itemView.context, "onPlaybackLost::$adapterPosition", Toast.LENGTH_SHORT).show()
        }

        override fun getMediaSource(): MediaSource {
            val pos = adapterPosition

            val start = pos * videoDuration
            val end = start + videoDuration

            return ClippingMediaSource(ExtractorMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")), start, end)
        }

        override fun getVisibilityPercent(): Float {
            return itemView.player.getVisibilityPercent().also {
                visibility.text = "Visibility: $it%"
            }
        }
    }
}
