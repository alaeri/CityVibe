package org.alaeri.cityvibe.player

import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton
import kotlinx.android.synthetic.main.activity_player.*
import ooo.oxo.library.widget.PullBackLayout
import org.alaeri.cityvibe.BaseActivity
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.model.Song
import org.alaeri.cityvibe.presenter.IPlayerPresenter
import org.alaeri.cityvibe.presenter.PlayerPresenter

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * This activity displays control buttons and a larger view of the selected song in a viewpager
 *
 * Most of the code is setting up the different views and listeners
 * The interesting bits are in setStatus
 * @see setStatus
 *
 */
class PlayerActivity: BaseActivity<IPlayerPresenter, IPlayerPresenter.View>(), IPlayerPresenter.View {

    override val presenter: IPlayerPresenter = PlayerPresenter()
    private val viewPagerAdapter = SongPagerAdapter(this)
    private var lastStatus = IPlayerPresenter.Status.PAUSE

    override fun setContentBeforePresenterStarts() {
        Log.d("PlayerActivity", "POSTPONING ANIMATION")
        supportPostponeEnterTransition()
        setContentView(R.layout.activity_player)

        viewPager.run{
            setPageTransformer(true, DepthPageTransformer())
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    presenter.swipeTo(position)
                }
            })
            adapter = viewPagerAdapter
        }


        previousButton.setOnClickListener { presenter.previousClicked() }
        nextButton.setOnClickListener { presenter.nextClicked() }
        playButton.setOnMusicFabClickListener(object : FloatingMusicActionButton.OnMusicFabClickListener{
            override fun onClick(view: View) {
                presenter.playPauseClicked() //Not sure why we can't convert this function to lambda
            }
        })

        //TODO fix transition issue (transparency and conflict with changeBounds enter animation)
        puller.setCallback(object: PullBackLayout.Callback{
            override fun onPullStart() {}
            override fun onPull(p0: Float) {}
            override fun onPullCancel() {}

            override fun onPullComplete() = supportFinishAfterTransition()
        })
        Log.d("PlayerActivity", "FINISHED CONSTRUCTING")
    }

    override fun setSongs(songs: List<Song>, selectedPosition: Int) {
        Log.d("PlayerActivity", "SETTING SONGS : $songs, $selectedPosition")
        viewPagerAdapter.songs = songs
        viewPagerAdapter.notifyDataSetChanged()
        viewPager.currentItem = selectedPosition
    }

    override fun showSongAt(position: Int) {
        viewPager.setCurrentItem(position, true)
    }


    override fun setStatus(status: IPlayerPresenter.Status) {
        Log.d("PlayerActivity", "Status song: $status $lastStatus")
        when(status){
            IPlayerPresenter.Status.PLAYING -> {
                if(status != lastStatus) { //Sometimes playButton animation loops otherwise.
                    playButton.animation?.cancel()
                    playButton.changeMode(FloatingMusicActionButton.Mode.PAUSE_TO_PLAY)
                    bufferingTextView.visibility = View.INVISIBLE
                }
            }
            IPlayerPresenter.Status.PAUSE-> {
                playButton.animation?.cancel()
                playButton.changeMode(FloatingMusicActionButton.Mode.PLAY_TO_PAUSE)
                bufferingTextView.visibility = View.INVISIBLE
            }
            IPlayerPresenter.Status.BUFFERING -> {
                bufferingTextView.text = getString(R.string.buffering)
                bufferingTextView.visibility = View.VISIBLE
            }
            IPlayerPresenter.Status.LOADING -> {
                bufferingTextView.text = getString(R.string.loading)
                bufferingTextView.visibility = View.VISIBLE
            }
            IPlayerPresenter.Status.ERROR -> {
                bufferingTextView.text = getString(R.string.no_network)
                bufferingTextView.visibility = View.VISIBLE
            }

        }
        lastStatus = status
    }

    override fun setSongProgress(progress: Int) { seekBar.progress = progress }

}