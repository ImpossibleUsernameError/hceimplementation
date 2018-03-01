package com.bachelorarbeit.peyerl.michael.android.relaycomm

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.View
import com.bachelorarbeit.peyerl.michael.android.relaycomm.client.ServerConnection
import com.bachelorarbeit.peyerl.michael.android.relaycomm.client.ServerResponseHandler
import com.bachelorarbeit.peyerl.michael.android.relaycomm.server.ListenerThread
import com.bachelorarbeit.peyerl.michael.android.relaycomm.server.ServerMessageHandler
import kotlinx.android.synthetic.main.activity_main.*
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.*

class MainActivity : AppCompatActivity(), ServerMessageHandler, ServerResponseHandler {

    //todo: Make a service out of this activity

    private var listener: ListenerThread? = null
    private var serverConnection: ServerConnection? = null
    private var server = false
    private var client = false
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        server = savedInstanceState?.getBoolean("server") ?: server
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("server", server)
    }

    override fun handleMessage(msg: String): String {
        runOnUiThread {
            tv_main.append("$msg\n")
        }
        return "response"
    }

    override fun onPause() {
        super.onPause()
        resetListeners()
    }

    override fun onResume() {
        super.onResume()
        if(server){
            configureAsServer(View(this))
        }
        if(client){
            configureAsClient(View(this))
        }
    }

    fun configureAsServer(v: View){
        Log.i(TAG, "Configuring as server")
        resetListeners()
        runOnUiThread {
            ll_connectInfo.visibility = View.GONE
            btn_sendMsg.visibility = View.GONE
            edt_msgToServer.visibility = View.GONE
        }
        val serverSocket = ServerSocket(8888)
        listener = ListenerThread(serverSocket, this)
        Thread(listener).start()
        server = true
        client = false
        runOnUiThread {
            tv_main.append("IP Address: ${getIpAddress() ?: "No Info"}\n")
        }
    }

    fun configureAsClient(v: View){
        Log.i(TAG, "Configuring as client")
        resetListeners()
        if (listener != null && (listener?.running ?: false)){
            listener?.shutdown()
        }
        runOnUiThread {
            ll_connectInfo.visibility = View.VISIBLE
            edt_msgToServer.visibility = View.VISIBLE
            btn_sendMsg.visibility = View.VISIBLE
        }
        server = false
        client = true
    }

    fun connectToServer(v: View){
        Log.i(TAG, "Trying to connect to Server")
        runOnUiThread {
            ll_connectInfo.visibility = View.GONE
        }
        serverConnection = ServerConnection(edt_ip_address.text.toString(), Integer.parseInt(edt_port.text.toString()), this)
    }

    fun sendMessageToServer(v: View){
        val msg = edt_msgToServer.text.toString()
        runOnUiThread {
            tv_main.append("Sending to Server: $msg\n")
        }
        serverConnection?.sendMessage(msg)
    }

    override fun processServerResponse(response: String) {
        Log.i(TAG, "Received message from server: $response")
        runOnUiThread {
            tv_main.append("Response: $response\n")
        }
    }

    private fun resetListeners(){
        Log.i(TAG, "Reset Listeners")
        listener?.shutdown()
        serverConnection?.shutdown()
    }

    override fun onConnectError() {
        runOnUiThread {
            edt_msgToServer.visibility = View.GONE
            btn_sendMsg.visibility = View.GONE
            tv_main.append("Could not establish server connection or connection lost!\n")
        }
    }

    fun getIpAddress(): String? {
        if(isWifiConnected()){
            val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        }
        val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        interfaces.forEach {
            Collections.list(it.inetAddresses).forEach {
                if(!it.isLoopbackAddress){
                    val addr = it.hostAddress
                    val isIpv4 = !addr.contains(":", true)
                    return if(isIpv4){
                        addr.toUpperCase()
                    }
                    else {
                        val ipV6ZoneSuffix = addr.indexOf('%')
                        if (ipV6ZoneSuffix < 0) addr.toUpperCase() else addr.substring(0 until ipV6ZoneSuffix).toUpperCase()
                    }
                }
            }
        }
        return null
    }

    fun isWifiConnected(): Boolean {
        val cManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }



}