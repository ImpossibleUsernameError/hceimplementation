package com.bachelorarbeit.peyerl.michael.android.relaycomm.client

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

/**
 * Created by Michael on 22.02.2018.
 */
class ServerConnection(val address: String, val port: Int, val serverResponseHandler: ServerResponseHandler): Runnable {

    private var socket: Socket? = null
    var exit = false
    private var thread: Thread
    private var connected = false
    private val TAG = "ServerConnection"

    init {
        if(socket == null){
            Thread{
                try {
                    val inetAddress = InetAddress.getByName(address)
                    socket = Socket(inetAddress, port)
                    connected = true
                    Log.i(TAG, "Successfully established connection to Server (IP: $address, Port: $port")
                }
                catch (e: Exception){
                    serverResponseHandler.onConnectError()
                    e.printStackTrace()
                }
            }.start()
        }
        thread = Thread(this)
        thread.start()
    }

    override fun run() {
        while(socket != null && !exit && connected){
            Log.i(TAG, "Waiting for Server Responses...")
            var msgFromServer = ""
            val input = BufferedReader(InputStreamReader(socket?.getInputStream()))
            var line = input.readLine()
            while(line != null){
                msgFromServer += line
                line = input.readLine()
            }
            serverResponseHandler.processServerResponse(msgFromServer)
            input.close()
        }
    }

    fun sendMessage(msg: String){
        if (socket != null && connected){
            Log.i(TAG, "Send Message to Server: $msg")
            val writer = PrintWriter(socket?.getOutputStream(), true)
            writer.println(msg)
        }
    }

    fun shutdown() {
        Log.i(TAG, "Shutdown ServerConnection")
        exit = true
        connected = false
        if(socket != null && !(socket?.isClosed ?: true)) {
            socket?.close()
        }
        thread.interrupt()
    }
}