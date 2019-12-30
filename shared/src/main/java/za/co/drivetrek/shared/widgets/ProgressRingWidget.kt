package za.co.drivetrek.shared.widgets

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import za.co.drivetrek.shared.R
import za.co.drivetrek.shared.base.ProgressRingAnimation

class ProgressRingWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {
    val INITIAL_ANGLE = 270
    val SWEEP_ANGLE = 360f
    private val ANIMATION_TIME = 2 //Time for one revolution in seconds
    private val DEFAULT_INNER_PADDING = 0
    private val DEFAULT_RING_WIDTH = 20
    private val DEFAULT_RING_GRADIENT_START = Color.BLACK
    private val DEFAULT_RING_GRADIENT_END = Color.WHITE
    private val DEFAULT_ZERO_PROGRESS = 0
    private val DEFAULT_RING_BACKGROUND_COLOR = Color.GRAY
    private val DEFAULT_ICON_SCALE = 0.8f
    private val dotPaint: Paint
    private val ringBackgroundPaint: Paint
    private val ringPaintGradient: Paint
    private val ringPaintReverseGradient: Paint
    private var iconScale = 0f
    private var maxValue = 100
    @JvmField
    var PROGRESS_TO_DEGREES_COEFFICIENT = SWEEP_ANGLE / maxValue
    private var iconDrawable: Drawable? = null
    private var rect: RectF? = null
    private var innerPadding = 0f
    private var ringWidth = 0f
    private var ringColorStart = 0
    private var ringColorEnd = 0
    protected var ringProgress = 0f
    private var ringBackgroundColor = 0
    private var cx: Float = 0f
    private var cy: Float = 0f
    private fun initPaints() { //Sets up our paint objects
        dotPaint.color = ringColorStart
        dotPaint.style = Paint.Style.FILL
        ringBackgroundPaint.color = ringBackgroundColor
        ringBackgroundPaint.style = Paint.Style.STROKE
        ringBackgroundPaint.strokeWidth = ringWidth
        ringPaintGradient.style = Paint.Style.STROKE
        ringPaintGradient.strokeWidth = ringWidth
        ringPaintGradient.strokeCap = Paint.Cap.ROUND
        ringPaintReverseGradient.style = Paint.Style.STROKE
        ringPaintReverseGradient.strokeWidth = ringWidth
        ringPaintReverseGradient.strokeCap = Paint.Cap.ROUND
    }

    private fun setupRingColor() {
        val gradient: Shader = SweepGradient(cx, cy, ringColorStart, ringColorEnd)
        val reverseGradient: Shader =
            SweepGradient(cx, cy, ringColorEnd, ringColorStart)
        val matrix = Matrix()
        matrix.postRotate(INITIAL_ANGLE.toFloat(), cx.toFloat(), cy.toFloat())
        gradient.setLocalMatrix(matrix)
        reverseGradient.setLocalMatrix(matrix)
        ringPaintGradient.shader = gradient
        ringPaintReverseGradient.shader = reverseGradient
    }

