package com.bachelorarbeit.peyerl.michael.android.relaycomm.server

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

/**
 * Created by Michael on 22.02.2018.
 */
class SocketHandler(val socket: Socket, val messageHandler: ServerMessageHandler): Runnable {

    var exit = false
    private val TAG = "SocketHandler"

    override fun run() {
        Log.i(TAG, "Ready to read client messages")
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        var line = input.readLine()
        var msg = ""
        while((line) != null && !exit){
            msg += line
            line = input.readLine()
        }
        Log.i(TAG, "Received Message from Client: $msg")
        val response = messageHandler.handleMessage(msg)
        Log.i(TAG, "Sending Response: $response")
        val output = PrintWriter(socket.getOutputStream(), true)
        try {
            output.println(response)
            output.close()
        }
        catch (e: IOException){
            e.printStackTrace()
        }
        finally {
            try{
                output.close()
            }
            catch (e: IOException){}
        }
    }

    fun shutdown(){
        Log.i(TAG, "Shutdown SocketHandler")
        exit = true
        Thread.currentThread().interrupt()
    }
}