package com.bachelorarbeit.peyerl.michael.android.hceimplementation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bachelorarbeit.peyerl.michael.android.hceimplementation.readNfc.CardReaderActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startCardReaderActivity(v: View){
        val intent = Intent(this, CardReaderActivity::class.java)
        startActivity(intent)
    }
}
