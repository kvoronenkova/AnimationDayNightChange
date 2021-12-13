package com.example.animationdaynightchange

interface RepoLocal {

    var timeIntervalDay: Long
    var timeIntervalDays: Long
    var isDayDirectionAnimation: Boolean
    var isDayColorAnimation: Boolean
    var isChangeColorAnimation: Boolean
    var isChangeDirectionPathAnimation: Boolean

    var leftToRightFlags: Boolean
    var leftFlag: Boolean

    var startPath: Float
    var endPath: Float
}