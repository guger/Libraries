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

package at.guger.circularimagebutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.ImageView.ScaleType.CENTER_CROP
import android.widget.ImageView.ScaleType.CENTER_INSIDE
import androidx.appcompat.widget.AppCompatImageButton
import kotlin.math.min

/**
 * [AppCompatImageButton] featuring a circular shape.
 *
 * @author Daniel Guger
 * @version 1.0
 */
class CircularImageButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageButton(context, attrs, defStyleAttr) {

    //region Variables

    //region Properties

    private var mBorderWidth: Float = 0.toFloat()
    private var mCanvasSize: Int = 0
    private var mShadowRadius: Float = 0.toFloat()
    private var mShadowColor = Color.BLACK
    private var mShadowGravity = ShadowGravity.BOTTOM
    private var mColorFilter: ColorFilter? = null

    //endregion

    //region Objects for drawing

    private var mImage: Bitmap? = null
    private var mImageDrawable: Drawable? = null
    private var mPaint = Paint().apply { isAntiAlias = true }
    private var mPaintBorder = Paint().apply { isAntiAlias = true }
    private var mPaintBackground = Paint().apply { isAntiAlias = true }

    //endregion

    //endregion

    init {
        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularImageButton, defStyleAttr, 0)

        // Init Border
        if (attributes.getBoolean(R.styleable.CircularImageButton_civ_border, true)) {
            val defaultBorderSize = DEFAULT_BORDER_WIDTH * getContext().resources.displayMetrics.density
            setBorderWidth(attributes.getDimension(R.styleable.CircularImageButton_civ_border_width, defaultBorderSize))
            setBorderColor(attributes.getColor(R.styleable.CircularImageButton_civ_border_color, Color.WHITE))
        }

        setBackgroundColor(attributes.getColor(R.styleable.CircularImageButton_civ_background_color, Color.WHITE))

        // Init Shadow
        if (attributes.getBoolean(R.styleable.CircularImageButton_civ_shadow, false)) {
            mShadowRadius = DEFAULT_SHADOW_RADIUS
            drawShadow(
                attributes.getFloat(R.styleable.CircularImageButton_civ_shadow_radius, mShadowRadius),
                attributes.getColor(R.styleable.CircularImageButton_civ_shadow_color, mShadowColor)
            )
            val shadowGravityIntValue = attributes.getInteger(R.styleable.CircularImageButton_civ_shadow_gravity, ShadowGravity.BOTTOM.value)
            mShadowGravity = ShadowGravity.fromValue(shadowGravityIntValue)
        }

