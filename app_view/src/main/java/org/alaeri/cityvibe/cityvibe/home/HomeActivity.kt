package org.alaeri.cityvibe.cityvibe.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import org.alaeri.cityvibe.cityvibe.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        songListView.layoutManager = LinearLayoutManager(this)
        songListView.adapter = SongsAdapter()
    }
}
