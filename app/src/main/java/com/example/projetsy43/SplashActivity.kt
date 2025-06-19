package com.example.projetsy43

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.projetsy43.viewmodel.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logoImage)

        // Start big and invisible
        logo.scaleX = 2.0f
        logo.scaleY = 2.0f

        // 1. Bounce in + fade in
        logo.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1f)
            .setDuration(1200)
            .setInterpolator(BounceInterpolator())
            .withEndAction {
                // 2. Fade out
                logo.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction {
                        // 3. Navigate to main page
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .start()
            }
            .start()
    }
}