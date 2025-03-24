package com.mycompany.sma

import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import nl.dionsegijn.konfetti.xml.KonfettiView
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.Position
import android.util.Log
import android.view.ViewTreeObserver
import android.os.Handler
import android.os.Looper
import android.view.View

open class BaseActivity : AppCompatActivity() {

    private lateinit var konfettiView: KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        konfettiView = findViewById(R.id.konfettiView)

        // Kiểm tra khi KonfettiView đã sẵn sàng
        konfettiView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (konfettiView.width > 0 && konfettiView.height > 0) {
                    Log.d("DEBUG_TEST", "KonfettiView ready with size: ${konfettiView.width} x ${konfettiView.height}")
                    konfettiView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            showParticles(event.x, event.y)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            showParticles(event.x, event.y)
        }
        return super.onTouchEvent(event)
    }

    private fun showParticles(x: Float, y: Float) {
        konfettiView.post {
            if (konfettiView.width == 0 || konfettiView.height == 0) {
                Log.e("DEBUG_TEST", "KonfettiView still isn't painted, cannot create effect.")
                return@post
            }

            Log.d("DEBUG_TEST", "Start effect at: ($x, $y)")

            konfettiView.start(
                listOf(
                    Party(
                        speed = 5f,
                        maxSpeed = 10f,
                        damping = 0.9f,
                        angle = 270,
                        spread = 360,
                        colors = listOf(0xFFE91E63.toInt(), 0xFFFFC107.toInt(), 0xFF3F51B5.toInt()),
                        shapes = listOf(Shape.Circle, Shape.Square),
                        fadeOutEnabled = true,
                        timeToLive = 2000L,
                        position = Position.Relative(
                            x.toDouble() / konfettiView.width.toDouble(),
                            y.toDouble() / konfettiView.height.toDouble()
                        ),
                        emitter = Emitter(duration = 100L).max(50)
                    )
                )
            )
        }
    }


}
