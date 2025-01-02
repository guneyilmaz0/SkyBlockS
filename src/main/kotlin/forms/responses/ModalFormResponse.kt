package net.guneyilmaz0.skyblocks.forms.responses

fun interface ModalFormResponse : FormAPIResponse {
    fun handle(i: Int)
}