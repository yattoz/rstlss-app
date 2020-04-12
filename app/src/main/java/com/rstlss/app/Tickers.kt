package com.rstlss.app

import android.support.v4.media.session.PlaybackStateCompat
import com.rstlss.app.playerstore.PlayerStore
import java.util.*

class ApiFetchTick  : TimerTask() {
    override fun run() {
        if (PlayerStore.instance.playbackState.value == PlaybackStateCompat.STATE_STOPPED)
        {
            PlayerStore.instance.fetchApi()
        }
    }
}

class Tick  : TimerTask() {
    override fun run() {
        PlayerStore.instance.currentTime.postValue(PlayerStore.instance.currentTime.value!! + 500)
    }
}

