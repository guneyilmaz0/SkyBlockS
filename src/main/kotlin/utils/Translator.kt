package net.guneyilmaz0.skyblocks.utils

import cn.nukkit.Player
import cn.nukkit.utils.Config
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import java.io.File

object Translator {

    private val configCache = mutableMapOf<String, Config>()

    private fun getConfigForLang(lang: String): Config =
        configCache.getOrPut(lang) { Config("${SkyBlockS.instance.dataFolder.path}/lang/$lang.yml", 2) }

    fun translate(player: Player, key: String, vararg replacements: String): String {
        val lang = Session.get(player).profile.selectedLang
        val config = getConfigForLang(lang)
        val message = config.getString(key) ?: return key

        return replacements.foldIndexed(message) { index, acc, value ->
            acc.replace("%var$index%", value)
        }
    }

    fun isLanguageSupported(lang: String): Boolean =
        File("${SkyBlockS.instance.dataFolder.path}/lang/$lang.yml").exists()

    fun getSupportedLanguages(): List<String> =
        File("${SkyBlockS.instance.dataFolder.path}/lang")
            .listFiles()?.map { it.nameWithoutExtension } ?: emptyList()
}
