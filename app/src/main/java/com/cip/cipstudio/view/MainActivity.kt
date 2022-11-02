package com.cip.cipstudio.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBRepository


class MainActivity : AppCompatActivity() {

    private lateinit var gameRepo : IGDBRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameRepo = IGDBRepository()

        supportActionBar!!.hide()
    }
}
