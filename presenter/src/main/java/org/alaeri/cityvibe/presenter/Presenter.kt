package org.alaeri.cityvibe.presenter

import org.alaeri.cityvibe.model.DataManager

/**
 * Created by Emmanuel Requier on 17/12/2017.
 */

interface AppPresenter<P : AppPresenter<P, V>, V: AppView<P, V>> {

    fun onStart(v : V)
    fun onResume()
    fun onPause()
    fun onDestroy()

}

interface BaseAppView {
    fun dataManager(): DataManager
}

interface AppView<P : AppPresenter<P, V>, V: AppView<P, V>> : BaseAppView

abstract class BaseAppPresenter<P : AppPresenter<P, V>, V: AppView<P, V>> : AppPresenter<P, V> {

    private var _view : V? = null
    protected val view: V?
        get() = _view

    private var _active : Boolean = false
    protected val active : Boolean
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


    protected abstract fun start()
    protected abstract fun resume()
    protected abstract fun pause()
    protected abstract fun destroy()


}