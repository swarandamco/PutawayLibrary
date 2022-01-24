package com.bfc.putaway.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, PutAwayLoginActivity::class.java)
        Handler().postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            startActivity(intent)
            finish()
        }, 5000)
    }
}
