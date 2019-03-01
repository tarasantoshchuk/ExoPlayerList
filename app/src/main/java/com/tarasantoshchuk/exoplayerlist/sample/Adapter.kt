package com.tarasantoshchuk.exoplayerlist.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.tarasantoshchuk.exoplayerlist.ExoPlayerAdapter
import com.tarasantoshchuk.exoplayerlist.ViewProvider

class Adapter(
    playerConfig: PlayerConfig<SimpleExoPlayer>,
    playbackConfig: PlaybackConfig<SimpleExoPlayer, TestViewHolder>,
    fullscreenConfig: FullscreenConfig<SimpleExoPlayer, TestViewHolder>,
    scrollConfig: ScrollConfig<TestViewHolder> = ScrollConfig.default()
) : ExoPlayerAdapter<SimpleExoPlayer, Adapter.TestViewHolder>(
    playerConfig,
    playbackConfig,
    fullscreenConfig,
    scrollConfig
) {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.number.text = position.toString()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ViewProvider<PlayerView> {
        override fun playerView(): PlayerView {
            return playerUI
        }

        val visibility = itemView.findViewById<TextView>(R.id.visibility)
        val number = itemView.findViewById<TextView>(R.id.number)
        val playerUI = itemView.findViewById<PlayerView>(R.id.player)
        val notPlayingWall = itemView.findViewById<View>(R.id.not_playing)
        val bufferingWall = itemView.findViewById<View>(R.id.buffering)
    }
}
