package com.example.animationdaynightchange

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.util.Log


class MyView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var x = 0
    private var y = 300

    private var startPathValue = 0F
    private var endPathValue = 0F

    private var isChangeDirection = false

    private val horizonPaint = Paint()
    private val sunAndMoonPaint = Paint()
    private val path = Path()
    private val valueDirectionAnimator = ValueAnimator()

    private val repoLocal: RepoLocal = RepoLocalImpl()

    private var pathMeasure: PathMeasure = PathMeasure()
    private var timeStartAnimationChangeDirection = 0L
    private var restOfTimeAnimationChangeDirection = 0L

    private var colorTo = Color.rgb(135, 206, 235)
    private var colorFrom = Color.rgb(25, 25, 112)
    private val pos = FloatArray(2)

    private val listener = ValueAnimator.AnimatorUpdateListener { animation ->
        val v = animation?.animatedValue as Float
        pathMeasure.getPosTan(v, pos, null)
        x = pos[0].toInt()
        y = pos[1].toInt()

    }

    private val listenerColorAnimator = ValueAnimator.AnimatorUpdateListener { animation ->
        setBackgroundColor(animation?.animatedValue as Int)
    }

    private val colorValueAnimator = ColorValueAnimator(
        colorTo, colorFrom, listenerColorAnimator, repoLocal
    )

    init {
        valueDirectionAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                timeStartAnimationChangeDirection = System.currentTimeMillis()
            }

            override fun onAnimationEnd(animation: Animator) {
                restOfTimeAnimationChangeDirection = 0L
                repoLocal.isDayDirectionAnimation = !repoLocal.isDayDirectionAnimation
                valueDirectionAnimator.apply {
                    duration = when {
                        repoLocal.isDayDirectionAnimation -> repoLocal.timeIntervalDay
                        else -> repoLocal.timeIntervalDays - repoLocal.timeIntervalDay
                    }
                    if (isChangeDirection) {
                        isChangeDirection = false
                        setFloatValues(startPathValue, endPathValue)
                    }
                    start()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawSunOrMoon(canvas)
        drawHorizon(canvas)
        invalidate()
    }

    private fun drawHorizon(canvas: Canvas?) {
        val gradient = LinearGradient(
            0F,
            height - 300F,
            width.toFloat(),
            height.toFloat(),
            Color.GREEN,
            Color.CYAN,
            Shader.TileMode.CLAMP
        )

        horizonPaint.apply {
            style = Paint.Style.FILL
            isDither = true
            shader = gradient
        }

        canvas?.drawRect(0F, height - 300F, width.toFloat(), height.toFloat(), horizonPaint)
    }

    private fun drawSunOrMoon(canvas: Canvas?) {
        sunAndMoonPaint.apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }

        if (repoLocal.isDayDirectionAnimation) {
            drawSun(canvas)
        } else {
            drawMoon(canvas)
        }


    }

    private fun drawMoon(canvas: Canvas?) {

        val path = Path().apply {
            arcTo(
                RectF(
                    x + 30F,
                    y + height - 600F,
                    x + 160F,
                    y + height - 510F
                ), 80F, 180F
            )
            arcTo(
                RectF(
                    x.toFloat(),
                    y + height - 600F,
                    x + 130F,
                    y + height - 500F
                ), 270F, -220F
            )
        }
        canvas?.drawPath(path, sunAndMoonPaint)
    }

    private fun drawSun(canvas: Canvas?) {
        canvas?.drawCircle(x + 100F, y + height - 500F, 100F, sunAndMoonPaint)
    }


    fun animateStart(
        timeDay: Long = TIMER_INTERVAL,
        timeDays: Long = TIMER_INTERVAL,
        isChange: Boolean
    ) {
        repoLocal.apply {
            timeIntervalDay = timeDay
            timeIntervalDays = timeDays
            isChangeColorAnimation = isChange
            leftToRightFlags  = !leftToRightFlags
            leftFlag = x.toFloat() < (width.toFloat()) / 2
        }


        path.arcTo(RectF(0F, 0F, width.toFloat() - 200F, 600F), 180F, 180F)
        isChangeDirection = isChange
        pathMeasure = PathMeasure(path, false)

        setValuesDirectionValueAnimator(isChangeDirection)
        colorValueAnimator.setColorValuesAnimator()
        valueDirectionAnimator.run {

            duration = if (isChangeDirection)
                restOfTimeAnimationChangeDirection + System.currentTimeMillis() - timeStartAnimationChangeDirection
            else
                repoLocal.timeIntervalDay
            interpolator = LinearInterpolator()
            timeStartAnimationChangeDirection = System.currentTimeMillis()
            restOfTimeAnimationChangeDirection = if (repoLocal.isDayDirectionAnimation)
                repoLocal.timeIntervalDay - duration
            else
                repoLocal.timeIntervalDays - repoLocal.timeIntervalDay - duration
            start()
        }
    }

    private fun setValuesDirectionValueAnimator(isChangeDirection: Boolean) {
        if (isChangeDirection) {
            endPathValue = startPathValue.also { startPathValue = endPathValue }
            valueDirectionAnimator.setFloatValues(x + 150F, endPathValue)
        } else {
            startPathValue = 0F
            endPathValue = pathMeasure.length
            valueDirectionAnimator.setFloatValues(startPathValue, endPathValue)
        }

        valueDirectionAnimator.addUpdateListener(listener)
    }

    companion object {
        private const val TIMER_INTERVAL = 1000 * 24L
    }

}
