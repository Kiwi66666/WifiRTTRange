package com.example.wifirtt.ui.data

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.updateLayoutParams

class PositionView(context: Context, attrs: AttributeSet? = null): View(context, attrs)  {
    private var routers: MutableList<Point> = ArrayList()
    private lateinit var point: Point
    private var paint = Paint()

    override fun onDraw(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawColor(Color.BLUE)
        routers.forEach{
            canvas.drawCircle(it.x, it.y, it.radius, paint)
        }
        if(point.color) {
            paint.color = Color.BLUE
        } else {
            paint.color = Color.GREEN
        }
        canvas.drawCircle(point.x, point.y, point.radius, paint)
            super.onDraw(canvas)
    }
    fun setPoint(p: Point) {
        this.point = p
        invalidate()
    }
    fun addRouter(r: MutableList<Point>) {
        this.routers = r
    }
}