package com.bachelorarbeit.peyerl.michael.android.hceimplementation

/**
 * Created by Michael on 29.01.2018.
 */
class CardData {

    var tlvs: MutableMap<String, String> = mutableMapOf()

    override fun toString(): String {
        val result = StringBuilder()
        val stringRep = tlvs.mapValues {
            if (Tags.isTransformable(it.key)){
                "${it.value}(${Tags.transform(it.key, it.value)})"
            }
            else {
                it.value
            }
        }
        stringRep.forEach{
            result.append("${EmvUtils.mapTagToMeaning(it.key.split("-")[0])}(${it.key}): ${it.value}\n")
        }
        return result.toString()
    }


}