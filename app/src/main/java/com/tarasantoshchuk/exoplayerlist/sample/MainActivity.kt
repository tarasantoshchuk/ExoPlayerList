package com.tarasantoshchuk.exoplayerlist.sample

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.PlayerView
import com.tarasantoshchuk.exoplayerlist.playback_config.PlayerViewConfig


class MainActivity : AppCompatActivity() {
    private val adapter: Adapter by lazy {
        Adapter(MyPlayerConfig(), PlayerViewConfig(), MyFullscreenConfig(fullscreenPlayer))
    }

    private val fullscreenPlayer: PlayerView by lazy {
        findViewById<PlayerView>(R.id.fullscreen_player)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recycler).let {
            it.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)

            it.adapter = adapter
        }

        findViewById<View>(R.id.toggle_fullscreen).setOnClickListener {
            if (!adapter.isFullscreen()) {
                adapter.enterFullScreen()
            }
        }
    }

    override fun onBackPressed() {
        if (adapter.isFullscreen()) {
            adapter.exitFullScreen()
        } else {
            super.onBackPressed()
        }
    }
}

fun View.fadeIn(): ViewPropertyAnimator {
    return animate()
        .alpha(1f)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                alpha = 0f
                visibility = View.VISIBLE
            }

        })
}

fun View.fadeOut(): ViewPropertyAnimator {
    return animate()
        .alpha(0f)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                alpha = 1f
            }

        })
}

fun View.disableClipOnParents() {
    if (parent == null) {
        return
    }

    if (this is ViewGroup) {
        clipChildren = false
    }

    if (parent is View) {
        (parent as View).disableClipOnParents()
    }
}
