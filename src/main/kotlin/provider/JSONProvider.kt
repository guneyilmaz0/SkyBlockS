package net.guneyilmaz0.skyblocks.provider

import cn.nukkit.utils.Config
import com.google.gson.Gson
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile

class JSONProvider(plugin: SkyBlockS) : Provider(plugin) {

    private lateinit var profiles: Config
    private lateinit var islands: Config

    override fun initialize() {
        profiles = Config("${SkyBlockS.instance.dataFolder.path}/profiles.json", 1)
        islands = Config("${SkyBlockS.instance.dataFolder.path}/islands.json", 1)
    }

    override fun getProfile(name: String): Profile? =
        if (profiles.exists(name.lowercase())) Gson().fromJson(
            profiles.getString(name.lowercase()),
            Profile::class.java
        )
        else null

    override fun isProfileExists(name: String): Boolean = profiles.exists(name.lowercase())

    override fun saveProfile(profile: Profile) {
        profiles.set(profile.nickName.lowercase(), profile)
        profiles.save()
    }

    override fun getIsland(id: String): IslandData? =
        if (islands.exists(id)) Gson().fromJson(
            islands.getString(id),
            IslandData::class.java
        )
        else null

    override fun isIslandExists(id: String): Boolean = islands.exists(id)

    override fun saveIsland(island: IslandData) {
        islands.set(island.id, island)
        islands.save()
    }

    override fun removeIsland(island: IslandData) {
        islands.remove(island.id)
        islands.save()
    }
}