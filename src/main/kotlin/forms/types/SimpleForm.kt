package net.guneyilmaz0.skyblocks.forms.types

import cn.nukkit.Player
import cn.nukkit.form.element.ElementButton
import cn.nukkit.form.element.ElementButtonImageData
import cn.nukkit.form.window.FormWindowSimple
import net.guneyilmaz0.skyblocks.forms.Form
import net.guneyilmaz0.skyblocks.forms.responses.SimpleFormResponse

@Suppress("unused", "MemberVisibilityCanBePrivate")
class SimpleForm(form: FormWindowSimple) : Form(form) {
    constructor() : this(FormWindowSimple("", ""))

    constructor(title: String) : this(FormWindowSimple(title, ""))

    constructor(title: String, content: String) : this(FormWindowSimple(title, content))

    fun send(player: Player, response: SimpleFormResponse) {
        playersForm[player.name] = response
        player.showFormWindow(this.form)
    }

    fun setTitle(value: String): SimpleForm {
        (form as FormWindowSimple).title = value
        return this
    }

    fun setContent(value: String): SimpleForm {
        (form as FormWindowSimple).content = value
        return this
    }

    fun addButton(text: String): SimpleForm {
        addButton(ElementButton(text))
        return this
    }

    fun addButton(text: String, ico: String): SimpleForm {
        return addButton(text, ImageType.PATH, ico)
    }

    fun addButton(text: String, type: ImageType, ico: String): SimpleForm {
        val button = ElementButton(text)
        button.addImage(ElementButtonImageData(if ((type === ImageType.PATH)) "path" else "url", ico))
        addButton(button)
        return this
    }

    fun addButton(elementButton: ElementButton): SimpleForm {
        (form as FormWindowSimple).addButton(elementButton)
        return this
    }
}
