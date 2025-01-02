package net.guneyilmaz0.skyblocks.forms.responses

fun interface SimpleFormResponse : FormAPIResponse {
    fun handle(i: Int)
}
