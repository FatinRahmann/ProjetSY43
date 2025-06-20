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

        // 1. Bounce in + Bounce again
        logo.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(BounceInterpolator())
            .withEndAction {
                // Second bounce: expand a bit then shrink
                logo.animate()
                    .scaleX(1.25f)
                    .scaleY(1.25f)
                    .setDuration(300)
                    .setInterpolator(BounceInterpolator())
                    .withEndAction {
                        logo.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(300)
                            .setInterpolator(BounceInterpolator())
                            .withEndAction {
                                // Fade out before navigating
                                logo.animate()
                                    .alpha(0f)
                                    .setDuration(400)
                                    .withEndAction {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .start()
                            }
                            .start()
                    }
                    .start()
            }
            .start()
            }
        }