package org.alaeri.cityvibe.presenter

import io.reactivex.disposables.CompositeDisposable
import org.alaeri.cityvibe.model.RefreshResults
import org.alaeri.cityvibe.model.Song

/**
 * Created by Emmanuel Requier on 17/12/2017.
 *
 */

interface IHomePresenter : IAppPresenter<IHomePresenter, IHomePresenter.View> {

    fun query(term: String)
    fun onSwipeToRefresh()
    fun openSong(position: Int, animatedProperties: Any?)

    interface View : IAppView<IHomePresenter, View> {
        fun replaceContentWith(songs: List<Song>)
        fun showAlert(alert: HomePresenter.Alert)
        fun stopRefreshing()
        fun openPlayer(animatedProperties: Any?)
    }
}

class HomePresenter: IHomePresenter, BaseAppPresenter<IHomePresenter, IHomePresenter.View>() {


    private val compositeDisposable = CompositeDisposable()
    private var currentResults : List<Song>? = null

    enum class Alert{
        NO_NETWORK,
        NO_CHANGES
    }

    override fun start() {
        replaceContentWith(dataManager.popularSongs)
        onSwipeToRefresh()
    }

    override fun resume() {}

    override fun pause() {}

    override fun destroy() {
        compositeDisposable.clear()
    }

    override fun query(term: String) {
        if (term.isEmpty()) {
            replaceContentWith(dataManager.popularSongs)
        } else {
            val sub = dataManager.search(term).subscribe { it ->
                replaceContentWith(it)
            }
            compositeDisposable.add(sub)
        }
    }

    override fun onSwipeToRefresh(){
        val sub = dataManager.refreshPopular().subscribe { it ->
            when (it) {
                is RefreshResults.NewResults -> {
                    val songs = it.songs
                    replaceContentWith(songs)
                }
                is RefreshResults.NoConnection -> view?.showAlert(Alert.NO_NETWORK)
                is RefreshResults.NoChange -> view?.showAlert(Alert.NO_CHANGES)
            }
            view?.stopRefreshing()
        }
        compositeDisposable.add(sub)
    }

    override fun openSong(position: Int, animatedProperties: Any?) {
        dataManager.songQueue = currentResults!!
        dataManager.positionInQueue = position
        view?.openPlayer(animatedProperties)
    }

    private fun replaceContentWith(songs: List<Song>){
        if(active) {
            currentResults = songs
            view?.replaceContentWith(songs)
        }
    }
}