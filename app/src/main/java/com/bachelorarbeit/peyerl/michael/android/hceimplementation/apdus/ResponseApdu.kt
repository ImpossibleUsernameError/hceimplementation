package com.bachelorarbeit.peyerl.michael.android.hceimplementation.apdus

/**
 * Created by Michael on 20.02.2018.
 */
data class ResponseApdu(val data: String, val statusWord: String){

    override fun toString(): String {
        return "$data$statusWord"
    }
}