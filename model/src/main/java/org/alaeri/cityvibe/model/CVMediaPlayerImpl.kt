package org.alaeri.cityvibe.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * Created by Emmanuel Requier on 17/12/2017.
 * This class hides the complexity of the Android Media Player by sending events to a flowable
 *
 */
class CVMediaPlayerImpl(val context: Context,val dataManager: DataManager) : CVMediaPlayer {

    private val mp = MediaPlayer()
    private val compositeDispo = CompositeDisposable()
    private val eventsSubject = BehaviorSubject.createDefault<CVMediaPlayer.Event>(CVMediaPlayer.Event.Noop())
    private val service = Executors.newScheduledThreadPool(1)

    companion object {
        const val SONG_LENGTH = 30000 //TODO set real length
    }

    override val events: Flowable<CVMediaPlayer.Event> = eventsSubject.toFlowable(BackpressureStrategy.LATEST)
            .observeOn(AndroidSchedulers.mainThread()).doOnNext { Log.d("PlayerActivity", "Event song: $it") }

    override val isPlaying: Boolean
        get() = eventsSubject.value is CVMediaPlayer.Event.Playing


    init {
        mp.setOnPreparedListener {
            eventsSubject.onNext(CVMediaPlayer.Event.Playing(0f))
            try {
                mp.start()
            } catch (e: Exception){
                eventsSubject.onNext(CVMediaPlayer.Event.Errored("playback error"))
            }
        }
        mp.setOnErrorListener { _,_,_ -> eventsSubject.onNext(CVMediaPlayer.Event.Errored("playback error")); true }
        mp.setOnCompletionListener { eventsSubject.onNext(CVMediaPlayer.Event.Completed()) }

        //We need to poll the mediaplayer for progress :L
        service.scheduleWithFixedDelay({ checkPlayingAndEmitProgress() }, 4, 4, TimeUnit.MILLISECONDS)

    }

    override fun playSong(song: Song) {
        mp.reset()
        val sub = dataManager.populateSong(song.id).subscribe(
                { it -> hasReceivedData(it) },  //onSuccess
                { _ -> eventsSubject.onNext(CVMediaPlayer.Event.Errored("could not load song data"))})
        compositeDispo.add(sub)
    }

    override fun pause() {
        mp.reset()
        eventsSubject.onNext(CVMediaPlayer.Event.Noop())
    }

    override fun destroy() {
        mp.release()

    }

    private fun checkPlayingAndEmitProgress() {
        if(!mp.isPlaying) return
        val length = SONG_LENGTH
        eventsSubject.onNext(CVMediaPlayer.Event.Playing(mp.currentPosition.toFloat() / length))
    }

    private fun hasReceivedData(song: PlayableSong){
        Log.d("PlayerActivity", "Playable song: $song")
        try {
            mp.setDataSource(context, Uri.parse(song.previewUrl))
            mp.prepareAsync()
            eventsSubject.onNext(CVMediaPlayer.Event.Buffering())
        } catch (e: IllegalStateException) {
            eventsSubject.onNext(CVMediaPlayer.Event.Errored("error during data source or prepare"))
        }
    }
}