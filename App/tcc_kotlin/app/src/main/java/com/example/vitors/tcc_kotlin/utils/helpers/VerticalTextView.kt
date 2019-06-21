package com.example.vitors.tcc_kotlin.utils.helpers

import android.content.Context
import android.graphics.Canvas
import java.util.Collections.rotate
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView


class VerticalTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {
    internal val topDown: Boolean

    init {
        val gravity = gravity
        if (Gravity.isVertical(gravity) && gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.BOTTOM) {
            setGravity(gravity and Gravity.HORIZONTAL_GRAVITY_MASK or Gravity.TOP)
            topDown = false
        } else
            topDown = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState

        canvas.save()

        if (topDown) {
            canvas.translate(width.toFloat(), 0.toFloat())
            canvas.rotate(90.toFloat())
        } else {
            canvas.translate(0.toFloat(), height.toFloat())
            canvas.rotate(-90.toFloat())
        }


        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())

        layout.draw(canvas)
        canvas.restore()
    }
}