package com.tarasantoshchuk.exoplayerlist

import android.view.View

interface ViewProvider<V: View> {
    fun playerView(): V
}