package com.bachelorarbeit.peyerl.michael.android.hceimplementation

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

/**
 * Created by Michael on 27.01.2018.
 */
class ApduService: HostApduService() {

    private val TAG = "ApduService"
    private var messageCounter = 0
    val STATUS_SUCCESS = "9000"
    val STATUS_FAILED = "6F00"
    val CLA_NOT_SUPPORTED = "6E00"
    val INS_NOT_SUPPORTED = "6D00"
    //todo: add AID
    val AID = ""
    val SELECT_INS = "A4"
    val DEFAULT_CLA = "00"
    val MIN_APDU_LENGTH = 12

    override fun processCommandApdu(apdu: ByteArray?, extras: Bundle?): ByteArray {

        Log.i(TAG, "Received APDU: $apdu - String Representation: ${if (apdu != null) String(apdu) else "null"}")
        return getNextMessage().toByteArray()
    }

    override fun onDeactivated(reason: Int) {
        Log.i(TAG, "Deactivated for reason $reason")
    }

    private fun getNextMessage() = "Message ${messageCounter++} from Android"

}