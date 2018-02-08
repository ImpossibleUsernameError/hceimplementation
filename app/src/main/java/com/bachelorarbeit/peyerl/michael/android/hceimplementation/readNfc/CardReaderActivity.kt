package com.bachelorarbeit.peyerl.michael.android.hceimplementation.readNfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bachelorarbeit.peyerl.michael.android.hceimplementation.*
import kotlinx.android.synthetic.main.activity_cardreader.*


/**
 * Created by Michael on 27.01.2018.
 */
class CardReaderActivity: AppCompatActivity(), NfcAdapter.ReaderCallback {

    private val TAG = "CardReaderActivity"

    private var nfcAdapter: NfcAdapter? = null
    private val MEASTRO_DEBIT_AID = "A0000000043060"
    private val SELECT_AID_COMMAND = "00A40400"
    private val SELECT_PSE = "00A404000E315041592E5359532E4444463031"
    private var READ_RECORD_PARAMETER: String? = null
    private val READ_RECORD = "00B2"
    private val GET_PROCESSING_OPTIONS_COMMAND = "80A80000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardreader)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        if (!nfcAdapter!!.isEnabled) {
            val alert = AlertDialog.Builder(this)
            with(alert) {
                setTitle("Turn on NFC")
                setMessage("Turn on NFC now!")
                setPositiveButton("Turn On") { _, _ ->
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    startActivity(intent)
                }
                show()
            }
        }
        nfcAdapter?.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A
                or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()

        //-----------------Select the Payment System Environment (PSE)---------------------------------
        //This is not guaranteed to work since it is optional in EMV Standard

        var responseData = sendCommandToCard("SELECT PSE", SELECT_PSE, isoDep){
            return@sendCommandToCard null
        }
        responseData ?: return


        //---------------------------Sending READ RECORD command with 0 length--------------------------------

        /*
        Because we don't know the length we will get back from the record, we first have to send
        the command with an expected length of 0
        The card will send an error code back providing the correct length, we have to expect
         */

        val sfi = responseData.tlvs[Tags.SFI]
        if(sfi == null){
            runOnUiThread{ tv_card_messages.append("Processing Error: SFI not present")}
            return
        }

        val p2 = Utils.convertNumberToTwoDigitHexString(Integer.parseInt(sfi, 2).shl(3) or 4)
        READ_RECORD_PARAMETER = "$sfi${p2}00"

        var readRecordCommand = "$READ_RECORD$READ_RECORD_PARAMETER"

        responseData = sendCommandToCard("READ RECORD", readRecordCommand, isoDep) {
            runOnUiThread {
                tv_card_messages.append("Error Response: $it\nUsing correct length now\n")
            }
            //The length is given in the last byte of the response
            val length = it.substring(it.length - 2 until it.length)

            READ_RECORD_PARAMETER = "$sfi$p2$length"
            readRecordCommand = "$READ_RECORD$READ_RECORD_PARAMETER"
            val response = sendCommandToCard("READ RECORD", readRecordCommand, isoDep){
                runOnUiThread {
                    tv_card_messages.append("Error reading Data Record! Response: $it")
                }
                return@sendCommandToCard null
            }
            return@sendCommandToCard response
        }
        responseData ?: return

        //-----------------Sending SELECT APPLICATION (AID) Command--------------------------------

        /*
        The AID of the Application should now be present in the response data
        For now selecting just the first application
        Later implementation could include iterating through all Applications based on priority
        and doing the following tasks for all of them
         */

        val aid = responseData.tlvs[Tags.ADF_NAME]
        if (aid == null) {
            runOnUiThread{tv_card_messages.append("No Application present")}
            return
        }
        val length = Utils.convertNumberToTwoDigitHexString(aid.length/2) //AID.length has to be an even number because it's composed of bytes
        val selectAidCommand = "$SELECT_AID_COMMAND$length$aid"
        responseData = sendCommandToCard("SELECT AID", selectAidCommand, isoDep) {
            runOnUiThread{
                tv_card_messages.append("Selection failed! Response: $it")
            }
            return@sendCommandToCard null
        }
        responseData ?: return

        val pdolData = EmvUtils.generatePdolString(EmvUtils.parsePdol(responseData.tlvs[Tags.PDOL]))
        val gpoCommand = "$GET_PROCESSING_OPTIONS_COMMAND$pdolData"
        responseData = sendCommandToCard("GET PROCESSING OPTIONS", gpoCommand, isoDep) {
            runOnUiThread {
                tv_card_messages.append("Error response: $it")
            }
            return@sendCommandToCard null
        }
        responseData ?: return

        val aip = responseData.tlvs[Tags.APPLICATION_INTERCHANGE_PROFILE]
        val afl = EmvUtils.parseAfl(responseData.tlvs[Tags.APPLICATION_FILE_LOCATOR]!!)

        runOnUiThread {
            tv_card_messages.append("Parsed Afl: \n")
        }
        var counter = 0
        afl.forEach{
            runOnUiThread {
                tv_card_messages.append("Afl Record ${++counter}:\nSFI: ${it[Tags.SFI]}\nFirst Record: ${it[EmvUtils.FIRST_REC]}\n" +
                        "Last Record: ${it[EmvUtils.LAST_REC]}\nNr of Records for Offline Authentication: ${it[EmvUtils.RECORDS_INVOLVED_IN_OFFLINE_AUTHENTICATION]}\n")
            }
        }

        afl.forEach{
            val sfi = Utils.convertNumberToTwoDigitHexString(Integer.parseInt(it[Tags.SFI], 16).shl(3).or(4))
            val first_rec = Integer.parseInt(it[EmvUtils.FIRST_REC], 16)
            val last_rec = Integer.parseInt(it[EmvUtils.LAST_REC], 16)
            val offl_auth = Integer.parseInt(it[EmvUtils.RECORDS_INVOLVED_IN_OFFLINE_AUTHENTICATION], 16)

            for (i in first_rec..last_rec){
                val readRecCommand = "$READ_RECORD${Utils.convertNumberToTwoDigitHexString(i)}${sfi}00"
                responseData = sendCommandToCard("READ RECORD", readRecCommand, isoDep) {
                    runOnUiThread {
                        tv_card_messages.append("Error Response: $it")
                    }
                    return@sendCommandToCard null
                }
            }
        }
    }

    fun sendCommandToCard(commandName: String, command: String, isoDep: IsoDep, errorFunction: (response: String) -> CardData?): CardData? {

        runOnUiThread {
            tv_card_messages.append("Sending $commandName Command to Card \n")
        }

        Log.i(TAG, "$commandName Command: $command")
        val response = Utils.toHexString(isoDep.transceive(Utils.hexStringToByteArray(command)))
        var statusWord = response.substring(response.length-4 until response.length)
        val responseData = CardData()
        if(statusWord == Tags.STATUSWORD_OK) {
            EmvUtils.processResponse(response, responseData)
            //responseData.convertHexToAscii()
            runOnUiThread {
                tv_card_messages.append("Status: OK\nResponse: $response\n$responseData\n")
            }
        }
        else {
            return errorFunction(response)
        }
        return responseData
    }

}