package com.bachelorarbeit.peyerl.michael.android.relaycomm.server

/**
 * Created by Michael on 22.02.2018.
 */
interface ServerMessageHandler {

    fun handleMessage(msg: String): String
}