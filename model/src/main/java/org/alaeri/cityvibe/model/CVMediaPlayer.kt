package org.alaeri.cityvibe.model

import io.reactivex.Flowable

/**
 * Created by Emmanuel Requier on 17/12/2017.
 * This interface is a wrapper to Android MediaPlayer so its easier to use.
 */
interface CVMediaPlayer {

    fun playSong(song: Song)

    fun pause()

    fun destroy()

    sealed class Event {

        class Noop : Event()
        class Loading : Event()
        class Buffering : Event()
        data class Playing(val progress: Float) : Event()
        class Completed : Event()
        data class Errored(val message: String) : Event()
    }

    val events: Flowable<Event>
    val isPlaying: Boolean

}


