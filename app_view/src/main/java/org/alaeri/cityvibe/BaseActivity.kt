package org.alaeri.cityvibe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.alaeri.cityvibe.cityvibe.CityVibeApp
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.presenter.IAppPresenter
import org.alaeri.cityvibe.presenter.IAppView

/**
 * Created by Emmanuel Requier on 17/12/2017.
 * We want to do here all the low-level MVP stuff
 */
abstract class BaseActivity<P: IAppPresenter<P, V>, V: IAppView<P,V>> :  AppCompatActivity(), IAppView<P,V>{

    abstract val presenter : P
    abstract fun setContentBeforePresenterStarts()

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBeforePresenterStarts()
        presenter.onStart(this as V) //I tried to fix it but I'm not really strong with generics
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    //This is here because Dependency Injection is not done yet
    final override fun dataManager(): DataManager = (this.application as CityVibeApp).dataManager

}