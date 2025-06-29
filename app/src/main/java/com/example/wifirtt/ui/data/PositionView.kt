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
    private var position: Point = Point(0F,0F, 0F, true)
    private var paint = Paint()

    override fun onDraw(canvas: Canvas) {
        paint.color = Color.LTGRAY
        canvas.drawColor(Color.BLACK)
        canvas.drawRect(4F,4F,width.toFloat()-8F,height.toFloat()-8F,paint)
        paint.color = Color.RED
        routers.forEach{
            canvas.drawCircle(it.x, it.y, it.radius, paint)
        }
        if(point.color) {
            paint.color = Color.WHITE
        } else {
            paint.color = Color.GREEN
        }
        canvas.drawCircle(point.x, point.y, point.radius, paint)
        paint.color= Color.BLUE
        canvas.drawCircle(position.x, position.y, position.radius, paint)
        super.onDraw(canvas)
    }
    fun setPoint(p: Point) {
        this.point = p
        invalidate()
    }
    fun setPos(p: Point) {
        this.position = p
    }
    fun addRouter(r: MutableList<Point>) {
        this.routers = r
    }
}