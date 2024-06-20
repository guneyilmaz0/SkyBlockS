package net.guneyilmaz0.skyblocks.objects

import cn.nukkit.utils.Config
import com.google.gson.Gson
import net.guneyilmaz0.skyblocks.SkyblockS
import java.util.*

data class Profile(
    val uuid: UUID,
    var nickName: String,
    var islandId: String?,
    var selectedLang: String = "en"
) {
    companion object {
        fun getProfile(name: String): Profile? {
            val configFilePath = "${SkyblockS.instance.dataFolder.path}/profiles.json"
            val config = Config(configFilePath, 1)

            val lowerCaseName = name.lowercase()
            if (!config.exists(lowerCaseName)) return null

            return Gson().fromJson(config.getString(lowerCaseName), Profile::class.java)
        }

        fun isProfileExists(name: String): Boolean =
            Config("${SkyblockS.instance.dataFolder.path}/profiles.json", 1).exists(name.lowercase())

    }

    fun save() {
        val configFilePath = "${SkyblockS.instance.dataFolder.path}/profiles.json"
        val config = Config(configFilePath, 1)
        config.set(nickName.lowercase(), this)
        config.save()
    }
}