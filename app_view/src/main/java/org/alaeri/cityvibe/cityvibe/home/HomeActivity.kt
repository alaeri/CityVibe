package org.alaeri.cityvibe.cityvibe.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import org.alaeri.cityvibe.cityvibe.R
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.model.DataManagerImpl
import org.alaeri.cityvibe.model.PlayableSong
import org.alaeri.cityvibe.model.Song
import java.util.*


class HomeActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private val dataManager : DataManager = DataManagerImpl()

    private val displayedSongs = ArrayList<Song>()

    private val songsAdapter = SongsAdapter(displayedSongs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        songListView.layoutManager = LinearLayoutManager(this)
        songListView.adapter = songsAdapter
        swiperefresh.setOnRefreshListener { refresh() }
        refresh()

    }

    private fun refresh() {
        swiperefresh.isRefreshing = true
        val sub = dataManager.refreshPopular().subscribe { it ->
            displayedSongs.clear()
            displayedSongs.addAll(it.songs)
            songsAdapter.notifyDataSetChanged()
            swiperefresh.isRefreshing = false
        }
        compositeDisposable.add(sub)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
