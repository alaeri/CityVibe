package org.alaeri.cityvibe.home

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import org.alaeri.cityvibe.BaseActivity
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.model.Song
import org.alaeri.cityvibe.player.PlayerActivity
import org.alaeri.cityvibe.presenter.HomePresenter
import org.alaeri.cityvibe.presenter.IHomePresenter
import java.util.*

class HomeActivity : BaseActivity<IHomePresenter, IHomePresenter.View>(), IHomePresenter.View{

    override var presenter: IHomePresenter = HomePresenter()

    private val displayedSongs = ArrayList<Song>()

    private val songsAdapter = SongsAdapter(displayedSongs)
            { position : Int, sharedImageView: AppCompatImageView ->
                            presenter.openSong(position, sharedImageView) }

    override fun setContentBeforePresenterStarts() {
        setContentView(R.layout.activity_home)
        searchView.run {
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
            setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String): Boolean = query(query)
                override fun onQueryTextChange(newText: String): Boolean = query(newText)
            })
        }

        songListView.run {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = songsAdapter
        }

        swiperefresh.setOnRefreshListener { refresh() }
    }

    override fun replaceContentWith(songs: List<Song>) {
        displayedSongs.clear()
        displayedSongs.addAll(songs)
        songsAdapter.notifyDataSetChanged()
    }

    override fun showAlert(alert: HomePresenter.Alert) {
        when(alert) {
            HomePresenter.Alert.NO_NETWORK -> Toast.makeText(this,R.string.no_network, Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    override fun stopRefreshing() {
        swiperefresh.isRefreshing = false
    }

    override fun openPlayer(animatedProperties: Any?) {
        Log.d("HomeActivity", "opening player..... $animatedProperties")
        val intent = Intent(this, PlayerActivity::class.java)
        val sharedImageView = animatedProperties as AppCompatImageView
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedImageView,
            ViewCompat.getTransitionName(sharedImageView))
        startActivity(intent, options.toBundle())
    }

    private fun query(newText: String): Boolean {
        presenter.query(newText)
        return true
    }

    private fun refresh() {
        swiperefresh.isRefreshing = true
        presenter.onSwipeToRefresh()
    }

}
