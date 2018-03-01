package com.bachelorarbeit.peyerl.michael.android.hceimplementation.apdus

/**
 * Created by Michael on 20.02.2018.
 */
data class CommandApdu(val header: Header, val data: Data?){

    companion object {

        val CLA_INTER_INDUSTRY = "00"
        val CLA_EMV_PROPRIETARY = "80"
        val INS_SELECT = "A4"
        val INS_READ_RECORD = "B2"
    }

    constructor(cla: String, ins: String, p1: String, p2: String): this(Header(cla, ins, p1, p2), Data("00", "", "00"))

    constructor(cla: String, ins: String, p1: String, p2: String, l: String, data: String, le: String): this(Header(cla,ins, p1, p2), Data(l, data, le))

    data class Header(val cla: String, val ins: String, val p1: String, val p2: String){

        override fun equals(other: Any?): Boolean {
            if(other == null || other !is Header){
                return false
            }
            return this.cla == other.cla && this.ins == other.ins && this.p1 == other.p1 && this.p2 == other.p2
        }

        override fun hashCode(): Int {
            var result = cla.hashCode()
            result = 31 * result + ins.hashCode()
            result = 31 * result + p1.hashCode()
            result = 31 * result + p2.hashCode()
            return result
        }

        override fun toString(): String {
            return "$cla-$ins-$p1-$p2"
        }
    }

    data class Data(val l: String, val data: String, val le: String){

        override fun equals(other: Any?): Boolean {
            if(other == null || other !is Data){
                return false
            }
            return this.l == other.l && this.data == other.data && this.le == other.le
        }

        override fun hashCode(): Int {
            var result = l.hashCode()
            result = 31 * result + data.hashCode()
            result = 31 * result + le.hashCode()
            return result
        }

        override fun toString(): String {
            return "$l-$data-$le"
        }

    }

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is CommandApdu){
            return false
        }
        return this.header == other.header && this.data == other.data
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "$header|$data"
    }
}