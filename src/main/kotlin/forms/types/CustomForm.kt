package net.guneyilmaz0.skyblocks.forms.types

import cn.nukkit.Player
import cn.nukkit.form.element.*
import cn.nukkit.form.window.FormWindowCustom
import net.guneyilmaz0.skyblocks.forms.Form
import net.guneyilmaz0.skyblocks.forms.responses.CustomFormResponse

@Suppress("unused")
class CustomForm(form: FormWindowCustom) : Form(form) {

    constructor() : this(FormWindowCustom(""))

    constructor(title: String) : this(FormWindowCustom(title))

    fun send(player: Player, response: CustomFormResponse) {
        playersForm[player.name] = response
        player.showFormWindow(form)
    }

    fun setTitle(title: String): CustomForm {
        (form as FormWindowCustom).title = title
        return this
    }

    fun addLabel(value: String): CustomForm {
        (form as FormWindowCustom).addElement(ElementLabel(value))
        return this
    }

    fun addInput(): CustomForm {
        val element = ElementInput("")
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addInput(name: String): CustomForm {
        val element = ElementInput(name)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addInput(name: String, placeholder: String): CustomForm {
        val element = ElementInput(name, placeholder)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addInput(name: String, placeholder: String, defaultText: String): CustomForm {
        val element = ElementInput(name, placeholder, defaultText)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addToggle(): CustomForm {
        val element = ElementToggle("")
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addToggle(name: String): CustomForm {
        val element = ElementToggle(name)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addToggle(name: String, defaultValue: Boolean): CustomForm {
        val element = ElementToggle(name, defaultValue)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addDropDown(name: String, list: List<String>): CustomForm {
        val element = ElementDropdown(name, list)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addDropDown(name: String, list: List<String>, defaultValue: Int): CustomForm {
        val element = ElementDropdown(name, list, defaultValue)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addSlider(name: String, min: Int, max: Int): CustomForm {
        val element = ElementSlider(name, min.toFloat(), max.toFloat())
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addSlider(name: String, min: Int, max: Int, step: Int): CustomForm {
        val element = ElementSlider(name, min.toFloat(), max.toFloat(), step, 3.0f)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addSlider(name: String, min: Int, max: Int, step: Int, defaultValue: Int): CustomForm {
        val element = ElementSlider(name, min.toFloat(), max.toFloat(), step, defaultValue.toFloat())
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addStepSlider(name: String, list: List<String>): CustomForm {
        val element = ElementStepSlider(name, list)
        (form as FormWindowCustom).addElement(element)
        return this
    }

    fun addStepSlider(name: String, list: List<String>, defaultStep: Int): CustomForm {
        val element = ElementStepSlider(name, list, defaultStep)
        (form as FormWindowCustom).addElement(element)
        return this
    }
}
