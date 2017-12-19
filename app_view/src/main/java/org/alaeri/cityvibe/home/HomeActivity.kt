package org.alaeri.cityvibe.home

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import org.alaeri.cityvibe.BaseActivity
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.model.Song
import org.alaeri.cityvibe.player.PlayerActivity
import org.alaeri.cityvibe.presenter.HomePresenter
import org.alaeri.cityvibe.presenter.IHomePresenter
import java.util.*
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class HomeActivity : BaseActivity<IHomePresenter, IHomePresenter.View>(), IHomePresenter.View{



    override var presenter: IHomePresenter = HomePresenter()

    private val displayedSongs = ArrayList<Song>()

    private val songsAdapter = SongsAdapter(displayedSongs)
            { position : Int, sharedImageView: AppCompatImageView ->
                            presenter.openSong(position, sharedImageView) }

    override fun setContentBeforePresenterStarts() {
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)
        searchView.run {

            fun query(newText: String): Boolean {
                presenter.query(newText)
                return true
            }

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

        largeRefreshButton.setOnClickListener { showLoading(); presenter.onSwipeToRefresh() }
        swiperefresh.setOnRefreshListener { swiperefresh.isRefreshing = true; presenter.onSwipeToRefresh() }
    }

    override fun onStart() {
        super.onStart()
        searchView.clearFocus()
        val imm = this@HomeActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0);
    }

    override fun openPlayer(animatedProperties: Any?) {
        Log.d("HomeActivity", "opening player..... $animatedProperties")
        val intent = Intent(this, PlayerActivity::class.java)
        val sharedImageView = animatedProperties as AppCompatImageView
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedImageView,
            ViewCompat.getTransitionName(sharedImageView))
        startActivity(intent, options.toBundle())
    }

    override fun showLoading() {
        Log.d("HomeActivity", "showLoading")
        songListView.visibility = View.GONE
        largeRefreshButton.visibility = View.GONE
        largeLoader.visibility = View.VISIBLE
    }

    override fun showContent(content: IHomePresenter.Content) {
        Log.d("HomeActivity", "showing content $content")
        when(content) {
            is IHomePresenter.Content.Empty -> {
                songListView.visibility = View.GONE
                largeRefreshButton.visibility = View.VISIBLE
                largeLoader.visibility = View.GONE
                if(content.fetchResult == IHomePresenter.FetchResult.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
                }
            }
            is IHomePresenter.Content.Top -> {
                subtitleTextView.setText(R.string.top)
                songListView.visibility = View.VISIBLE
                largeRefreshButton.visibility = View.GONE
                largeLoader.visibility = View.GONE
                displayedSongs.clear()
                displayedSongs.addAll(content.songs)
                songsAdapter.notifyDataSetChanged()
            }
            is IHomePresenter.Content.Search -> {
                subtitleTextView.text = getString(R.string.search_subtitle).format(content.term)
                songListView.visibility = View.VISIBLE
                largeRefreshButton.visibility = View.GONE
                largeLoader.visibility = View.GONE
                displayedSongs.clear()
                displayedSongs.addAll(content.songs)
                songsAdapter.notifyDataSetChanged()
            }
        }
        swiperefresh.isRefreshing = false
    }



}
