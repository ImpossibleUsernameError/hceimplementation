package com.bachelorarbeit.peyerl.michael.android.relaycomm.server

import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

/**
 * Created by Michael on 22.02.2018.
 */
class ListenerThread(val serverSocket: ServerSocket, val messageHandler: ServerMessageHandler): Runnable {

    var exit = false
    var running = false
    var handlers = mutableListOf<SocketHandler>()
    val threadPool = Executors.newCachedThreadPool()
    private val TAG = "ListenerThread"

    override fun run() {
        running = true
        var socket: Socket? = null
        while(!exit){
           Log.i(TAG, "Waiting for Client Connects...")
           try {
               socket = serverSocket.accept()
               Log.i(TAG, "Connected Client: IP: ${socket.inetAddress}, Port: ${socket.port}")
               val socketHandler = SocketHandler(socket, messageHandler)
               handlers.add(socketHandler)
               Log.i(TAG, "Starting Client Handling")
               threadPool.execute(socketHandler)
           }
           catch (e: IOException){
                e.printStackTrace()
           }
        }
        if(socket != null && !socket.isClosed){
            socket.close()
        }
    }

    fun shutdown(){
        Log.i(TAG, "Shutdown ListenerThread")
        exit = true

        handlers.forEach{
            it.shutdown()
        }
        threadPool.shutdown()
        serverSocket.close()
        running = false
        Thread.currentThread().interrupt()
    }
}