package net.guneyilmaz0.skyblocks.forms.types

import cn.nukkit.Player
import cn.nukkit.form.window.FormWindowModal
import net.guneyilmaz0.skyblocks.forms.Form
import net.guneyilmaz0.skyblocks.forms.responses.ModalFormResponse

@Suppress("unused", "MemberVisibilityCanBePrivate")
class ModalForm(form: FormWindowModal) : Form(form) {

    constructor() : this(FormWindowModal("", "", "", ""))

    constructor(title: String) : this(FormWindowModal(title, "", "", ""))

    constructor(title: String, content: String) : this(FormWindowModal(title, content, "", ""))

    constructor(title: String, content: String, trueButton: String) : this(
        FormWindowModal(
            title,
            content,
            trueButton,
            ""
        )
    )

    constructor(title: String, content: String, trueButton: String, falseButton: String) : this(
        FormWindowModal(
            title,
            content,
            trueButton,
            falseButton
        )
    )

    fun send(player: Player, response: ModalFormResponse) {
        playersForm[player.name] = response
        player.showFormWindow(this.form)
    }

    fun setTitle(value: String): ModalForm {
        (form as FormWindowModal).title = value
        return this
    }

    fun setContent(value: String): ModalForm {
        (form as FormWindowModal).content = value
        return this
    }

    fun setPositiveButton(value: String): ModalForm {
        return setButton1(value)
    }

    fun setButton1(value: String): ModalForm {
        (form as FormWindowModal).button1 = value
        return this
    }

    fun setNegativeButton(value: String): ModalForm {
        return setButton2(value)
    }

    fun setButton2(value: String): ModalForm {
        (form as FormWindowModal).button2 = value
        return this
    }
}
