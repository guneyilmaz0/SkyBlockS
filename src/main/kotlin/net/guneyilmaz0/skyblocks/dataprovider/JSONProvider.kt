package net.guneyilmaz0.skyblocks.dataprovider

import com.google.gson.Gson
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile
import org.allaymc.api.utils.config.Config

class JSONProvider : DataProvider {

    private lateinit var profilesConfig: Config
    private lateinit var islandsConfig: Config
    private val gson = Gson()

    override fun connect() {
        val dataFolder = SkyBlockS.instance.pluginContainer.dataFolder

        profilesConfig = Config("${dataFolder}/profiles.json", Config.JSON)
        islandsConfig = Config("${dataFolder}/islands.json", Config.JSON)
    }

    override fun disconnect() {
        profilesConfig.save()
        islandsConfig.save()
    }

    override fun getProfile(name: String): Profile? {
        val key = name.lowercase()
        if (!profilesConfig.exists(key)) return null
        return gson.fromJson(gson.toJsonTree(profilesConfig.get(key)), Profile::class.java)
    }

    override fun isProfileExists(name: String): Boolean = profilesConfig.exists(name.lowercase())

    override fun saveProfile(profile: Profile) {
        profilesConfig.set(profile.nickName.lowercase(), gson.toJsonTree(profile))
        profilesConfig.save()
    }

    override fun getIsland(id: String): IslandData? {
        if (!islandsConfig.exists(id)) return null
        return gson.fromJson(gson.toJsonTree(islandsConfig.get(id)), IslandData::class.java)
    }

    override fun isIslandExists(id: String): Boolean = islandsConfig.exists(id)

    override fun saveIsland(island: IslandData) {
        islandsConfig.set(island.id, gson.toJsonTree(island))
        islandsConfig.save()
    }

    override fun removeIsland(island: IslandData) {
        islandsConfig.remove(island.id)
        islandsConfig.save()
    }
}