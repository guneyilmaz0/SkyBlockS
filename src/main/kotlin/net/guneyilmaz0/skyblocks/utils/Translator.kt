package net.guneyilmaz0.skyblocks.utils

import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import org.allaymc.api.player.Player
import org.allaymc.api.utils.config.Config
import java.io.File

object Translator {
    private val configCache = mutableMapOf<String, Config>()

    private fun getConfigForLang(lang: String): Config {
        return configCache.getOrPut(lang) {
            Config(
                "${SkyBlockS.instance.pluginContainer.dataFolder}/lang/$lang.yml",
                2
            )
        }
    }

    fun translate(player: Player, key: String, vararg replacements: String): String {
        val lang = Session.get(player).profile.selectedLang
        val config = getConfigForLang(lang)
        val message = config.getString(key)

        return replacements.foldIndexed(message) { index, acc, value ->
            acc.replace("%var$index%", value)
        }
    }

    fun isLanguageSupported(lang: String): Boolean {
        return File("${SkyBlockS.instance.pluginContainer.dataFolder}/lang/$lang.yml").exists()
    }

    fun getSupportedLanguages(): List<String> {
        return File("${SkyBlockS.instance.pluginContainer.dataFolder}/lang")
            .listFiles()?.map { it.nameWithoutExtension } ?: emptyList()
    }
}