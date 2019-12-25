package za.co.drivetrek.shared.widgets

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import za.co.drivetrek.shared.R

class PercentageDialWidget : View {
    protected var dialProgressPaint: Paint? = null
    protected var dialPaint: Paint? = null
    protected var percentageTextPaint: Paint? = null
    protected var labelPaint: Paint? = null
    protected var descriptionText: String? = null
    protected var percentageText: String? = null
    protected var discoveryAngle = 0f
    protected var dialCenterPoint: Point? = null
    protected var paddingSize = 0

    constructor(context: Context?) : super(context) {
        init(null, null, null, null, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.PercentageDial, 0, 0)
        val percentageTextColor =
            attributes.getColorStateList(R.styleable.PercentageDial_percentageTextColor)
        val labelTextColor =
            attributes.getColorStateList(R.styleable.PercentageDial_labelTextColor)
        val dialColor =
            attributes.getColorStateList(R.styleable.PercentageDial_dialColor)
        val dialProgressColor =
            attributes.getColorStateList(R.styleable.PercentageDial_dialProgressColor)
        val labelText = attributes.getString(R.styleable.PercentageDial_labelText)
        init(percentageTextColor, labelTextColor, dialColor, dialProgressColor, labelText)
    }

    private fun init(
        percentageTextColor: ColorStateList?,
        labelTextColor: ColorStateList?,
        dialColor: ColorStateList?,
        dialProgressColor: ColorStateList?,
        labelText: String?
    ) { // stuff needed to draw the dial
        dialCenterPoint = Point()
        paddingSize = resources.getDimension(R.dimen.dial_padding).toInt()
        val selfPaymentArcWidth = resources.getDimension(R.dimen.other_arc_width)
        val discoveryArcWidth =
            resources.getDimension(R.dimen.drivetrek_arc_width)
        // the text that is displayed
        descriptionText =
            labelText ?: resources.getString(R.string.lessons_completed)
        percentageText = "0"
        percentageTextPaint = createPercentageTextPaint(percentageTextColor)
        labelPaint = createLabelTextPaint(labelTextColor)
        dialPaint = createDialPaint(dialColor, selfPaymentArcWidth)
        dialProgressPaint = createProgressDialPaint(dialProgressColor, discoveryArcWidth)
    }

    fun setDiscoveryAngles(discoveryAngle: Float) {
        this.discoveryAngle = discoveryAngle
        invalidate()
    }

    private fun createProgressDialPaint(
        color: ColorStateList?,
        arcWidth: Float
    ): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color?.defaultColor ?: resources.getColor(R.color.colorAccent)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = arcWidth
        return paint
    }

    private fun createDialPaint(
        color: ColorStateList?,
        arcWidth: Float
    ): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color =
            color?.defaultColor ?: resources.getColor(R.color.colorPrimaryDark)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = arcWidth
        return paint
    }

    private fun createLabelTextPaint(color: ColorStateList?): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color?.defaultColor ?: resources.getColor(R.color.buttonText)
        paint.textSize = resources.getDimension(R.dimen.other_text_size)
        paint.textAlign = Align.CENTER
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 2f
        return paint
    }

    private fun createPercentageTextPaint(color: ColorStateList?): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = color?.defaultColor ?: resources.getColor(R.color.colorAccent)
        paint.textSize = resources.getDimension(R.dimen.drivetrek_text_size)
        paint.textAlign = Align.CENTER
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 2f
        return paint
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measureWidth(widthMeasureSpec),
            measureHeight(heightMeasureSpec)
        )
        dialCenterPoint!!.x = width / 2
        dialCenterPoint!!.y = height / 2
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private fun measureWidth(measureSpec: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) { // We were told how big to be
            result = specSize
        } else { // Measure the text
            result = specSize
            if (specMode == MeasureSpec.AT_MOST) { // Respect AT_MOST value if that was what is called for by
// measureSpec
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private fun measureHeight(measureSpec: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = specSize
            if (specMode == MeasureSpec.AT_MOST) { // Respect AT_MOST value if that was what is called for by
// measureSpec
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        val height = height.toFloat()
        val width = width.toFloat()
        dialCenterPoint!!.x = (width / 2).toInt()
        dialCenterPoint!!.y = (height / 2).toInt()
        val ascent: Float = (percentageTextPaint!!.ascent() * 0.30).toFloat()
        val descriptionAscent: Float = (labelPaint!!.ascent())
        var radius = if (width < height) width / 2 else height / 2
        radius -= paddingSize.toFloat()
        val discoveryRect = RectF()
        discoveryRect[dialCenterPoint!!.x - radius, dialCenterPoint!!.y - radius, dialCenterPoint!!.x + radius] =
            dialCenterPoint!!.y + radius
        canvas.drawArc(discoveryRect, 270.0f, -360f, false, dialPaint!!)
        canvas.drawArc(discoveryRect, 270.0f, discoveryAngle, false, dialProgressPaint!!)
        canvas.drawText(
            "$percentageText%", dialCenterPoint!!.x.toFloat(),
            dialCenterPoint!!.y - ascent, percentageTextPaint!!
        )
        canvas.drawText(
            descriptionText!!, dialCenterPoint!!.x.toFloat(),
            dialCenterPoint!!.y - ascent - descriptionAscent + 5, labelPaint!!
        )
        super.onDraw(canvas)
    }

    fun animateDial(percentage: Int) {
        discoveryAngle = 0f
        percentageText = Integer.toString(percentage)
        val targetAngle = percentage / 100.0f * 360
        val fadeInDialViewBackgroundRing: ObjectAnimator
        val dailGroupAnimationSet: AnimatorSet
        fadeInDialViewBackgroundRing = ObjectAnimator.ofFloat(this, "discoveryAngle", targetAngle)
        dailGroupAnimationSet = AnimatorSet()
        dailGroupAnimationSet.playTogether(fadeInDialViewBackgroundRing)
        dailGroupAnimationSet.startDelay = 500
        dailGroupAnimationSet.duration = 3000
        dailGroupAnimationSet.start()
    }
}