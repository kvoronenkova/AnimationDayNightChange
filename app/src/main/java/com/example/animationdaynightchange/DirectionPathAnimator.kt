package com.example.animationdaynightchange

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.PathMeasure
import android.view.animation.LinearInterpolator

class DirectionPathAnimator(
    private val listener: ValueAnimator.AnimatorUpdateListener,
    private val repoLocal: RepoLocal
) {

    private val valueAnimator = ValueAnimator()
    private var startAnimationTime = 0L
    private var restAnimationTime = 0L

    init {
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                startAnimationTime = System.currentTimeMillis()
            }

            override fun onAnimationEnd(animation: Animator) {
                restAnimationTime = 0L
                repoLocal.isDayDirectionAnimation = !repoLocal.isDayDirectionAnimation
                valueAnimator.apply {
                    duration = when {
                        repoLocal.isDayDirectionAnimation -> repoLocal.timeIntervalDay
                        else -> repoLocal.timeIntervalDays - repoLocal.timeIntervalDay
                    }
                    if (repoLocal.isChangeDirectionPathAnimation) {
                        repoLocal.isChangeDirectionPathAnimation = false
                        setFloatValues(repoLocal.startPath, repoLocal.endPath)
                    }
                    start()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    fun startAnimation(pathMeasure: PathMeasure, x: Float) {
        repoLocal.apply {
            if (isChangeDirectionPathAnimation) {
                endPath = startPath.also { startPath = endPath }
                valueAnimator.setFloatValues(x + 150F, endPath)
            } else {
                startPath = 0F
                endPath = pathMeasure.length
                valueAnimator.setFloatValues(startPath, endPath)
            }
        }

        valueAnimator.run {
            addUpdateListener(listener)
            duration = if (repoLocal.isChangeDirectionPathAnimation)
                restAnimationTime + System.currentTimeMillis() - startAnimationTime
            else
                repoLocal.timeIntervalDay
            interpolator = LinearInterpolator()
            startAnimationTime = System.currentTimeMillis()
            restAnimationTime = if (repoLocal.isDayDirectionAnimation)
                repoLocal.timeIntervalDay - duration
            else
                repoLocal.timeIntervalDays - repoLocal.timeIntervalDay - duration
            start()
        }
    }
}