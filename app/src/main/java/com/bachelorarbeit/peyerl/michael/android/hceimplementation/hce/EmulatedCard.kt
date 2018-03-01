package com.bachelorarbeit.peyerl.michael.android.hceimplementation.hce

import android.util.Log
import com.bachelorarbeit.peyerl.michael.android.hceimplementation.apdus.CommandApdu
import com.bachelorarbeit.peyerl.michael.android.hceimplementation.apdus.ResponseApdu

/**
 * Created by Michael on 20.02.2018.
 */
class EmulatedCard {

    val TAG = "EmulatedCard"

    private val data: Map<CommandApdu, ResponseApdu> = hashMapOf(
        Pair(CommandApdu("00", "A4", "04", "00", "0E", "315041592E5359532E4444463031", "00"),
                ResponseApdu("6F2C840E325041592E5359532E4444463031A51ABF0C1761154F07A000000004306087010150074D41455354524F", "9000")),
        Pair(CommandApdu("00", "A4", "04", "00", "0E", "325041592E5359532E4444463031", "00"),
                ResponseApdu("6F2C840E325041592E5359532E4444463031A51ABF0C1761154F07A000000004306087010150074D41455354524F", "9000")),
        Pair(CommandApdu("00", "A4", "04", "00", "07", "A0000000043060", "00"),
                ResponseApdu("6F218407A0000000043060A51650074D41455354524F5F2D046465656E9F38039F5C08", "9000")),
        Pair(CommandApdu("80", "A8", "00", "00", "0A", "83080000000000000000", "00"),
                ResponseApdu("771682021980941008010303100102001004040020010101", "9000")),
        Pair(CommandApdu("00", "B2", "02", "0C", "00", "", "00"),
                ResponseApdu("70535F24032012315F25031609195A0A6700617042132983525F5F3401018C279F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F21039F7C148D0C910A8A0295059F37049F4C08", "9000"))
    )

    fun processCommand(apdu: CommandApdu): ResponseApdu {

        Log.i(TAG, "process Command: $apdu")
        return if(data.containsKey(apdu)) data[apdu]!! else ResponseApdu("", "6A86")
    }
}