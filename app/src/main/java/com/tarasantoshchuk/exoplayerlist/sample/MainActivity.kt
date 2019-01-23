package com.tarasantoshchuk.exoplayerlist.sample

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.ViewAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.PlayerView
import android.view.ViewGroup



class MainActivity : AppCompatActivity() {
    private val adapter: Adapter by lazy {
        Adapter(this)
    }

    private val playerView: PlayerView by lazy {
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
                adapter.enterFullScreen { player, oldHolder ->
                    PlayerView.switchTargetView(player, oldHolder.playerUI, playerView)

                    oldHolder.playerUI.run {
                        disableClipOnParents()
                        animateToMatch(playerView).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {
                            }

                            override fun onAnimationCancel(animation: Animator?) {
                            }

                            override fun onAnimationStart(animation: Animator?) {
                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                playerView.visibility = View.VISIBLE
                            }
                        })
                            .start()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (adapter.isFullscreen()) {
            adapter.exitFullScreen { player, newHolder ->
                playerView.visibility = View.INVISIBLE
                PlayerView.switchTargetView(player, playerView, newHolder.playerUI)
            }
        } else {
            super.onBackPressed()
        }
    }
}

fun View.animateToMatch(other: View): ViewPropertyAnimator {
    return animate()
        .xBy(other.pivotX - pivotX)
        .yBy(other.pivotY - pivotY)
        .scaleX(other.width.toFloat() / width.toFloat())
        .scaleY(other.height.toFloat() / height.toFloat())
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
