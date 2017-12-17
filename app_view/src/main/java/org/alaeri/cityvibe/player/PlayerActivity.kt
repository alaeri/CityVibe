package org.alaeri.cityvibe.player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_player.*
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.cityvibe.CityVibeApp
import org.alaeri.cityvibe.home.HomeActivity
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.model.Song

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * This activity displays control buttons and a larger view of the selected song in a viewpager
 *
 */
class PlayerActivity: AppCompatActivity() {

    private val compositeDispo = CompositeDisposable()
    private val mp = MediaPlayer()
    private lateinit var dataManager : DataManager
    private lateinit var songs :  ArrayList<Song>
    private var songPosition : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        supportPostponeEnterTransition()
        dataManager = (application  as CityVibeApp).dataManager

        songPosition = intent.extras[HomeActivity.KEY_EXTRA_SELECTED_SONG_POSITION] as Int
        songs = intent.extras[HomeActivity.KEY_EXTRA_SONGS] as ArrayList<Song> // :*(

        viewPager.adapter = SongPagerAdapter(this, songs)
        viewPager.currentItem = songPosition
        viewPager.setPageTransformer(true, DepthPageTransformer())

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                songPosition = position
                startPlayback(songs[position])
            }
        })

        previousButton.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem-1, true)}
        nextButton.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem+1, true)}
        mp.setOnCompletionListener { viewPager.setCurrentItem(viewPager.currentItem+1, true) }
        playButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener{
            override fun onClick(view: View) {
                if(mp.isPlaying)  mp.pause() else mp.start()
            }
        })

        quitButton.setOnClickListener{ finish() } //TODO Add transition to bottom
    }

    override fun onStart() {
        super.onStart()
        startPlayback( songs[songPosition] )
    }

    override fun onPause() {
        super.onPause()
        bufferingTextView.visibility = View.INVISIBLE
        compositeDispo.clear()
        mp.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.release()
    }

    private fun startPlayback(song: Song){
        compositeDispo.clear()
        mp.reset()
        bufferingTextView.text = getString(R.string.loading)
        bufferingTextView.visibility = View.VISIBLE

        val sub = dataManager.populateSong(song.id).subscribe ({ it ->

            Log.d("PlayerActivity", "Playable song: $it")
            mp.setDataSource(this, Uri.parse(it.previewUrl))
            mp.prepareAsync()
            bufferingTextView.visibility = View.VISIBLE
            bufferingTextView.text = getString(R.string.buffering)
            mp.setOnPreparedListener {
                mp.start()
                bufferingTextView.visibility = View.INVISIBLE
                playButton.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
            }
        },{
           bufferingTextView.text = getString(R.string.no_network)
           bufferingTextView.visibility = View.VISIBLE
        })
        compositeDispo.add(sub)
    }

}