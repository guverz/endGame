package com.example.endgame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.random.Random


class CircleLogic(ctx: Context) : View(ctx) {
    private val circles = mutableListOf<Circle>()
    private var rectColor: Int = Color.RED
    private var draggingCircle: Circle? = null
    private var offsetX = 0f
    private var offsetY = 0f
    private var gameFinished : Boolean = false

    private val theRect = RectF(350f, 1700f, 750f, 2100f)
    private val paintRect = Paint().apply {
        color = rectColor
    }

    private val circlePaint = Paint()

    init {
        createCircles()
    }

    private fun createCircles() {
        circles.clear()
        val max = Random.nextInt(from = 5, until = 10)
        for (i in 0 until max) {
            val radius = 100f
            val x = Random.nextFloat() * (800 - 2 * radius) + radius
            val y = Random.nextFloat() * (1600 - 2 * radius) + radius
            val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            circles.add(Circle(x, y, radius, color))
        }
        rectColor = circles.firstOrNull()?.color ?: Color.RED
        paintRect.color = rectColor
        gameFinished = false
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(theRect, paintRect)

        for (circle in circles) {
            circlePaint.color = circle.color
            canvas.drawCircle(circle.x, circle.y, circle.radius, circlePaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameFinished) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                createCircles()
            }
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (circle in circles) {
                    if (circle.contains(event.x, event.y)) {
                        draggingCircle = circle
                        offsetX = event.x - circle.x
                        offsetY = event.y - circle.y
                        break
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                draggingCircle?.let {
                    it.x = event.x - offsetX
                    it.y = event.y - offsetY
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                draggingCircle?.let {
                    if (theRect.contains(it.x, it.y) && it.color == rectColor) {
                        circles.remove(it)
                        rectColor = circles.firstOrNull()?.color ?: Color.TRANSPARENT
                        paintRect.color = rectColor

                        if (circles.isEmpty()) {
                            Toast.makeText(context, "Game over, tap to continue", Toast.LENGTH_LONG).show()
                            gameFinished = true
                        }
                    }
                    draggingCircle = null
                    invalidate()
                }
            }
        }
        return true
    }
}

data class Circle(var x: Float, var y: Float, val radius: Float, val color: Int) {
    fun contains(px: Float, py: Float): Boolean {
        val dx = px - x
        val dy = py - y
        return dx * dx + dy * dy <= radius * radius
    }
}
