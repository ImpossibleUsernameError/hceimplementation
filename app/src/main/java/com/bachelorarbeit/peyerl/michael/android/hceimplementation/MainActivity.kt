package com.bachelorarbeit.peyerl.michael.android.hceimplementation

import android.content.ComponentName
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bachelorarbeit.peyerl.michael.android.hceimplementation.hce.Aids
import com.bachelorarbeit.peyerl.michael.android.hceimplementation.readNfc.CardReaderActivity

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "isDefault: ${CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(this))
                .isDefaultServiceForAid(ComponentName("com.bachelorarbeit.peyerl.michael.android.hceimplementation.hce", "ApduService"),
                        Aids.MAESTRO_AID)}")

    }

    fun startCardReaderActivity(v: View){
        val intent = Intent(this, CardReaderActivity::class.java)
        startActivity(intent)
    }
}
