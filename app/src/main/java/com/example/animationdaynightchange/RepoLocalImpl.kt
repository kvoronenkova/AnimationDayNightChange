package com.example.animationdaynightchange

class RepoLocalImpl : RepoLocal {

    override var timeIntervalDay: Long = 0
        set(value) {
            field = value * 1000L
        }

    override var timeIntervalDays: Long = 0L
        set(value) {
            field = value * 1000L
        }

    override var isDayDirectionAnimation: Boolean = true
    override var isDayColorAnimation: Boolean = true
    override var isChangeColorAnimation: Boolean = false
    override var leftToRightFlags: Boolean = false
    override var leftFlag: Boolean = true
}