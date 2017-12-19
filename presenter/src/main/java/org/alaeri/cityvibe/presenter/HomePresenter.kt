package org.alaeri.cityvibe.presenter

import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import org.alaeri.cityvibe.model.Provider
import org.alaeri.cityvibe.model.Song
import org.alaeri.cityvibe.model.Top
import java.util.*

/**
 * Created by Emmanuel Requier on 17/12/2017.
 *
 */

interface IHomePresenter : IAppPresenter<IHomePresenter, IHomePresenter.View> {

    fun query(term: String)
    fun onSwipeToRefresh()
    fun openSong(position: Int, animatedProperties: Any?)

    /**
     *
     * User has not loaded top
     * loading
     *
     *
     */
    //
    // loading -> NOK -> Content.Empty(date, errorMessage)
    // loading -> OK -> Content.Top(date,  songs, UPDATE)
        //User launch refresh
        //refreshing -> NOK -> Content.Top(date, songs, NO_CONNECTION)
        //refreshing -> OK
                //-> SameContent -> Content.Top(date, songs, NO_CHANGES)
                //-> NewContent -> Content.Top(date, songs, UPDATE)
    // result -> (songs, refreshDate, OK)



    enum class FetchResult {
        INIT,
        NO_NETWORK,
        NO_CHANGES,
        UPDATE
    }

    sealed class Content {
        data class Empty(val fetchResult: FetchResult) : Content()
        data class Top(val songs: List<Song>, val lastRefreshDate: Date, val fetchResult: FetchResult) : Content()
        data class Search(val songs:List<Song>, val term: String) : Content()
    }

    interface View : IAppView<IHomePresenter, View> {
        fun showLoading()
        fun showContent(content: Content)
        fun openPlayer(animatedProperties: Any?)
    }
}

class HomePresenter: IHomePresenter, BaseAppPresenter<IHomePresenter, IHomePresenter.View>() {


    private val compositeDisposable = CompositeDisposable()
//    private var currentResults : List<Song>? = null
    private lateinit var lastTop: Top //= Top.NoResults("initially empty", Provider.CACHE)
    private lateinit var lastContent : IHomePresenter.Content //= IHomePresenter.Content.Empty(IHomePresenter.FetchResult.INIT)


    override fun start() {
        val top = dataManager.top
        lastTop = top
        val content = dataToContent(top)
        view?.showContent(content)
        lastContent = content
        if(top is Top.NoResults) {
            view?.showLoading()
            onSwipeToRefresh()
        }
    }

    private fun dataToContent(top: Top): IHomePresenter.Content {
        return when (top) {
            is Top.NoResults -> IHomePresenter.Content.Empty(IHomePresenter.FetchResult.NO_CHANGES)
            is Top.Results -> IHomePresenter.Content.Top(top.songs, top.refreshDate, IHomePresenter.FetchResult.NO_CHANGES)
        }
    }

    override fun resume() {}

    override fun pause() {}

    override fun destroy() {
        compositeDisposable.clear()
    }

    override fun query(term: String) {
        if (term.isEmpty()) {
            val content = dataToContent(lastTop)
            view?.showContent(content)
            lastContent = content
        } else {
            val sub = dataManager.search(term).subscribe { it ->
                val content = IHomePresenter.Content.Search(it, term)
                view?.showContent(content)
                lastContent = content
            }
            compositeDisposable.add(sub)
        }
    }

    override fun onSwipeToRefresh(){

        val sub = dataManager.refreshPopular().subscribe { it ->
            val immutableLastTop = lastTop
            val content = buildContentWithTops(it, immutableLastTop)
            lastTop = it
            view?.showContent(content)
            lastContent = content
        }
        compositeDisposable.add(sub)
    }

    private fun buildContentWithTops(newTop: Top, lastTop: Top): IHomePresenter.Content {
        return when (newTop) {
            is Top.Results -> {
                if (lastTop is Top.Results && lastTop.songs.isEqual(newTop.songs)) {
                    val fetchStatus = if (newTop.provider == Provider.CACHE) {
                        IHomePresenter.FetchResult.NO_NETWORK
                    } else {
                        IHomePresenter.FetchResult.NO_CHANGES
                    }
                    IHomePresenter.Content.Top(newTop.songs, newTop.refreshDate, fetchStatus)
                } else {
                    val fetchStatus = if (newTop.provider == Provider.CACHE) {
                        IHomePresenter.FetchResult.NO_NETWORK
                    } else {
                        IHomePresenter.FetchResult.UPDATE
                    }
                    IHomePresenter.Content.Top(newTop.songs, newTop.refreshDate, fetchStatus)
                }
            }
            is Top.NoResults -> IHomePresenter.Content.Empty(IHomePresenter.FetchResult.NO_NETWORK)
        }
    }

    override fun openSong(position: Int, animatedProperties: Any?) {
        val immutableContent = lastContent
        val songQueue = when(immutableContent){
            is IHomePresenter.Content.Empty -> return
            is IHomePresenter.Content.Search -> immutableContent.songs
            is IHomePresenter.Content.Top -> immutableContent.songs
        }
        dataManager.songQueue = songQueue
        dataManager.positionInQueue = position
        view?.openPlayer(animatedProperties)
    }

    private fun <T> List<T>.isEqual(o: List<T>): Boolean  {
        val temp = ArrayList(this)
        temp.retainAll(o)
        Log.d("HomePresenterImpl","size: ${temp.size} / ${this.size}")
        return temp.size == this.size
    }
}