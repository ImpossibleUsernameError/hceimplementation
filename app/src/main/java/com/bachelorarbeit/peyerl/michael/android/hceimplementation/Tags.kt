package com.bachelorarbeit.peyerl.michael.android.hceimplementation
/**
 * Created by Michael on 29.01.2018.
 */
class Tags {

    companion object {

        private val tags = mapOf<String, String>(

        )

        const val ADF_NAME = "4F"
        const val APPLICATION_LABEL = "50"
        const val TRACK_2_EQUIVALENT_DATA = "57"
        const val PRIMARY_ACCOUNT_NUMBER = "5A"
        const val PSE_RECORD = "61"
        const val FCI_TEMPLATE = "6F"
        const val READ_RECORD_RESPONSE = "70"
        const val GPO_RESPONSE_FORMAT2 = "77"
        const val GPO_RESPONSE_FORMAT1 = "80"
        const val APPLICATION_INTERCHANGE_PROFILE = "82"
        const val DF_NAME = "84"
        const val APPLICATION_PRIORITY_INDICATOR = "87"
        const val SFI = "88"
        const val CDOL1 = "8C"
        const val CDOL2 = "8D"
        const val CARDHOLDER_VERIFICATION_METHOD_LIST = "8E"
        const val APPLICATION_FILE_LOCATOR = "94"
        const val FCI_PROPRIETARY_TEMPLATE = "A5"
        const val CARDHOLDER_NAME = "5F20"
        const val APPLICATION_EXPIRATION_DATE = "5F24"
        const val APPLICATION_EFFECTIVE_DATE = "5F25"
        const val ISSUER_COUNTRY_CODE = "5F28"
        const val SERVICE_CODE = "5F30"
        const val PAN_SEQUENCE_NUMBER = "5F34"
        const val ACCOUNT_TYPE = "5F57"
        const val LANGUAGE_PREFERENCE = "5F2D"
        const val STATUSWORD_OK = "9000"
        const val APPLICATION_USAGE_CONTROL = "9F07"
        const val APPLICATION_VERSION_NR = "9F08"
        const val ISSUER_ACTION_CODE_DEFAULT = "9F0D"
        const val ISSUER_ACTION_CODE_DENIAL = "9F0E"
        const val ISSUER_ACTION_CODE_ONLINE = "9F0F"
        const val ISSUER_CODE_TABLE_INDEX = "9F11"
        const val APPLICATION_PREFERRED_NAME = "9F12"
        const val ISSUER_PUBLIC_KEY_EXPONENT = "9F32"
        const val PDOL = "9F38"
        const val APPLICATION_CURRENCY_CODE = "9F42"
        const val APPLICATION_REFERENCE_CURRENCY_EXPONENT = "9F43"
        const val APPLICATION_CURRENCY_EXPONENT = "9F44"
        const val ICC_PUBLIC_KEY_CERTIFICATE = "9F46"
        const val ICC_PUBLIC_KEY_EXPONENT = "9F47"
        const val ICC_PUBLIC_KEY_REMAINDER = "9F48"
        const val DYNAMIC_DATA_AUTHENTICATION_DATA_OBJECT_LIST = "9F49"
        const val STATIC_AUTHENTICATION_TAG_LIST = "9F4A"
        const val SIGNED_DYNAMIC_APPLICATION_DATA = "9F4B"
        const val LOG_ENTRY = "9F4D"
        const val DS_ODS_CARD = "9F54"
        val PDOL_TAGS = arrayOf("9F5C")
        const val DS_SLOT_AVAILABILITY = "9F5F"
        const val DS_SLOT_MANAGEMENT_CONTROL = "9F6F"
        const val DS_SUMMARY_1 = "9F7D"
        const val DS_UNPREDICTABLE_NR = "9F7F"
        const val FCI_ISSUER_DISCRETIONARY_DATA = "BF0C"


        private val transformable = arrayOf(APPLICATION_LABEL, LANGUAGE_PREFERENCE, DF_NAME, APPLICATION_PREFERRED_NAME)
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

        init {
            transformable.associateTo(transformFunctions) { it to ::transformHexToAscii }
            transformFunctions[Tags.APPLICATION_FILE_LOCATOR] = ::group4Bytes
        }
    }
}