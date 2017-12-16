package org.alaeri.cityvibe.home

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_home.*
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.cityvibe.CityVibeApp
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

        searchView.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String): Boolean = query(query)

            override fun onQueryTextChange(newText: String): Boolean = query(newText)

        })


        songListView.layoutManager = LinearLayoutManager(this)
        songListView.adapter = songsAdapter

        swiperefresh.setOnRefreshListener { refresh() }
        initTopCharts()

    }

    private fun query(newText: String): Boolean {
        if (newText.isEmpty()) {
            replaceContentWith(dataManager.popularSongs)
        } else {
            val sub = dataManager.search(newText).subscribe { it ->
                replaceContentWith(it)
            }
            compositeDisposable.add(sub)
        }
        return true
    }

    private fun initTopCharts() {
        replaceContentWith(dataManager.popularSongs)
        refresh()
    }

    private fun refresh() {
        swiperefresh.isRefreshing = true
        val sub = dataManager.refreshPopular().subscribe { it ->
            when (it) {
                is RefreshResults.NewResults -> {
                    val songs = it.songs
                    replaceContentWith(songs)
                }
                is RefreshResults.NoChange -> Toast.makeText(this, R.string.no_changes, Toast.LENGTH_SHORT).show()
                is RefreshResults.NoConnection -> Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
            }
            swiperefresh.isRefreshing = false
        }
        compositeDisposable.add(sub)
    }

    private fun replaceContentWith(songs: List<Song>) {
        displayedSongs.clear()
        displayedSongs.addAll(songs)
        songsAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
