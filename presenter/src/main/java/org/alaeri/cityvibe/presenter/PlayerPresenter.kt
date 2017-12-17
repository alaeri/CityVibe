package org.alaeri.cityvibe.presenter

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import org.alaeri.cityvibe.model.Song

/**
 * Created by Emmanuel Requier on 17/12/2017.
 */

interface IPlayerPresenter : AppPresenter<IPlayerPresenter, IPlayerPresenter.View> {

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

    interface View : AppView<IPlayerPresenter, View> {
        fun setStatus(status: Status)
        fun setSongs(Songs: List<Song>, selectedPosition: Int)
        fun showSongAt(position: Int)
    }
}

class PlayerPresenter : IPlayerPresenter, BaseAppPresenter<IPlayerPresenter, IPlayerPresenter.View>() {

    private val compositeDispo = CompositeDisposable()
    private val mp = MediaPlayer()
    private lateinit var songs: List<Song>
    private var songPosition : Int = 0
    private var justStarted = false

    init {
        mp.setOnPreparedListener {
            mp.start()
            view?.setStatus(IPlayerPresenter.Status.PLAYING)
        }
        mp.setOnErrorListener { _,_,_ -> view?.setStatus(IPlayerPresenter.Status.ERROR); true }
        mp.setOnCompletionListener {  }
    }


    override fun start() {
        songs = dataManager.songQueue
        songPosition = dataManager.positionInQueue
        Log.d("PlayerPresenter", "Playable songs: $songs, $songPosition")
        view?.setSongs(songs, songPosition)
        justStarted = true
    }

    override fun resume() {
        if(justStarted){ justStarted = false } else { startPlayback(songs[songPosition]) }
    }

    override fun pause() {
        mp.reset()
    }

    override fun destroy() {
        compositeDispo.clear()
        mp.release()
    }

    private fun startPlayback(song: Song) {
        mp.reset()
        view?.setStatus(IPlayerPresenter.Status.LOADING)

        val sub = dataManager.populateSong(song.id).subscribe ({ it ->

            Log.d("PlayerActivity", "Playable song: $it")
            try{
                mp.setDataSource(dataManager.context, Uri.parse(it.previewUrl))
                mp.prepareAsync()
            } catch (e: IllegalStateException){
                view?.setStatus(IPlayerPresenter.Status.ERROR)
            }
            view?.setStatus(IPlayerPresenter.Status.BUFFERING)

        },{
            view?.setStatus(IPlayerPresenter.Status.ERROR)
        })
        compositeDispo.add(sub)
    }

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
