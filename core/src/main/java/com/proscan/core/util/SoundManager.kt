package com.proscan.core.util

import android.content.Context
import android.media.MediaPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val activePlayers = mutableMapOf<Int, MediaPlayer>()

    fun play(resId: Int) {
        val existing = activePlayers[resId]
        if (existing != null && existing.isPlaying) {
            existing.seekTo(0)
            return
        }
        existing?.release()
        activePlayers[resId] = MediaPlayer.create(context, resId)?.apply {
            setOnCompletionListener {
                activePlayers.remove(resId)
                release()
            }
            start()
        } as MediaPlayer
    }

    fun releaseAll() {
        activePlayers.values.forEach { it.release() }
        activePlayers.clear()
    }
}
