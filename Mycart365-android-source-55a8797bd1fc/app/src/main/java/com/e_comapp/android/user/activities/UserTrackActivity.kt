package com.e_comapp.android.user.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.e_comapp.android.R


class UserTrackActivity : AppCompatActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_track)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {}
    private fun setupDefaults() {}
    private fun setupEvents() {}
}