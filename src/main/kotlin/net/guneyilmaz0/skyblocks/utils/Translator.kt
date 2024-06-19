package net.guneyilmaz0.skyblocks.utils

import cn.nukkit.Player
import cn.nukkit.utils.Config
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyblockS

object Translator {
    fun translate(player: Player, string: String): String {
        val lang = Session.get(player).profile.selectedLang
        val config = Config("${SkyblockS.instance.dataFolder.path}/lang/$lang.yml", 2)
        return config.getString(string) ?: string
    }

    fun translate(player: Player, string: String, vararg strings: String): String {
        val lang = Session.get(player).profile.selectedLang
        val config = Config("${SkyblockS.instance.dataFolder.path}/lang/$lang.yml", 2)
        val msg = config.getString(string) ?: return string
        return strings.foldIndexed(msg) { index, acc, value ->
            acc.replace("%var$index%", value)
        }
    }
}