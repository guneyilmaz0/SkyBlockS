package net.guneyilmaz0.skyblocks.forms

import cn.nukkit.Player
import cn.nukkit.form.window.FormWindow
import net.guneyilmaz0.skyblocks.forms.responses.CustomFormResponse
import net.guneyilmaz0.skyblocks.forms.responses.FormAPIResponse
import net.guneyilmaz0.skyblocks.forms.responses.ModalFormResponse
import net.guneyilmaz0.skyblocks.forms.responses.SimpleFormResponse

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class Form(protected var form: FormWindow) {

    fun send(player: Player) = player.showFormWindow(form)

    companion object {
        val playersForm: MutableMap<String, FormAPIResponse> = mutableMapOf()

        fun sendForm(player: Player, form: FormWindow, response: FormAPIResponse) {
            playersForm[player.name] = response
            player.showFormWindow(form)
        }

        fun sendForm(player: Player, form: FormWindow, response: ModalFormResponse) =
            sendForm(player, form, response as FormAPIResponse)

        fun sendForm(player: Player, form: FormWindow, response: CustomFormResponse) =
            sendForm(player, form, response as FormAPIResponse)

        fun sendForm(player: Player, form: FormWindow, response: SimpleFormResponse) =
            sendForm(player, form, response as FormAPIResponse)

    }
}