    private fun setupIconColor() {
        if (iconDrawable != null) {
            iconDrawable!!.colorFilter = PorterDuffColorFilter(-0x1f1f20, PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        val count = childCount
        //The position where we will draw our inner image
        val leftPos = (paddingLeft + ringWidth + innerPadding).toInt()
        val rightPos = (r - l - paddingRight - ringWidth - innerPadding).toInt()
        val parentTop = (paddingTop + ringWidth + innerPadding).toInt()
        val parentBottom = (b - t - paddingBottom - innerPadding - ringWidth).toInt()
        for (i in 0 until count) { //Draw everything on the same spot because we expect only one child
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                child.layout(leftPos, parentTop, rightPos, parentBottom)
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putFloat("ringProgress", ringProgress)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        var state: Parcelable? = state
        if (state is Bundle) // implicit null check
        {
            val bundle = state
            ringProgress = bundle.getFloat("ringProgress")
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(width, height, oldw, oldh)
        val radius = ringWidth / 2
        //The square that will be inscribed by our circle
        rect = RectF(
            paddingLeft + ringWidth / 2,
            paddingTop + ringWidth / 2,
            width - ringWidth / 2 - paddingRight,
            height - ringWidth / 2 - paddingBottom
        )
        //We set up our paint gradient again as its center has changed. We re-rotate the gradient using Android.graphics.Matrix
        cx = (width / 2).toFloat()
        cy = (height / 2).toFloat()
        setupRingColor()
        if (iconDrawable != null) {
            iconDrawable!!.bounds = calculateBounds(rect, iconScale, radius)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (ringProgress < SWEEP_ANGLE) { // Draw the background of the ring
            canvas.drawArc(rect!!, INITIAL_ANGLE.toFloat(), SWEEP_ANGLE, false, ringBackgroundPaint)
        }
        canvas.drawArc(rect!!, INITIAL_ANGLE.toFloat(), ringProgress, false, ringPaintGradient)
        if (ringProgress > SWEEP_ANGLE) {
            canvas.drawArc(
                rect!!,
                INITIAL_ANGLE.toFloat(),
                if (ringProgress % SWEEP_ANGLE == 0f) SWEEP_ANGLE else ringProgress % SWEEP_ANGLE,
                false,
                ringPaintReverseGradient
            )
        }
        dotPaint.color = ringColorEnd
        if (ringProgress / SWEEP_ANGLE % 2 < 1) {
            if (ringProgress <= SWEEP_ANGLE) {
                dotPaint.color = ringColorStart
            }
        }
        if (ringProgress == SWEEP_ANGLE) {
            dotPaint.color = ringColorStart
        }
        canvas.drawCircle(rect!!.centerX(), rect!!.top, ringWidth / 2, dotPaint)
        if (iconDrawable != null) {
            val radius = ringWidth / 2
            iconDrawable!!.bounds = calculateBounds(rect, iconScale, radius)
            iconDrawable!!.draw(canvas)
        }
    }

    private fun calculateBounds(
        rect: RectF?,
        iconScale: Float,
        radius: Float
    ): Rect {
        val scaleRadius = iconScale * radius
        return Rect(
            (rect!!.centerX() - scaleRadius).toInt(), (rect.top - scaleRadius).toInt(),
            (rect.centerX() + scaleRadius).toInt(), (rect.top + scaleRadius).toInt()
        )
    }

    fun getMaxValue(): Int {
        return maxValue
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        PROGRESS_TO_DEGREES_COEFFICIENT = SWEEP_ANGLE / maxValue
    }

    fun getRingColorStart(): Int {
        return ringColorStart
    }

    fun setRingColorStart(mRingColorStart: Int) {
        ringColorStart = mRingColorStart
        invalidate()
    }

    fun getRingColorEnd(): Int {
        return ringColorEnd
    }

    fun setRingColorEnd(mRingColorEnd: Int) {
        ringColorEnd = mRingColorEnd
        invalidate()
    }

    fun setIconDrawable(mIconDrawable: Drawable?) {
        iconDrawable = mIconDrawable
        invalidate()
    }

    fun setInnerPadding(mInnerPadding: Float) {
        innerPadding = mInnerPadding
        invalidate()
        requestLayout()
    }

    fun setRingWidth(mRingWidth: Float) {
        ringWidth = mRingWidth
        invalidate()
        requestLayout()
    }

    fun getProgress(): Float {
        return ringProgress / PROGRESS_TO_DEGREES_COEFFICIENT
    }

    fun setProgress(progress: Float) {
        this.ringProgress = progress * PROGRESS_TO_DEGREES_COEFFICIENT
        invalidate()
    }

    fun setProgressAnimated(mProgress: Float) {
        val animTime =
            (Math.abs(mProgress * PROGRESS_TO_DEGREES_COEFFICIENT - ringProgress) / (SWEEP_ANGLE / ANIMATION_TIME)).toInt()
        this.setProgressAnimated(mProgress, animTime * 1000)
    }

    private fun setProgressAnimated(mProgress: Float, duration: Int) {
        val animation =
            ProgressRingAnimation(this@ProgressRingWidget, mProgress)
        animation.duration = duration.toLong()
        startAnimation(animation)
    }

    fun setRingBackgroundColor(ringBackgroundColor: Int) {
        this.ringBackgroundColor = ringBackgroundColor
        invalidate()
    }

    init {
        setWillNotDraw(false) //Call my draw method
        val typedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ProgressRingWidget, 0, 0)
        try { //Sets our instance variables from styles defined in resource files
// Using Vector Drawables pre-Lollipop requires the VectorDrawableCompat to be implemented programmitically.
// (Yes even with the following flag set in the gradle.build: vectorDrawables.useSupportLibrary = true)
// https://stackoverflow.com/questions/41407811/android-vectordrawables-usesupportlibrary-true-is-stopping-app
            val iconDrawableResourceId = typedArray.getResourceId(
                R.styleable.ProgressRingWidget_aps_iconDrawable,
                R.drawable.empty
            )
            if (iconDrawableResourceId != R.drawable.empty) {
                iconDrawable = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    context.resources
                        .getDrawable(iconDrawableResourceId, context.theme)
                } else {
                    VectorDrawableCompat.create(
                        context.resources,
                        iconDrawableResourceId,
                        context.theme
                    )
                }
            }
            innerPadding = typedArray.getDimension(
                R.styleable.ProgressRingWidget_aps_innerPadding,
                DEFAULT_INNER_PADDING.toFloat()
            )
            ringWidth = typedArray.getDimension(
                R.styleable.ProgressRingWidget_aps_ringWidth,
                DEFAULT_RING_WIDTH.toFloat()
            )
            ringColorStart = typedArray.getColor(
                R.styleable.ProgressRingWidget_aps_ringColorStart,
                DEFAULT_RING_GRADIENT_START
            )
            ringColorEnd = typedArray.getColor(
                R.styleable.ProgressRingWidget_aps_ringColorEnd,
                DEFAULT_RING_GRADIENT_END
            )
            ringProgress = typedArray.getFloat(
                R.styleable.ProgressRingWidget_aps_progress,
                DEFAULT_ZERO_PROGRESS.toFloat()
            )
            ringBackgroundColor = typedArray.getColor(
                R.styleable.ProgressRingWidget_aps_ringBackgroundColor,
                DEFAULT_RING_BACKGROUND_COLOR
            )
            iconScale = typedArray.getFloat(
                R.styleable.ProgressRingWidget_aps_iconScale,
                DEFAULT_ICON_SCALE
            )
            maxValue = typedArray.getInteger(R.styleable.ProgressRingWidget_aps_maxValue, maxValue)
        } finally {
            typedArray.recycle()
        }
        dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        ringBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        ringPaintGradient = Paint()
        ringPaintReverseGradient = Paint()
        initPaints()
        setupIconColor()
    }
}