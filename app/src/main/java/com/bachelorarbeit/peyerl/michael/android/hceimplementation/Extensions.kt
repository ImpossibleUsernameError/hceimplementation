package com.bachelorarbeit.peyerl.michael.android.hceimplementation

/**
 * Created by Michael on 28.01.2018.
 */

fun String.nextByte() = substring(0..1)

fun String.removeNextByte() = removeRange(0..1)

fun String.nextBytes(number: Int) = substring(0..(number*2-1))

fun String.removeNextBytes(number: Int) = removeRange(0..(number*2-1))

