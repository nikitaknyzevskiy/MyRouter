package com.rokobit.myrouter.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import com.rokobit.myrouter.R

class OvalProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Float = 500f
        set(value) {
            field = value
            postInvalidate()
        }

    var maxProgress: Float = 100f

    private val padding = 25f

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {

        val height = width

        canvas?.save()

        canvas?.clipOutRect(0f, height / 2f, width.toFloat(), height.toFloat())

        canvas?.drawOval(
            padding,
            padding,
            width.toFloat() - padding,
            height.toFloat() - padding,
            Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = Color.DKGRAY
                strokeWidth = 40f
            }
        )

        canvas?.save()

        canvas?.clipRect(0f, 0f, (width.toFloat() / (maxProgress / progress)), height.toFloat())

        canvas?.drawOval(
            padding,
            padding,
            width.toFloat() - padding,
            height.toFloat() - padding,
            Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = context.getColor(R.color.colorPrimary)
                strokeWidth = 40f
            }
        )

        canvas?.restore()
        canvas?.restore()

        /*val textPaint = TextPaint().apply {
            isAntiAlias = true
            color = context.getColor(R.color.colorPrimary)
            textSize = 110f
        }

        canvas?.drawText(
            "$progress Mbps",
            ((width / 4f) - ((textPaint.descent() + textPaint.ascent()) / 4)),
            height / 2.5f,
            textPaint
        );*/

    }

}