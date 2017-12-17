package org.alaeri.cityvibe.cityvibe

import android.app.Application
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.model.DataManagerImpl

/**
 * Created by Emmanuel Requier on 16/12/2017.
 */
class CityVibeApp: Application() {

    val dataManager : DataManager = DataManagerImpl(this)




}
