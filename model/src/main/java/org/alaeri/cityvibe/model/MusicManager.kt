package org.alaeri.cityvibe.model

import android.media.MediaPlayer

/**
 * Created by Emmanuel Requier on 17/12/2017.
 */
interface MusicManager {

    fun playSong(){}

    fun pause()

    fun destroy()

    fun setOnCompletionListener(onCompletionListener: ()->Unit )


}


