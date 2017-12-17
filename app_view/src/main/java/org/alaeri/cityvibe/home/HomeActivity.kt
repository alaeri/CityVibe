package org.alaeri.cityvibe.home

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
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


    companion object {
        const val KEY_EXTRA_SELECTED_SONG_POSITION = "SELECTED_SONG_POSITION"
        const val KEY_EXTRA_SONGS = "SONGS"
    }

    override var presenter: HomePresenter = HomePresenter(this)

    private val displayedSongs = ArrayList<Song>()

    private val songsAdapter = SongsAdapter(displayedSongs) { position, sharedImageView ->
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(KEY_EXTRA_SELECTED_SONG_POSITION, position)
        intent.putExtra(KEY_EXTRA_SONGS, displayedSongs)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedImageView,
                ViewCompat.getTransitionName(sharedImageView))
       startActivity(intent, options.toBundle())
    }

    override fun setContent() {
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

    private fun query(newText: String): Boolean {
        presenter?.query(newText)
        return true
    }

    private fun refresh() {
        swiperefresh.isRefreshing = true
        presenter?.onSwipeToRefresh()
    }


}
