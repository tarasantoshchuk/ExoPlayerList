package com.tarasantoshchuk.exoplayerlist.sample

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter

class MyPlayerConfig : ExoPlayerAdapter.PlayerConfig<SimpleExoPlayer> {
    private lateinit var context: Context
    private lateinit var simpleCache: SimpleCache

    private val cacheDataSourceFactory: CacheDataSourceFactory by lazy {
        val dataSourceFactory = DefaultHttpDataSourceFactory(Util.getUserAgent(context, "ExoExoMple"))
        CacheDataSourceFactory(simpleCache, dataSourceFactory)
    }

    override fun init(context: Context) {
        this.context = context
        simpleCache = SimpleCache(context.filesDir, LeastRecentlyUsedCacheEvictor(40_000_000))
    }

    override fun release() {
        simpleCache.release()
    }

    override fun createPlayer(context: Context): SimpleExoPlayer {
        return ExoPlayerFactory.newSimpleInstance(context)
    }

    override fun createMediaSource(position: Int): MediaSource {
        val videoDuration = 15_000_000L

        val start = position * videoDuration
        val end = start + videoDuration

        return ClippingMediaSource(
            ExtractorMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")), start, end)
    }
}
