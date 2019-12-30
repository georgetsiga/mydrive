package za.co.drivetrek.shared.base

import android.view.animation.Animation
import android.view.animation.Transformation
import rx.subjects.PublishSubject
import za.co.drivetrek.shared.widgets.ProgressRingWidget

class ProgressRingAnimation(ring: ProgressRingWidget, newAngle: Float) :
    Animation() {
    val onClickSubject =
        PublishSubject.create<ProgressRingWidget>()
    protected var ring: ProgressRingWidget
    var triggeredListener = false
    protected var oldAngle: Float
    protected var newAngle: Float

    constructor(
        ring: ProgressRingWidget,
        newAngle: Float,
        forcedAnimation: Boolean
    ) : this(ring, newAngle) {
        if (forcedAnimation && this.duration == 0L) {
            this.duration = 1000
        }
    }

    override fun applyTransformation(
        interpolatedTime: Float,
        transformation: Transformation
    ) {
        val angle = oldAngle + (newAngle - oldAngle) * interpolatedTime
        ring.setProgress(angle)
        ring.requestLayout()
        val ringProgress = Math.round(ring.getProgress())
        if (ringProgress >= ring.getMaxValue()) {
            onClickSubject.onNext(ring)
        }
    }

    init {
        oldAngle = ring.getProgress()
        this.newAngle = newAngle
        this.ring = ring
        val animTime =
            (Math.abs(newAngle * ring.PROGRESS_TO_DEGREES_COEFFICIENT - ring.getProgress()) / 180.0f).toInt()
        this.duration = animTime * 1000.toLong()
    }
}