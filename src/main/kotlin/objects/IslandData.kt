package net.guneyilmaz0.skyblocks.objects

import cn.nukkit.Player
import cn.nukkit.utils.Config
import com.google.gson.Gson
import net.guneyilmaz0.skyblocks.SkyBlockS
import kotlin.collections.ArrayList

data class IslandData(
    val id: String,
    var owner: String,
    var type: String,
    var members: List<String>,
    var lock: Boolean = false
) {
    companion object {
        fun createDefault(player: Player, id: String, type: String): IslandData =
            IslandData(id, player.name, type, ArrayList())

        fun getIslandData(name: String): IslandData? {
            val configFilePath = "${SkyBlockS.instance.dataFolder.path}/islands.json"
            val config = Config(configFilePath, 1)

            val lowerCaseName = name.lowercase()
            if (!config.exists(lowerCaseName)) return null

            return Gson().fromJson(config.getString(lowerCaseName), IslandData::class.java)
        }

        fun isIslandExists(id: String): Boolean =
            Config("${SkyBlockS.instance.dataFolder.path}/islands.json", 1).exists(id)

        fun saveIsland(island: IslandData) {
            val configFilePath = "${SkyBlockS.instance.dataFolder.path}/islands.json"
            val config = Config(configFilePath, 1)
            config.set(island.id, island)
            config.save()
        }

        fun removeIsland(id: String) {
            val configFilePath = "${SkyBlockS.instance.dataFolder.path}/islands.json"
            val config = Config(configFilePath, 1)
            config.remove(id)
            config.save()
        }
    }
}