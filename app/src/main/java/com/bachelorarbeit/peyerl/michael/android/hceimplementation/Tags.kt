package com.bachelorarbeit.peyerl.michael.android.hceimplementation
/**
 * Created by Michael on 29.01.2018.
 */
class Tags {

    companion object {

        private val tags = mapOf<String, String>(
        Pair("4F", "ADF Name"),
        Pair("50", "Application Label"),
        Pair("57", "Track 2 Equivalent Data"),
        Pair("5A", "Primary Account Number (PAN)"),
        Pair("61", "PSE Record"),
        Pair("6F", "FCI Template"),
        Pair("70", "READ RECORD Response"),
        Pair("77", "GPO Response Format 2"),
        Pair("80", "GPO Response Format 1"),
        Pair("82", "Application Interchange Profile (AIP)"),
        Pair("84", "DF Name"),
        Pair("87", "Application Priority Indicator"),
        Pair("88", "Short File Indicator (SFI)"),
        Pair("8C", "CDOL 1"),
        Pair("8D", "CDOL 2"),
        Pair("8E", "Cardholder Verification Method List"),
        Pair("94", "Application File Locator"),
        Pair("A5", "FCI Proprietary Template"),
        Pair("5F20", "Cardholder Name"),
        Pair("5F24", "Application Expiration Date"),
        Pair("5F25", "Application Effective Date"),
        Pair("5F28", "Issuer Country Code"),
        Pair("5F30", "Service Code"),
        Pair("5F34", "PAN Sequence Number"),
        Pair("5F57", "Account Type"),
        Pair("5F2D", "Language Preference"),
        Pair("9000", "StatusWord Ok"),
        Pair("9F07", "Application Usage Control"),
        Pair("9F08", "Application Version Number"),
        Pair("9F0D", "Issuer Action Code - Default"),
        Pair("9F0E", "Issuer Action Code - Denial"),
        Pair("9F0F", "Issuer Action Code - Online"),
        Pair("9F11", "Issuer Code Table Index"),
        Pair("9F12", "Application Preferred Name"),
        Pair("9F32", "Issuer Public Key Exponent"),
        Pair("9F38", "PDOL"),
        Pair("9F42", "Application Currency Code"),
        Pair("9F43", "Application Reference Currency Code"),
        Pair("9F44", "Application Currency Exponent"),
        Pair("9F46", "ICC Public Key Certificate"),
        Pair("9F47", "ICC Public Key Exponent"),
        Pair("9F48", "ICC Public Key Remainder"),
        Pair("9F49", "DDOL"),
        Pair("9F4A", "Satic Data Authentication Tag List"),
        Pair("9F4B", "Signed Dynamic Application Data"),
        Pair("9F4D", "Log Entry"),
        Pair("9F5F", "DS Slot Availability"),
        Pair("9F6F", "DS Slot Management Control"),
        Pair("9F7D", "DS Summary 1"),
        Pair("9F7F", "DS Unpredictable Number"),
        Pair("BF0C", "FCI Issuer Discretionary Data"))

        const val SFI = "88"
        const val ADF_NAME = "4F"
        const val PDOL = "9F38"
        const val APPLICATION_INTERCHANGE_PROFILE = "82"
        const val APPLICATION_FILE_LOCATOR = "94"
        const val STATUSWORD_OK = "9000"
        val PDOL_TAGS = arrayOf("9F5C")

        private val transformable = arrayOf("50", "5F2D", "84", "9F12")
        val transformFunctions: MutableMap<String, (String?) -> String> = mutableMapOf()


        private fun transformHexToAscii(element: String?): String {
            if(element == null){
                return ""
            }
            var element: String = element
            val result = StringBuilder()
            while(element.isNotEmpty()) {
                result.append(Integer.parseInt(element.nextByte(), 16).toChar())
                element = element.removeNextByte()
            }
            return result.toString()
        }

        private fun group4Bytes(element: String?): String {
            if(element == null){
                return ""
            }
            var element: String = element
            val result = StringBuilder()
            while(element.isNotEmpty()){
                result.append(element.nextBytes(4) + " ")
                element = element.removeNextBytes(4)
            }
            return result.toString()
        }

        fun isTransformable(tag: String) = transformFunctions.keys.contains(tag)

        fun transform(tag: String, value: String) = if (transformFunctions[tag] != null) transformFunctions[tag]!!(value) else value

        fun contains(tag: String) = tags.keys.contains(tag)

        fun nameOf(tag: String) = tags[tag]

        init {
            transformable.associateTo(transformFunctions) { it to ::transformHexToAscii }
            transformFunctions["94"] = ::group4Bytes
        }
    }
}