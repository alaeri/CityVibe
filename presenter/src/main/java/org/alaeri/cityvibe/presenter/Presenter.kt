package org.alaeri.cityvibe.presenter

import org.alaeri.cityvibe.model.DataManager

/**
 * Created by Emmanuel Requier on 17/12/2017.
 *
 * I reused some of the basic mvp framework we use at my work app for this
 * We try to lock the presenter and the view to avoid unchecked casts,
 * Too verbose // still an unchecked cast in BaseActivity.
 *
 * see BaseActivity.kt in module app_view that shows how to link the activity and the presenter
 */

interface IAppPresenter<P : IAppPresenter<P, V>, V: IAppView<P, V>> {
    fun onStart(v : V)
    fun onResume()
    fun onPause()
    fun onDestroy()

}

interface IAppView<P : IAppPresenter<P, V>, V: IAppView<P, V>> {
    fun dataManager(): DataManager
}

abstract class BaseAppPresenter<P : IAppPresenter<P, V>, V: IAppView<P, V>> : IAppPresenter<P, V> {

    private var _view : V? = null
    protected val view: V?
        get() = _view

    private var _active : Boolean = false
    protected val active : Boolean // should be true when activity isResumed
        get() = _active

    private lateinit var _dataManager : DataManager
    protected val dataManager : DataManager
        get() = _dataManager

    override fun onStart(v: V){
        _view = v
        _dataManager = v.dataManager()
        start()
    }

    override fun onResume(){
        _active = true
        resume()
    }

    override fun onPause(){
        _active = false
        pause()
    }

    override fun onDestroy(){
        _view = null
        destroy()
    }


    /**
     * Let's mandate the use of these functions
     * so no one can forget about lifecycle and cleaning Rx stuff
     * view is set to null before start and after destroy too so we can't make mistakes
     */
    protected abstract fun start()
    protected abstract fun resume()
    protected abstract fun pause()
    protected abstract fun destroy()


}