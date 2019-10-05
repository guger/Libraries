/*
 * Copyright (c) 2019 - Daniel Guger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.guger.strokepiechart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Circular stroked pie chart.
 */
class StrokePieChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    //region Variables

    private val defaultSize: Int
    private val density: Float
    private var entriesSum: Float = 0.0f

    var animationDuration: Long = 300
    var distance: Float = 5.0f

    var roundEdges: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    var strokeWidth: Float = 6.0f
        set(value) {
            field = value
            invalidate()
        }
    @ColorInt
    var color: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }

    var text: String? = null
        set(value) {
            field = value
            invalidate()
        }
    var textSize: Float?
    @ColorInt
    var textColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }
    var typeface: Typeface? = null
        set(value) {
            field = value
            invalidate()
        }

    var chartRect: RectF = RectF()

    var entries: ArrayList<Float> = ArrayList()
        private set
    var colors: ArrayList<Int> = ArrayList()

    //endregion

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StrokePieChart)

        density = context.resources.displayMetrics.density
        defaultSize = (DEFAULT_SIZE * density).roundToInt()
        strokeWidth = typedArray.getDimension(R.styleable.StrokePieChart_strokeWidth, strokeWidth * density)
        roundEdges = typedArray.getBoolean(R.styleable.StrokePieChart_roundedEdges, false)
        text = typedArray.getString(R.styleable.StrokePieChart_text)
        textSize = typedArray.getDimension(R.styleable.StrokePieChart_textSize, 0.0f).takeIf { it > 0 }
        textColor = typedArray.getColor(R.styleable.StrokePieChart_textColor, Color.WHITE)

        val textAppearanceResId = typedArray.getResourceId(R.styleable.StrokePieChart_textAppearance, -1)
        typeface = textAppearanceResId.takeIf { it > 0 }?.let { ResourcesCompat.getFont(context, it) }

        color = typedArray.getColor(R.styleable.StrokePieChart_defaultColor, Color.BLACK)

        typedArray.recycle()
    }

    //region View

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSpec = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpec = MeasureSpec.getMode(heightMeasureSpec)

        val width = when (widthSpec) {
            MeasureSpec.AT_MOST -> defaultSize
            MeasureSpec.EXACTLY -> measuredWidth
            else -> 0
        }

        val height = when (heightSpec) {
            MeasureSpec.AT_MOST -> defaultSize
            MeasureSpec.EXACTLY -> measuredHeight
            else -> 0
        }

        val chartSize = min(width, height)
        val centerX = (width - chartSize) / 2
        val centerY = (height - chartSize) / 2
        val strokeHalf = strokeWidth / 2

        chartRect.set(
            centerX + strokeHalf,
            centerY + strokeHalf,
            centerX + chartSize - strokeHalf,
            centerY + chartSize - strokeHalf
        )
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (entries.isNotEmpty()) {
            check(colors.size == entries.size) { "Color and Stats list must have the same size." }

            entries.forEachIndexed { index, stat ->
                drawChart(
                    canvas,
                    colors[index],
                    if (index == 0) 0.0f else calculatePercents(entries.subList(0, index).sum()),
                    calculatePercents(stat)
                )
            }
        } else {
            drawChart(canvas, color, 0.0f, calculatePercents(FULL_CIRCLE_ANGLE))
        }

        drawText(canvas)
    }

    //endregion

    //region Methods

    fun setEntries(entriesList: ArrayList<Entry>) {
        entriesList.sortBy { it.value }

        entries = ArrayList()
        colors = ArrayList()

        for (stat in entriesList) {
            entries.add(stat.value)
            colors.add(stat.color)
        }

        entriesSum = entriesList.sumByDouble { it.value.toDouble() }.toFloat()
        invalidate()
    }

    fun startAnimation() {
        val animatedEntries = ArrayList(entries)

        val animatedDistance = distance

        ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()

            addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float

                entries.forEachIndexed { index, _ ->
                    entries[index] = animatedEntries[index] * value
                    distance = animatedDistance * value
                    invalidate()
                }
            }
        }.start()
    }

    private fun drawChart(canvas: Canvas?, @ColorInt color: Int, start: Float, degree: Float) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = strokeWidth
        paint.isAntiAlias = true
        if (roundEdges) paint.strokeCap = Paint.Cap.ROUND

        val offset = distance / 2

        val base = if (entries.size <= 1) 90.0f else 135.0f

        canvas?.drawArc(chartRect, base + start + offset, degree - offset, false, paint)
    }

    private fun drawText(canvas: Canvas?) {
        text?.let {
            val paint = Paint()
            paint.color = textColor
            paint.textSize = textSize ?: chartRect.height() / 3
            paint.typeface = typeface ?: Typeface.DEFAULT_BOLD
            paint.isAntiAlias = true

            val textBounds = Rect()
            paint.getTextBounds(it, 0, it.length, textBounds)
            paint.measureText(it)

            canvas?.drawText(it, (((width - textBounds.right) / 2).toFloat()), ((height + textBounds.height()) / 2).toFloat(), paint)
        }
    }

    private fun calculatePercents(value: Float) = FULL_CIRCLE_ANGLE / entriesSum * value

    //endregion

    companion object {
        private const val DEFAULT_SIZE = 100
        private const val FULL_CIRCLE_ANGLE = 360.0f
    }
}