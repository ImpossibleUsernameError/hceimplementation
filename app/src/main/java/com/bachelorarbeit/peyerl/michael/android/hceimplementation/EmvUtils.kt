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
            when (tag) {
                Tags.FCI_TEMPLATE,
                Tags.FCI_PROPRIETARY_TEMPLATE,
                Tags.DF_NAME,
                Tags.SFI,
                Tags.APPLICATION_LABEL,
                Tags.APPLICATION_PRIORITY_INDICATOR,
                Tags.READ_RECORD_RESPONSE,
                Tags.PSE_RECORD,
                Tags.ADF_NAME,
                Tags.GPO_RESPONSE_FORMAT2,
                Tags.APPLICATION_FILE_LOCATOR,
                Tags.APPLICATION_INTERCHANGE_PROFILE,
                Tags.TRACK_2_EQUIVALENT_DATA,
                Tags.PRIMARY_ACCOUNT_NUMBER,
                Tags.CDOL1,
                Tags.CDOL2,
                Tags.CARDHOLDER_VERIFICATION_METHOD_LIST -> {
                    length = response.nextByte()
                    response = response.removeNextByte()
                }
                Tags.GPO_RESPONSE_FORMAT1 -> {
                    response = response.removeNextByte()
                    val aip = response.nextBytes(2)
                    cardData.tlvs[Tags.APPLICATION_INTERCHANGE_PROFILE] = aip
                    response = response.removeNextBytes(2)
                    while (response.isNotEmpty()){
                        val afl = response.nextBytes(4)
                        response = response.removeNextBytes(4)
                        var counter = 0
                        var tagToSave = Tags.APPLICATION_FILE_LOCATOR
                        while (cardData.tlvs.keys.contains(tagToSave)){
                            tagToSave += "-${++counter}"
                        }
                        cardData.tlvs[tagToSave] = afl
                    }
                    return
                }
                "9F", "5F", "BF" -> {
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
                Tags.FCI_TEMPLATE,
                Tags.FCI_PROPRIETARY_TEMPLATE,
                Tags.READ_RECORD_RESPONSE,
                Tags.PSE_RECORD,
                Tags.GPO_RESPONSE_FORMAT1,
                Tags.GPO_RESPONSE_FORMAT2 -> {
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
            return when (tag) {
                Tags.FCI_TEMPLATE -> "FCI Template"
                Tags.DF_NAME -> "DF Name"
                Tags.FCI_PROPRIETARY_TEMPLATE -> "FCI Proprietary Template"
                Tags.SFI -> "SFI"
                Tags.APPLICATION_LABEL -> "Application Label"
                Tags.APPLICATION_PRIORITY_INDICATOR -> "Application Priority Indicator"
                Tags.PDOL -> "PDOL"
                Tags.APPLICATION_PREFERRED_NAME -> "Application Preferred Name"
                Tags.LANGUAGE_PREFERENCE -> "Language Preference"
                Tags.ISSUER_CODE_TABLE_INDEX -> "Issuer Code Table Index"
                Tags.FCI_ISSUER_DISCRETIONARY_DATA -> "FCI Issuer Discretionary Data"
                Tags.READ_RECORD_RESPONSE -> "Read Record Response"
                Tags.PSE_RECORD -> "PSE Record"
                Tags.ADF_NAME -> "ADF Name - Application Name (AID)"
                Tags.APPLICATION_INTERCHANGE_PROFILE -> "Application Interchange Profile (AIP)"
                Tags.APPLICATION_FILE_LOCATOR -> "Application File Locator"
                Tags.GPO_RESPONSE_FORMAT1 -> "GPO Response Format 1"
                Tags.GPO_RESPONSE_FORMAT2 -> "GPO Response Format 2"
                Tags.DS_ODS_CARD -> "DS ODS Card"
                Tags.DS_SLOT_AVAILABILITY -> "DS Slot Availability"
                Tags.DS_SLOT_MANAGEMENT_CONTROL -> "DS Slot Management Control"
                Tags.DS_SUMMARY_1 -> "DS Summary 1"
                Tags.DS_UNPREDICTABLE_NR -> "DS Unpredictable Number"
                Tags.APPLICATION_EXPIRATION_DATE -> "Application Expiration Date"
                Tags.CARDHOLDER_NAME -> "Cardholder Name"
                Tags.ACCOUNT_TYPE -> "Account Type"
                Tags.APPLICATION_EFFECTIVE_DATE -> "Application Effective Date"
                Tags.TRACK_2_EQUIVALENT_DATA -> "Track2 Equivalent Data"
                Tags.PRIMARY_ACCOUNT_NUMBER -> "Primary Account Nr (PAN)"
                Tags.ISSUER_COUNTRY_CODE -> "Issuer Country Code"
                Tags.SERVICE_CODE -> "Service Code"
                Tags.PAN_SEQUENCE_NUMBER -> "PAN Sequence Number"
                Tags.APPLICATION_CURRENCY_CODE -> "Application Currency Code"
                Tags.APPLICATION_REFERENCE_CURRENCY_EXPONENT -> "Application Reference Currency Code"
                Tags.APPLICATION_CURRENCY_EXPONENT -> "Application Currency Exponent"
                Tags.ICC_PUBLIC_KEY_CERTIFICATE -> "ICC Public Key Certificate"
                Tags.ICC_PUBLIC_KEY_EXPONENT -> "ICC Public Key Exponent"
                Tags.ICC_PUBLIC_KEY_REMAINDER -> "ICC Public Key Remainder"
                Tags.DYNAMIC_DATA_AUTHENTICATION_DATA_OBJECT_LIST -> "Dynamic Data Authentication Data Object List (DDOL)"
                Tags.STATIC_AUTHENTICATION_TAG_LIST -> "Static Authentication Tag List"
                Tags.SIGNED_DYNAMIC_APPLICATION_DATA -> "Signed Dynamic Application Data"
                Tags.CDOL1 -> "CDOL1"
                Tags.CDOL2 -> "CDOL2"
                Tags.CARDHOLDER_VERIFICATION_METHOD_LIST -> "Cardholder Verification Method List"
                Tags.APPLICATION_VERSION_NR -> "Application Version Number"
                Tags.ISSUER_PUBLIC_KEY_EXPONENT -> "Issuer Public Key Exponent"
                Tags.APPLICATION_USAGE_CONTROL -> "Application Usage Control"
                Tags.ISSUER_ACTION_CODE_DEFAULT -> "Issuer Action Code - Default"
                Tags.ISSUER_ACTION_CODE_DENIAL -> "Issuer Action Code - Denial"
                Tags.ISSUER_ACTION_CODE_ONLINE -> "Issuer Action Code - Online"
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
                result.add(mapOf(Pair(Tags.SFI, sfi), Pair(FIRST_REC, first_record), Pair(LAST_REC, last_record),
                        Pair(RECORDS_INVOLVED_IN_OFFLINE_AUTHENTICATION, afl.nextByte())))
                afl = afl.removeNextByte()
            }
            return result
        }
    }
}