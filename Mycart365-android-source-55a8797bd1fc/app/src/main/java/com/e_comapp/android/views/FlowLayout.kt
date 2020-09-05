package com.e_comapp.android.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.e_comapp.android.R

class FlowLayout : ViewGroup {
    private var horizontalSpacing = 0
    private var verticalSpacing = 0
    private var orientation = 0
    private var debugDraw = false

    constructor(context: Context) : super(context) {
        readStyleParameters(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        readStyleParameters(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        readStyleParameters(context, attributeSet)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - this.paddingRight - this.paddingLeft
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec) - this.paddingTop - this.paddingBottom
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)
        val size: Int
        val mode: Int
        if (orientation == HORIZONTAL) {
            size = sizeWidth
            mode = modeWidth
        } else {
            size = sizeHeight
            mode = modeHeight
        }
        var lineThicknessWithSpacing = 0
        var lineThickness = 0
        var lineLengthWithSpacing = 0
        var lineLength: Int
        var prevLinePosition = 0
        var controlMaxLength = 0
        var controlMaxThickness = 0
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            val lp = child.layoutParams as LayoutParams
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, this.paddingLeft + this.paddingRight, lp.width),
                    getChildMeasureSpec(heightMeasureSpec, this.paddingTop + this.paddingBottom, lp.height))
            val hSpacing = getHorizontalSpacing(lp)
            val vSpacing = getVerticalSpacing(lp)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            var childLength: Int
            var childThickness: Int
            var spacingLength: Int
            var spacingThickness: Int
            if (orientation == HORIZONTAL) {
                childLength = childWidth
                childThickness = childHeight
                spacingLength = hSpacing
                spacingThickness = vSpacing
            } else {
                childLength = childHeight
                childThickness = childWidth
                spacingLength = vSpacing
                spacingThickness = hSpacing
            }
            lineLength = lineLengthWithSpacing + childLength
            lineLengthWithSpacing = lineLength + spacingLength
            val newLine = lp.newLine || mode != MeasureSpec.UNSPECIFIED && lineLength > size
            if (newLine) {
                prevLinePosition = prevLinePosition + lineThicknessWithSpacing
                lineThickness = childThickness
                lineLength = childLength
                lineThicknessWithSpacing = childThickness + spacingThickness
                lineLengthWithSpacing = lineLength + spacingLength
            }
            lineThicknessWithSpacing = Math.max(lineThicknessWithSpacing, childThickness + spacingThickness)
            lineThickness = Math.max(lineThickness, childThickness)
            var posX: Int
            var posY: Int
            if (orientation == HORIZONTAL) {
                posX = paddingLeft + lineLength - childLength
                posY = paddingTop + prevLinePosition
            } else {
                posX = paddingLeft + prevLinePosition
                posY = paddingTop + lineLength - childHeight
            }
            lp.setPosition(posX, posY)
            controlMaxLength = Math.max(controlMaxLength, lineLength)
            controlMaxThickness = prevLinePosition + lineThickness
        }

        /* need to take paddings into account */if (orientation == HORIZONTAL) {
            controlMaxLength += paddingLeft + paddingRight
            controlMaxThickness += paddingBottom + paddingTop
        } else {
            controlMaxLength += paddingBottom + paddingTop
            controlMaxThickness += paddingLeft + paddingRight
        }
        if (orientation == HORIZONTAL) {
            setMeasuredDimension(View.resolveSize(controlMaxLength, widthMeasureSpec),
                    View.resolveSize(controlMaxThickness, heightMeasureSpec))
        } else {
            setMeasuredDimension(View.resolveSize(controlMaxThickness, widthMeasureSpec),
                    View.resolveSize(controlMaxLength, heightMeasureSpec))
        }
    }

    private fun getVerticalSpacing(lp: LayoutParams): Int {
        val vSpacing: Int
        vSpacing = if (lp.verticalSpacingSpecified()) {
            lp.verticalSpacing
        } else {
            verticalSpacing
        }
        return vSpacing
    }

    private fun getHorizontalSpacing(lp: LayoutParams): Int {
        val hSpacing: Int
        hSpacing = if (lp.horizontalSpacingSpecified()) {
            lp.horizontalSpacing
        } else {
            horizontalSpacing
        }
        return hSpacing
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val lp = child.layoutParams as LayoutParams
            child.layout(lp.x, lp.y, lp.x + child.measuredWidth, lp.y + child.measuredHeight)
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val more = super.drawChild(canvas, child, drawingTime)
        drawDebugInfo(canvas, child)
        return more
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attributeSet: AttributeSet): LayoutParams {
        return LayoutParams(context, attributeSet)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    private fun readStyleParameters(context: Context, attributeSet: AttributeSet?) {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout)
        try {
            horizontalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpacing, 0)
            verticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpacing, 0)
            orientation = a.getInteger(R.styleable.FlowLayout_orientation, HORIZONTAL)
            debugDraw = a.getBoolean(R.styleable.FlowLayout_debugDraw, false)
        } finally {
            a.recycle()
        }
    }

    private fun drawDebugInfo(canvas: Canvas, child: View) {
        if (!debugDraw) {
            return
        }
        val childPaint = createPaint(-0x100)
        val layoutPaint = createPaint(-0xff0100)
        val newLinePaint = createPaint(-0x10000)
        val lp = child.layoutParams as LayoutParams
        if (lp.horizontalSpacing > 0) {
            val x = child.right.toFloat()
            val y = child.top + child.height / 2.0f
            canvas.drawLine(x, y, x + lp.horizontalSpacing, y, childPaint)
            canvas.drawLine(x + lp.horizontalSpacing - 4.0f, y - 4.0f, x + lp.horizontalSpacing, y, childPaint)
            canvas.drawLine(x + lp.horizontalSpacing - 4.0f, y + 4.0f, x + lp.horizontalSpacing, y, childPaint)
        } else if (horizontalSpacing > 0) {
            val x = child.right.toFloat()
            val y = child.top + child.height / 2.0f
            canvas.drawLine(x, y, x + horizontalSpacing, y, layoutPaint)
            canvas.drawLine(x + horizontalSpacing - 4.0f, y - 4.0f, x + horizontalSpacing, y, layoutPaint)
            canvas.drawLine(x + horizontalSpacing - 4.0f, y + 4.0f, x + horizontalSpacing, y, layoutPaint)
        }
        if (lp.verticalSpacing > 0) {
            val x = child.left + child.width / 2.0f
            val y = child.bottom.toFloat()
            canvas.drawLine(x, y, x, y + lp.verticalSpacing, childPaint)
            canvas.drawLine(x - 4.0f, y + lp.verticalSpacing - 4.0f, x, y + lp.verticalSpacing, childPaint)
            canvas.drawLine(x + 4.0f, y + lp.verticalSpacing - 4.0f, x, y + lp.verticalSpacing, childPaint)
        } else if (verticalSpacing > 0) {
            val x = child.left + child.width / 2.0f
            val y = child.bottom.toFloat()
            canvas.drawLine(x, y, x, y + verticalSpacing, layoutPaint)
            canvas.drawLine(x - 4.0f, y + verticalSpacing - 4.0f, x, y + verticalSpacing, layoutPaint)
            canvas.drawLine(x + 4.0f, y + verticalSpacing - 4.0f, x, y + verticalSpacing, layoutPaint)
        }
        if (lp.newLine) {
            if (orientation == HORIZONTAL) {
                val x = child.left.toFloat()
                val y = child.top + child.height / 2.0f
                canvas.drawLine(x, y - 6.0f, x, y + 6.0f, newLinePaint)
            } else {
                val x = child.left + child.width / 2.0f
                val y = child.top.toFloat()
                canvas.drawLine(x - 6.0f, y, x + 6.0f, y, newLinePaint)
            }
        }
    }

    private fun createPaint(color: Int): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color
        paint.strokeWidth = 2.0f
        return paint
    }

    class LayoutParams : ViewGroup.LayoutParams {
        var x = 0
        var y = 0
        var horizontalSpacing = NO_SPACING
        var verticalSpacing = NO_SPACING
        var newLine = false

        constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
            readStyleParameters(context, attributeSet)
        }

        constructor(width: Int, height: Int) : super(width, height) {}
        constructor(layoutParams: ViewGroup.LayoutParams?) : super(layoutParams) {}

        fun horizontalSpacingSpecified(): Boolean {
            return horizontalSpacing != NO_SPACING
        }

        fun verticalSpacingSpecified(): Boolean {
            return verticalSpacing != NO_SPACING
        }

        fun setPosition(x: Int, y: Int) {
            this.x = x
            this.y = y
        }

        private fun readStyleParameters(context: Context, attributeSet: AttributeSet) {
            val a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout_LayoutParams)
            try {
                horizontalSpacing = a.getDimensionPixelSize(
                        R.styleable.FlowLayout_LayoutParams_layout_horizontalSpacing, NO_SPACING)
                verticalSpacing = a.getDimensionPixelSize(R.styleable.FlowLayout_LayoutParams_layout_verticalSpacing,
                        NO_SPACING)
                newLine = a.getBoolean(R.styleable.FlowLayout_LayoutParams_layout_newLine, false)
            } finally {
                a.recycle()
            }
        }

        companion object {
            private const val NO_SPACING = -1
        }
    }

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }
}