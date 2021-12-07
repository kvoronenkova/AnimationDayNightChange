package com.example.animationdaynightchange

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myView = findViewById<MyView>(R.id.dayToNight)
        val animateStartButton = findViewById<MaterialButton>(R.id.animateStartButton)
        val timeDayEditText = findViewById<EditText>(R.id.timeDayEdit)
        val timeDaysEditText = findViewById<EditText>(R.id.timeDaysEdit)

        animateStartButton.setOnClickListener {
            animateStartButton.isEnabled = false
            if(timeDayEditText.text.toString().toLong() > timeDaysEditText.text.toString().toLong() )
                timeDayEditText.text = timeDaysEditText.text
            myView.animateStart(timeDayEditText.text.toString().toLong(), timeDaysEditText.text.toString().toLong(),false)
        }

        myView.setOnClickListener {
            myView.animateStart(
                timeDayEditText.text.toString().toLong(),
                timeDaysEditText.text.toString().toLong(),
                true
            )
        }
    }
}