package org.alaeri.cityvibe.cityvibe.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import org.alaeri.cityvibe.cityvibe.CityVibeApp
import org.alaeri.cityvibe.cityvibe.R
import org.alaeri.cityvibe.model.DataManager
import org.alaeri.cityvibe.model.RefreshResults
import org.alaeri.cityvibe.model.Song
import java.util.*


class HomeActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var  dataManager : DataManager

    private val displayedSongs = ArrayList<Song>()

    private val songsAdapter = SongsAdapter(displayedSongs)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataManager = (this.application as CityVibeApp).dataManager

        setContentView(R.layout.activity_home)
        songListView.layoutManager = LinearLayoutManager(this)
        songListView.adapter = songsAdapter

        swiperefresh.setOnRefreshListener { refresh() }
        initTopCharts()

    }

    private fun initTopCharts() {
        displayedSongs.clear()
        displayedSongs.addAll(dataManager.popularSongs)
        songsAdapter.notifyDataSetChanged()
        refresh()
    }

    private fun refresh() {
        swiperefresh.isRefreshing = true
        val sub = dataManager.refreshPopular().subscribe { it ->
            when (it) {
                is RefreshResults.NewResults -> {
                    displayedSongs.clear()
                    displayedSongs.addAll(it.songs)
                    songsAdapter.notifyDataSetChanged()
                }
                is RefreshResults.NoChange -> Toast.makeText(this, R.string.no_changes, Toast.LENGTH_SHORT).show()
                is RefreshResults.NoConnection -> Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
            }
            swiperefresh.isRefreshing = false
        }
        compositeDisposable.add(sub)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
