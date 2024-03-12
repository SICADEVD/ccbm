package ci.projccb.mobile.tools

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.os.Build

class Fill {
    enum class Type {
        EMPTY, COLOR, LINEAR_GRADIENT, DRAWABLE
    }

    enum class Direction {
        DOWN, UP, RIGHT, LEFT
    }

    /**
     * the type of fill
     */
    private var mType: Fill.Type = Fill.Type.EMPTY

    /**
     * the color that is used for filling
     */
    var color: Int? = null
        private set
    private var mFinalColor: Int? = null

    /**
     * the drawable to be used for filling
     */
    protected var mDrawable: Drawable? = null
    var gradientColors: IntArray? = null
    var gradientPositions: FloatArray? = null

    /**
     * transparency used for filling
     */
    private var mAlpha = 255

    constructor()
    constructor(color: Int) {
        mType = Fill.Type.COLOR
        this.color = color
        calculateFinalColor()
    }

    constructor(startColor: Int, endColor: Int) {
        mType = Fill.Type.LINEAR_GRADIENT
        gradientColors = intArrayOf(startColor, endColor)
    }

    constructor(gradientColors: IntArray) {
        mType = Fill.Type.LINEAR_GRADIENT
        this.gradientColors = gradientColors
    }

    constructor(gradientColors: IntArray, gradientPositions: FloatArray) {
        mType = Fill.Type.LINEAR_GRADIENT
        this.gradientColors = gradientColors
        this.gradientPositions = gradientPositions
    }

    constructor(drawable: Drawable) {
        mType = Fill.Type.DRAWABLE
        mDrawable = drawable
    }

    var type: Fill.Type
        get() = mType
        set(type) {
            mType = type
        }

    fun setColor(color: Int) {
        this.color = color
        calculateFinalColor()
    }

    fun setGradientColors(startColor: Int, endColor: Int) {
        gradientColors = intArrayOf(startColor, endColor)
    }

    var alpha: Int
        get() = mAlpha
        set(alpha) {
            mAlpha = alpha
            calculateFinalColor()
        }

    private fun calculateFinalColor() {
        mFinalColor = if (color == null) {
            null
        } else {
            val alpha = Math.floor((color!! shr 24) / 255.0 * (mAlpha / 255.0) * 255.0).toInt()
            alpha shl 24 or (color!! and 0xffffff)
        }
    }

    fun fillRect(
        c: Canvas, paint: Paint,
        left: Float, top: Float, right: Float, bottom: Float,
        gradientDirection: Fill.Direction
    ) {
        when (mType) {
            Fill.Type.EMPTY -> return
            Fill.Type.COLOR -> {
                if (mFinalColor == null) return
                if (isClipPathSupported) {
                    val save = c.save()
                    c.clipRect(left, top, right, bottom)
                    c.drawColor(mFinalColor!!)
                    c.restoreToCount(save)
                } else {
                    // save
                    val previous = paint.style
                    val previousColor = paint.color

                    // set
                    paint.style = Paint.Style.FILL
                    paint.color = mFinalColor!!
                    c.drawRect(left, top, right, bottom, paint)

                    // restore
                    paint.color = previousColor
                    paint.style = previous
                }
            }

            Fill.Type.LINEAR_GRADIENT -> {
                if (gradientColors == null) return
                val gradient = LinearGradient(
                    (if (gradientDirection == Fill.Direction.RIGHT) right else if (gradientDirection == Fill.Direction.LEFT) left else left).toInt()
                        .toFloat(),
                    (if (gradientDirection == Fill.Direction.UP) bottom else if (gradientDirection == Fill.Direction.DOWN) top else top).toInt()
                        .toFloat(),
                    (if (gradientDirection == Fill.Direction.RIGHT) left else if (gradientDirection == Fill.Direction.LEFT) right else left).toInt()
                        .toFloat(),
                    (if (gradientDirection == Fill.Direction.UP) top else if (gradientDirection == Fill.Direction.DOWN) bottom else top).toInt()
                        .toFloat(),
                    gradientColors!!,
                    gradientPositions,
                    Shader.TileMode.MIRROR
                )
                paint.shader = gradient
                c.drawRect(left, top, right, bottom, paint)
            }

            Fill.Type.DRAWABLE -> {
                if (mDrawable == null) return
                mDrawable!!.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                mDrawable!!.draw(c)
            }
        }
    }

    fun fillPath(
        c: Canvas, path: Path?, paint: Paint,
        clipRect: RectF?
    ) {
        when (mType) {
            Fill.Type.EMPTY -> return
            Fill.Type.COLOR -> {
                if (mFinalColor == null) return
                if (clipRect != null && isClipPathSupported) {
                    val save = c.save()
                    c.clipPath(path!!)
                    c.drawColor(mFinalColor!!)
                    c.restoreToCount(save)
                } else {
                    // save
                    val previous = paint.style
                    val previousColor = paint.color

                    // set
                    paint.style = Paint.Style.FILL
                    paint.color = mFinalColor!!
                    c.drawPath(path!!, paint)

                    // restore
                    paint.color = previousColor
                    paint.style = previous
                }
            }

            Fill.Type.LINEAR_GRADIENT -> {
                if (gradientColors == null) return
                val gradient = LinearGradient(
                    0f,
                    0f,
                    c.width.toFloat(),
                    c.height.toFloat(),
                    gradientColors!!,
                    gradientPositions,
                    Shader.TileMode.MIRROR
                )
                paint.shader = gradient
                c.drawPath(path!!, paint)
            }

            Fill.Type.DRAWABLE -> {
                if (mDrawable == null) return
                ensureClipPathSupported()
                val save = c.save()
                c.clipPath(path!!)
                mDrawable!!.setBounds(
                    clipRect?.left?.toInt() ?: 0,
                    clipRect?.top?.toInt() ?: 0,
                    clipRect?.right?.toInt() ?: c.width,
                    clipRect?.bottom?.toInt() ?: c.height
                )
                mDrawable!!.draw(c)
                c.restoreToCount(save)
            }
        }
    }

    private val isClipPathSupported: Boolean
        private get() = Build.VERSION.SDK_INT >= 18

    private fun ensureClipPathSupported() {
        if (Build.VERSION.SDK_INT < 18) {
            throw RuntimeException(
                "Fill-drawables not (yet) supported below API level 18, " +
                        "this code was run on API level " + Build.VERSION.SDK_INT + "."
            )
        }
    }
}
