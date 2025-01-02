package net.guneyilmaz0.skyblocks.listeners

import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.player.PlayerFormRespondedEvent
import cn.nukkit.event.player.PlayerQuitEvent
import cn.nukkit.form.response.FormResponseCustom
import cn.nukkit.form.response.FormResponseModal
import cn.nukkit.form.response.FormResponseSimple
import cn.nukkit.form.window.FormWindowCustom
import cn.nukkit.form.window.FormWindowModal
import cn.nukkit.form.window.FormWindowSimple
import net.guneyilmaz0.skyblocks.forms.Form
import net.guneyilmaz0.skyblocks.forms.responses.*

@Suppress("unused")
class FormListener : Listener {

    @EventHandler
    fun onPlayerFormResponded(event: PlayerFormRespondedEvent) {
        val player = event.player
        val window = event.window
        val response = window.response

        if (!Form.playersForm.containsKey(player.name)) return

        val temp = Form.playersForm[player.name]
        if (response == null || event.wasClosed()) {
            when (temp) {
                is CustomFormResponse -> temp.handle(null)
                is ModalFormResponse -> temp.handle(-1)
                is SimpleFormResponse -> temp.handle(-1)
            }
            return
        }

        when (window) {
            is FormWindowSimple -> (temp as SimpleFormResponse).handle((response as FormResponseSimple).clickedButtonId)
            is FormWindowCustom -> (temp as CustomFormResponse).handle(ArrayList((response as FormResponseCustom).responses.values))
            is FormWindowModal -> (temp as ModalFormResponse).handle((response as FormResponseModal).clickedButtonId)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        Form.playersForm.remove(event.player.name)
    }
}