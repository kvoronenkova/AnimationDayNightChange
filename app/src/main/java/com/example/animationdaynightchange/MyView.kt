package com.example.animationdaynightchange

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class MyView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var x = 0
    private var y = 300

    private val horizonPaint = Paint()
    private val sunAndMoonPaint = Paint()
    private val path = Path()

    private val repoLocal: RepoLocal = RepoLocalImpl()

    private var pathMeasure: PathMeasure = PathMeasure()

    private var colorTo = Color.rgb(135, 206, 235)
    private var colorFrom = Color.rgb(25, 25, 112)
    private val pos = FloatArray(2)

    private val listenerDirectionPathAnimator = ValueAnimator.AnimatorUpdateListener { animation ->
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

    private val directionPathAnimator = DirectionPathAnimator(listenerDirectionPathAnimator, repoLocal)

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
            isChangeDirectionPathAnimation = isChange
            leftToRightFlags  = !leftToRightFlags
            leftFlag = x.toFloat() < (width.toFloat()) / 2
        }

        path.arcTo(RectF(0F, 0F, width.toFloat() - 200F, 600F), 180F, 180F)
        pathMeasure = PathMeasure(path, false)

        colorValueAnimator.startAnimation()
        directionPathAnimator.startAnimation(pathMeasure, x.toFloat())

    }

    companion object {
        private const val TIMER_INTERVAL = 1000 * 24L
    }

}
