package com.bachelorarbeit.peyerl.michael.android.relaycomm.client

/**
 * Created by Michael on 22.02.2018.
 */
interface ServerResponseHandler {

    fun processServerResponse(response: String)

    fun onConnectError()
}