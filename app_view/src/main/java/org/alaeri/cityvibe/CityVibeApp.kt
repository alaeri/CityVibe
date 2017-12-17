package org.alaeri.cityvibe.cityvibe

import android.app.Application
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.model.DataManagerImpl

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * CityVibeApp Singleton contains a reference to the DataManager
 *
 */
class CityVibeApp: Application() {

    val dataManager : DataManager = DataManagerImpl(this)




}
