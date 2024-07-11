package net.guneyilmaz0.skyblocks.utils

import cn.nukkit.Player
import cn.nukkit.utils.Config
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import java.io.File

object Translator {
    fun translate(player: Player, string: String): String {
        val lang = Session.get(player).profile.selectedLang
        val config = Config("${SkyBlockS.instance.dataFolder.path}/lang/$lang.yml", 2)
        return config.getString(string) ?: string
    }

    fun translate(player: Player, string: String, vararg strings: String): String {
        val lang = Session.get(player).profile.selectedLang
        val config = Config("${SkyBlockS.instance.dataFolder.path}/lang/$lang.yml", 2)
        val msg = config.getString(string) ?: return string
        return strings.foldIndexed(msg) { index, acc, value ->
            acc.replace("%var$index%", value)
        }
    }

    fun isLanguageSupported(lang: String): Boolean =
        File("${SkyBlockS.instance.dataFolder.path}/lang/$lang.yml").exists()

    fun getSupportedLanguages(): List<String> =
        File("${SkyBlockS.instance.dataFolder.path}/lang").listFiles()!!.map { it.nameWithoutExtension }
}