        attributes.recycle()
    }

    //region Set Attr Method

    fun setBorderWidth(borderWidth: Float) {
        this.mBorderWidth = borderWidth
        requestLayout()
        invalidate()
    }

    fun setBorderColor(borderColor: Int) {
        mPaintBorder.color = borderColor
        invalidate()
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        mPaintBackground.color = backgroundColor
        invalidate()
    }

    fun addShadow() {
        if (mShadowRadius == 0f) mShadowRadius = DEFAULT_SHADOW_RADIUS

        drawShadow(mShadowRadius, mShadowColor)
        invalidate()
    }

    fun setShadowRadius(shadowRadius: Float) {
        drawShadow(shadowRadius, mShadowColor)
        invalidate()
    }

    fun setShadowColor(shadowColor: Int) {
        drawShadow(mShadowRadius, shadowColor)
        invalidate()
    }

    fun setShadowGravity(shadowGravity: ShadowGravity) {
        this.mShadowGravity = shadowGravity
        invalidate()
    }

    override fun setColorFilter(colorFilter: ColorFilter) {
        if (this.mColorFilter === colorFilter)
            return
        this.mColorFilter = colorFilter
        mImageDrawable = null // To force re-update shader
        invalidate()
    }

    override fun getScaleType(): ImageView.ScaleType {
        val currentScaleType = super.getScaleType()
        return if (currentScaleType == null || currentScaleType != CENTER_INSIDE) CENTER_CROP else currentScaleType
    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        if (scaleType != CENTER_CROP && scaleType != CENTER_INSIDE) {
            throw IllegalArgumentException(
                String.format(
                    "ScaleType %s not supported. " + "Just ScaleType.CENTER_CROP & ScaleType.CENTER_INSIDE are available for this library.",
                    scaleType
                )
            )
        } else {
            super.setScaleType(scaleType)
        }
    }

    //endregion

    //region Draw Method

    @SuppressLint("CanvasSize")
    public override fun onDraw(canvas: Canvas) {
        // Load the bitmap
        loadBitmap()

        // Check if mImage isn't null
        if (mImage == null)
            return

        if (!isInEditMode) {
            mCanvasSize = min(canvas.width, canvas.height)
        }

        // circleCenter is the x or y of the view's center
        // radius is the radius in pixels of the cirle to be drawn
        // mPaint contains the shader that will texture the shape
        val circleCenter = (mCanvasSize - mBorderWidth * 2).toInt() / 2
        val margeWithShadowRadius = mShadowRadius * 2

        // Draw Border
        canvas.drawCircle(circleCenter + mBorderWidth, circleCenter + mBorderWidth, circleCenter + mBorderWidth - margeWithShadowRadius, mPaintBorder)
        // Draw Circle background
        canvas.drawCircle(circleCenter + mBorderWidth, circleCenter + mBorderWidth, circleCenter - margeWithShadowRadius, mPaintBackground)
        // Draw CircularImageButton
        canvas.drawCircle(circleCenter + mBorderWidth, circleCenter + mBorderWidth, circleCenter - margeWithShadowRadius, mPaint)
    }

    private fun loadBitmap() {
        if (mImageDrawable === drawable)
            return

        mImageDrawable = drawable
        mImage = drawableToBitmap(mImageDrawable)
        updateShader()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasSize = Math.min(w, h)
        if (mImage != null)
            updateShader()
    }

    private fun drawShadow(shadowRadius: Float, shadowColor: Int) {
        this.mShadowRadius = shadowRadius
        this.mShadowColor = shadowColor
        setLayerType(View.LAYER_TYPE_SOFTWARE, mPaintBorder)

        var dx = 0.0f
        var dy = 0.0f

        when (mShadowGravity) {
            CircularImageButton.ShadowGravity.CENTER -> {
                dx = 0.0f
                dy = 0.0f
            }
            CircularImageButton.ShadowGravity.TOP -> {
                dx = 0.0f
                dy = -shadowRadius / 2
            }
            CircularImageButton.ShadowGravity.BOTTOM -> {
                dx = 0.0f
                dy = shadowRadius / 2
            }
            CircularImageButton.ShadowGravity.START -> {
                dx = -shadowRadius / 2
                dy = 0.0f
            }
            CircularImageButton.ShadowGravity.END -> {
                dx = shadowRadius / 2
                dy = 0.0f
            }
        }

        mPaintBorder.setShadowLayer(shadowRadius, dx, dy, shadowColor)
    }

    private fun updateShader() {
        if (mImage == null)
            return

        // Create Shader
        val shader = BitmapShader(mImage!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Center Image in Shader
        val scale: Float
        var dx = 0f
        var dy = 0f

        when (scaleType) {
            CENTER_CROP -> if (mImage!!.width * height > width * mImage!!.height) {
                scale = (height - (paddingTop + paddingBottom)) / mImage!!.height.toFloat()
                dx = (width - mImage!!.width * scale) * 0.5f
                dy = (paddingTop + paddingBottom) * 0.5f
            } else {
                scale = (width - (paddingStart + paddingEnd)) / mImage!!.width.toFloat()
                dx = (paddingStart + paddingEnd) * 0.5f
                dy = (height - mImage!!.height * scale) * 0.5f
            }
            CENTER_INSIDE -> if (mImage!!.width * height < width * mImage!!.height) {
                scale = height / mImage!!.height.toFloat()
                dx = (width - mImage!!.width * scale) * 0.5f
            } else {
                scale = width / mImage!!.width.toFloat()
                dy = (height - mImage!!.height * scale) * 0.5f
            }
            else -> throw IllegalStateException("ScaleType must be CENTER_CROP or CENTER_INSIDE.")
        }

        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(dx, dy)
        }
        shader.setLocalMatrix(matrix)

        // Set Shader in Paint
        mPaint.shader = shader

        // Apply mColorFilter
        mPaint.colorFilter = mColorFilter
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        return try {
            // Create Bitmap object out of the mImageDrawable
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
    //endregion

    //region Measure Method

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = if (widthMeasureSpec >= heightMeasureSpec) widthMeasureSpec else heightMeasureSpec
        super.onMeasure(size, size)
    }

    private fun measureHeight(measureSpecHeight: Int): Int {
        val result: Int
        val specMode = View.MeasureSpec.getMode(measureSpecHeight)
        val specSize = View.MeasureSpec.getSize(measureSpecHeight)

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = mCanvasSize
        }

        return result + 2
    }

    //endregion

    enum class ShadowGravity {
        CENTER,
        TOP,
        BOTTOM,
        START,
        END;

        val value: Int
            get() {
                return when (this) {
                    CENTER -> 1
                    TOP -> 2
                    BOTTOM -> 3
                    START -> 4
                    END -> 5
                }
            }

        companion object {

            fun fromValue(value: Int): ShadowGravity {
                when (value) {
                    1 -> return CENTER
                    2 -> return TOP
                    3 -> return BOTTOM
                    4 -> return START
                    5 -> return END
                }
                throw IllegalArgumentException("This value is not supported for ShadowGravity: $value")
            }
        }

    }

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 4f
        private const val DEFAULT_SHADOW_RADIUS = 8.0f
    }
}
