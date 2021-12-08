package com.example.animationdaynightchange

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class ColorValueAnimator(
    var colorTo: Int,
    var colorFrom: Int,
    private val listener: ValueAnimator.AnimatorUpdateListener,
    private val repoLocal: RepoLocal
) {

    private val valueAnimator = ValueAnimator.ofArgb(colorTo, colorFrom, colorTo)
    private var startAnimationTime = 0L
    private var restAnimationTime = 0L
    private var color: Int = 0

    init {
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                startAnimationTime = System.currentTimeMillis()
            }

            override fun onAnimationEnd(animation: Animator?) {
                restAnimationTime = 0L
                repoLocal.isDayColorAnimation = !repoLocal.isDayColorAnimation

                valueAnimator.run {
                    duration = if (repoLocal.isDayColorAnimation) {
                        valueAnimator.setIntValues(colorFrom, colorTo, colorFrom)
                        repoLocal.timeIntervalDay
                    } else {
                        valueAnimator.setIntValues(colorFrom, colorFrom)
                        repoLocal.timeIntervalDays - repoLocal.timeIntervalDay
                    }

                    if (repoLocal.isChangeColorAnimation) {
                        repoLocal.isChangeColorAnimation = false
                    }
                    start()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
    }


    fun setColorValuesAnimator() {
        when {
            repoLocal.isDayDirectionAnimation -> {
                if (repoLocal.isChangeColorAnimation) {
                    color = valueAnimator.animatedValue as Int
                    if (repoLocal.leftFlag && !repoLocal.leftToRightFlags || !repoLocal.leftFlag && repoLocal.leftToRightFlags) {
                        valueAnimator.setIntValues(color, colorFrom)
                    } else {
                        valueAnimator.setIntValues(color, colorTo, colorFrom)
                    }
                } else
                    valueAnimator.setIntValues(colorFrom, colorTo, colorFrom)
            }
            else -> valueAnimator.setIntValues(colorFrom, colorFrom)
        }

        valueAnimator.addUpdateListener(listener)

        valueAnimator.run {
            duration = if (repoLocal.isChangeColorAnimation) {
                restAnimationTime + System.currentTimeMillis() - startAnimationTime
            } else {
                when {
                    repoLocal.isDayColorAnimation -> repoLocal.timeIntervalDay
                    else -> repoLocal.timeIntervalDays - repoLocal.timeIntervalDay
                }
            }
            interpolator = LinearInterpolator()
            startAnimationTime = System.currentTimeMillis()
            restAnimationTime = when {
                repoLocal.isDayColorAnimation -> repoLocal.timeIntervalDay - duration
                else -> repoLocal.timeIntervalDays - repoLocal.timeIntervalDay - duration
            }
            start()

        }
    }

}