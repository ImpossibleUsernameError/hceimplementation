package com.bachelorarbeit.peyerl.michael.android.hceimplementation

/**
 * Created by Michael on 01.02.2018.
 */
class EmvUtils {

    companion object {

        const val FIRST_REC = "first_record"
        const val LAST_REC = "last_record"
        const val RECORDS_INVOLVED_IN_OFFLINE_AUTHENTICATION = "offline_auth_records"

        fun processResponse(response: String, cardData: CardData) {
            var response = response
            var tag = response.nextByte()
            response = response.removeNextByte()
            val length: String
            when {
                tag == "80" -> {
                    response = response.removeNextByte()
                    val aip = response.nextBytes(2)
                    cardData.tlvs["82"] = aip
                    response = response.removeNextBytes(2)
                    while (response.isNotEmpty()){
                        val afl = response.nextBytes(4)
                        response = response.removeNextBytes(4)
                        var counter = 0
                        var tagToSave = "94"
                        while (cardData.tlvs.keys.contains(tagToSave)){
                            tagToSave += "-${++counter}"
                        }
                        cardData.tlvs[tagToSave] = afl
                    }
                    return
                }
                Tags.contains(tag) -> {
                    length = response.nextByte()
                    response = response.removeNextByte()
                }
                tag in arrayOf("9F", "5F", "BF") -> {
                    tag += response.nextByte()
                    response = response.removeNextByte()
                    length = response.nextByte()
                    response = response.removeNextByte()
                }
                else -> {
                    length = "00"
                }
            }
            val value = response.nextBytes(length.toInt(16))
            var tagToSave = tag
            var counter = 0
            while (cardData.tlvs.keys.contains(tagToSave)){
                tagToSave += "-${++counter}"
            }
            cardData.tlvs[tagToSave] = value
            response = when (tag) {
               "6F", "A5", "77", "61", "80", "70" -> {
                    value
                }
                else -> {
                    response.removeNextBytes(length.toInt(16))
                }
            }
            if(response.length > 5 && length != "00"){
                processResponse(response, cardData)
            }
        }

        fun mapTagToMeaning(tag: String): String {
            return when {
                Tags.contains(tag) -> Tags.nameOf(tag)!!
                else -> "Unknown Option $tag"
            }
        }

        fun parsePdol(pdol: String?): Map<String, String> {
            if(pdol == null){
                return mutableMapOf<String, String>()
            }
            val result = mutableMapOf<String, String>()
            var pdol = pdol
            while (pdol != ""){
                var tag = pdol!!.nextByte()
                pdol = pdol.removeNextByte()
                var length: String
                if(Tags.PDOL_TAGS.contains(tag)){
                    length = pdol.nextByte()
                    pdol = pdol.removeNextByte()
                    result[tag] = length
                }
                else if (pdol != "") {
                    tag += pdol.nextByte()
                    pdol = pdol.removeNextByte()
                    length = pdol.nextByte()
                    pdol = pdol.removeNextByte()
                    if(Tags.PDOL_TAGS.contains(tag)){
                        result[tag] = length
                    }
                    else{
                        throw IllegalArgumentException("Unknown Tag $tag")
                    }
                }
            }
            return result
        }

        fun generatePdolString(pdol: Map<String, String>): String {
            var pdolData = ""
            var length = 2
            pdol.forEach {
                pdolData += it.value
                length += Integer.parseInt(it.value, 16)
                repeat(Integer.parseInt(it.value, 16) * 2) {
                    pdolData += "0"
                }
            }
            return "${Utils.convertNumberToTwoDigitHexString(length)}83${pdolData}00"
        }

        fun parseAfl(afl: String): List<Map<String, String>> {
            var afl = afl
            val result: MutableList<Map<String, String>> = mutableListOf()
            while (afl.isNotEmpty()) {
                var sfi = Utils.convertNumberToTwoDigitHexString(Integer.parseInt(afl.nextByte(), 16).and(248).shr(3))
                afl = afl.removeNextByte()
                val first_record = afl.nextByte()
                afl = afl.removeNextByte()
                val last_record = afl.nextByte()
                afl = afl.removeNextByte()
                result.add(mapOf(Pair("88", sfi), Pair(FIRST_REC, first_record), Pair(LAST_REC, last_record),
                        Pair(RECORDS_INVOLVED_IN_OFFLINE_AUTHENTICATION, afl.nextByte())))
                afl = afl.removeNextByte()
            }
            return result
        }
    }
}