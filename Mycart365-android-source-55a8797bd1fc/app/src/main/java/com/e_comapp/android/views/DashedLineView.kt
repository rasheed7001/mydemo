package com.e_comapp.android.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.e_comapp.android.R

class DashedLineView : View {
    private var density = 0f
    private var paint: Paint? = null
    private var path: Path? = null
    private var effects: PathEffect? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        density = resources.displayMetrics.density
        paint = Paint()
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = density * 4
        //set your own color
        paint!!.color = context.resources.getColor(R.color.doted_line_grey)
        path = Path()
        //array is ON and OFF distances in px (4px line then 2px space)
        effects = DashPathEffect(floatArrayOf(10f, 6f, 10f, 6f), 0F)
    }

    fun setColor(color: Int) {
        paint!!.color = color
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas)
        paint!!.pathEffect = effects
        val measuredHeight = measuredHeight
        val measuredWidth = measuredWidth
        if (measuredHeight <= measuredWidth) {
            // horizontal
            path!!.moveTo(0f, 0f)
            path!!.lineTo(measuredWidth.toFloat(), 0f)
            canvas.drawPath(path, paint)
        } else {
            // vertical
            path!!.moveTo(0f, 0f)
            path!!.lineTo(0f, measuredHeight.toFloat())
            canvas.drawPath(path, paint)
        }
    }
}