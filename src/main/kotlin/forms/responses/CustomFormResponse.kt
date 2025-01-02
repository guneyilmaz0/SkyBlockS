package net.guneyilmaz0.skyblocks.forms.responses

fun interface CustomFormResponse : FormAPIResponse {
    fun handle(data: List<Any?>?)
}