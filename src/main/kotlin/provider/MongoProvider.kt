package net.guneyilmaz0.skyblocks.provider

import net.guneyilmaz0.mongos4k.MongoS
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile

class MongoProvider(plugin: SkyBlockS) : Provider(plugin) {

    private lateinit var database: MongoS

    override fun initialize() {
        database = MongoS(plugin.config.getString("mongo.uri"), plugin.config.getString("mongo.database"))
    }

    override fun getProfile(name: String): Profile? = database.get<Profile>("profiles", name)

    override fun isProfileExists(name: String): Boolean = database.exists("profiles", name)

    override fun saveProfile(profile: Profile) = database.set("profiles", profile.nickName, profile)

    override fun getIsland(id: String): IslandData? = database.get<IslandData>("islands", id)

    override fun isIslandExists(id: String): Boolean = database.exists("islands", id)

    override fun saveIsland(island: IslandData) = database.set("islands", island.id, island)

    override fun removeIsland(island: IslandData) {
        database.remove("islands", island.id)
    }
}