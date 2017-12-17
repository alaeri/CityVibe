package org.alaeri.cityvibe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.alaeri.cityvibe.cityvibe.CityVibeApp
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.presenter.AppPresenter
import org.alaeri.cityvibe.presenter.AppView

/**
 * Created by Emmanuel Requier on 17/12/2017.
 */
abstract class BaseActivity<P: AppPresenter<P, V>, V: AppView<P,V>> :  AppCompatActivity(), AppView<P,V>{

    abstract val presenter : P
    abstract fun setContentBeforePresenterStarts()

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBeforePresenterStarts()
        presenter.onStart(this as V)
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

    final override fun dataManager(): DataManager = (this.application as CityVibeApp).dataManager

}