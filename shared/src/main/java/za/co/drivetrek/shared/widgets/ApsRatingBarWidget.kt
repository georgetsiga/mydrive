package za.co.drivetrek.shared.widgets

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import za.co.drivetrek.shared.R

class ApsRatingBarWidget : AppCompatRatingBar {
    private val TAG = "ApsRatingBar"

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attributeSet: AttributeSet) : super(
        context,
        attributeSet
    ) {
        init(attributeSet, 0)
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet,
        defstyleattr: Int
    ) : super(context, attributeSet, defstyleattr) {
        init(attributeSet, defstyleattr)
    }

    private fun init(attrs: AttributeSet, defStyleAttr: Int) {
        setAttributes(attrs, defStyleAttr)
        setIsIndicator(true)
    }

    private fun setAttributes(
        attributeSet: AttributeSet,
        defStyleAttr: Int
    ) {
        val attributes = context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ColorStarRating, defStyleAttr, 0
        )
        val progressColor = attributes.getColor(
            R.styleable.ColorStarRating_dtk_orange,
            ContextCompat.getColor(context, R.color.dtk_orange)
        )
    }

    private fun setRatingStarColor(drawable: Drawable, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color)
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun setRatingProgressColor(resId: Int) {
        val stars = progressDrawable as LayerDrawable
        setRatingStarColor(
            DrawableCompat.wrap(stars.getDrawable(2)),
            resId
        )
        setRatingStarColor(
            DrawableCompat.wrap(stars.getDrawable(1)),
            resId
        )
    }

    fun setCustomRating(
        rating: Float,
        numStars: Int, @ColorInt color: Int
    ) {
        if (numStars >= rating) {
            this.numStars = numStars
            this.rating = rating
            setRatingProgressColor(color)
        } else {
            Log.d(TAG, "Rating is greater than number of stars")
        }
    }
}