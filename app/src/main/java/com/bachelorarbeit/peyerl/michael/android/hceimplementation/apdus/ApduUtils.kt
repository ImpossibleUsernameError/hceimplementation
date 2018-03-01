package com.bachelorarbeit.peyerl.michael.android.hceimplementation.apdus

import com.bachelorarbeit.peyerl.michael.android.hceimplementation.*

/**
 * Created by Michael on 20.02.2018.
 */
class ApduUtils {

    companion object {

        fun parseCommandApdu(apdu: ByteArray): CommandApdu {
            var apduString = Utils.toHexString(apdu)
            val cla = apduString.nextByte()
            apduString = apduString.removeNextByte()
            val ins = apduString.nextByte()
            apduString = apduString.removeNextByte()
            val p1 = apduString.nextByte()
            apduString = apduString.removeNextByte()
            val p2 = apduString.nextByte()
            apduString = apduString.removeNextByte()

            var l = ""
            if(apduString.isBlank()){
                return CommandApdu(cla, ins, p1, p2)
            }
            while (apduString.isNotBlank() && Utils.convertHexToInt(l) != apduString.length/2-1) {
                l += apduString.nextByte()
                apduString = apduString.removeNextByte()
            }
            val length = Utils.convertHexToInt(l)
            val data = apduString.nextBytes(length)
            apduString = apduString.removeNextBytes(length)
            val le = apduString.nextByte()
            apduString = apduString.removeNextByte()
            assert(apduString.isBlank())
            return CommandApdu(cla, ins, p1, p2, l, data, le)
        }
    }
}