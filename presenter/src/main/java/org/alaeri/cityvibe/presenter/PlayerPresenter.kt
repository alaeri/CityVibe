package org.alaeri.cityvibe.presenter

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.alaeri.cityvibe.model.CVMediaPlayer
import org.alaeri.cityvibe.model.CVMediaPlayerImpl
import org.alaeri.cityvibe.model.Song
import java.util.concurrent.TimeUnit

/**
 * Created by Emmanuel Requier on 17/12/2017.
 * Presenter for the player
 *
 */
interface IPlayerPresenter : IAppPresenter<IPlayerPresenter, IPlayerPresenter.View> {

    fun swipeTo(position: Int)
    fun playPauseClicked()
    fun nextClicked()
    fun previousClicked()

    enum class Status {
        LOADING, //fetching preview url
        BUFFERING, //buffering song
        PLAYING,
        PAUSE,
        ERROR
    }

    interface View : IAppView<IPlayerPresenter, View> {
        fun setStatus(status: Status)
        fun setSongs(songs: List<Song>, selectedPosition: Int)
        fun showSongAt(position: Int)
        fun setSongProgress(progress: Int)
    }
}

class PlayerPresenter : IPlayerPresenter, BaseAppPresenter<IPlayerPresenter, IPlayerPresenter.View>() {

    private val compositeDispo = CompositeDisposable()
    private lateinit var songs: List<Song>
    private var songPosition : Int = 0
    private lateinit var mp : CVMediaPlayer


    override fun start() {
        songs = dataManager.songQueue
        songPosition = dataManager.positionInQueue
        Log.d("PlayerPresenter", "Playable songs: $songs, $songPosition")
        mp = CVMediaPlayerImpl(dataManager.context!!, dataManager)

        view?.setSongs(songs, songPosition)

        //Slow subscription to show messages and statuses without blinking
        val sub = mp.events.throttleLast(60, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe {
            val status = when (it) {
                is CVMediaPlayer.Event.Errored -> IPlayerPresenter.Status.ERROR
                is CVMediaPlayer.Event.Playing -> IPlayerPresenter.Status.PLAYING
                is CVMediaPlayer.Event.Buffering -> IPlayerPresenter.Status.BUFFERING
                is CVMediaPlayer.Event.Loading -> IPlayerPresenter.Status.LOADING
                else -> IPlayerPresenter.Status.PAUSE
            }
            view?.setStatus(status)
            if(it is CVMediaPlayer.Event.Completed){ nextClicked() }
        }
        compositeDispo.add(sub)

        //Fast subscription for a smooth seekbar
        val seekSub = mp.events.filter{it is CVMediaPlayer.Event.Playing}.subscribe {
            view?.setSongProgress(((it as CVMediaPlayer.Event.Playing).progress * 1000).toInt())
        }
        compositeDispo.add(seekSub)
    }

    override fun resume() {
        startPlayback(songs[songPosition])
    }

    override fun pause() {
        mp.pause()
    }

    override fun destroy() {
        compositeDispo.clear()
        mp.destroy()
    }

    private fun startPlayback(song: Song) = mp.playSong(song)

    override fun playPauseClicked() {
        if(mp.isPlaying) mp.pause() else startPlayback(songs[songPosition])
    }

    override fun nextClicked() {
        if(songPosition < songs.size -1) {
            songPosition ++
            startPlayback(songs[songPosition])
            view?.showSongAt(songPosition)
        }
    }

    override fun previousClicked() {
        if(songPosition > 0) {
            songPosition --
        }
        startPlayback(songs[songPosition])
        view?.showSongAt(songPosition)
    }

    override fun swipeTo(position: Int) {
        songPosition = position
        startPlayback(songs[songPosition])
    }
}
