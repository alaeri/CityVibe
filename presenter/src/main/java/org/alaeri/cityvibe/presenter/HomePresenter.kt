package org.alaeri.cityvibe.presenter

import io.reactivex.disposables.CompositeDisposable
import org.alaeri.cityvibe.model.RefreshResults
import org.alaeri.cityvibe.model.Song

/**
 * Created by Emmanuel Requier on 17/12/2017.
 */

interface IHomePresenter : AppPresenter<IHomePresenter, IHomePresenter.View> {

    interface View : AppView<IHomePresenter, View> {
        fun replaceContentWith(songs: List<Song>)
        fun showAlert(alert: HomePresenter.Alert)
        fun stopRefreshing()
    }
}

class HomePresenter(view: IHomePresenter.View) : IHomePresenter, BaseAppPresenter<IHomePresenter, IHomePresenter.View>(view) {

    private val compositeDisposable = CompositeDisposable()

    enum class Alert{
        NO_NETWORK,
        NO_CHANGES
    }

    override fun start() {
        view?.replaceContentWith(dataManager.popularSongs)
        onSwipeToRefresh()
    }

    override fun resume() {}

    override fun pause() {}

    override fun destroy() {
        compositeDisposable.clear()
    }

    fun query(newText: String) {
        if (newText.isEmpty()) {
            replaceContentWith(dataManager.popularSongs)
        } else {
            val sub = dataManager.search(newText).subscribe { it ->
                replaceContentWith(it)
            }
            compositeDisposable.add(sub)
        }
    }

    fun onSwipeToRefresh(){
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

    private fun replaceContentWith(songs: List<Song>){
        if(active) {
            view?.replaceContentWith(songs)
        }
    }




}