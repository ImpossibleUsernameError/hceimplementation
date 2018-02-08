package com.bachelorarbeit.peyerl.michael.android.hceimplementation

/**
 * Created by Michael on 27.01.2018.
 */
class Utils {

    companion object {

        private val HEX_CHARS = "0123456789ABCDEF"
        private val HEX_CHARS_ARRAY = "0123456789ABCDEF".toCharArray()

        fun hexStringToByteArray(data: String) : ByteArray {

            val result = ByteArray(data.length / 2)

            for (i in 0 until data.length step 2) {
                val firstIndex = HEX_CHARS.indexOf(data[i])
                val secondIndex = HEX_CHARS.indexOf(data[i + 1])

                val octet = firstIndex.shl(4).or(secondIndex)
                result[i.shr(1)] = octet.toByte()
            }

            return result
        }

        fun toHexString(byteArray: ByteArray) : String {
            val result = StringBuffer()

            byteArray.forEach {
                val octet = it.toInt()
                val firstIndex = (octet and 0xF0).ushr(4)
                val secondIndex = octet and 0x0F
                result.append(HEX_CHARS_ARRAY[firstIndex])
                result.append(HEX_CHARS_ARRAY[secondIndex])
            }

            return result.toString()
        }

        /**
         * The number given as input parameter must not be greater than 255
         */
        fun convertNumberToTwoDigitHexString(number: Int): String {
            var nr = number.toString(16).toUpperCase() //AID.length has to be an even number because it's composed of bytes
            if(nr.length == 1){
                nr = "0$nr"
            }
            return nr
        }
    }
}