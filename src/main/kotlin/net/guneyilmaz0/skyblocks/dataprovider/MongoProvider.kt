package net.guneyilmaz0.skyblocks.dataprovider

import net.guneyilmaz0.mongos4k.MongoS
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile

class MongoProvider : DataProvider {

    private lateinit var database: MongoS

    private companion object {
        const val COLLECTION_PROFILES = "profiles"
        const val COLLECTION_ISLANDS = "islands"
    }

    override fun connect() {
        val uri = SkyBlockS.instance.config.getString("mongo.uri")
        val db = SkyBlockS.instance.config.getString("mongo.database")
        database = MongoS(uri, db)
    }

    override fun disconnect() {
        database.close()
    }

    override fun getProfile(name: String): Profile? =
        runCatching { database.get<Profile>(COLLECTION_PROFILES, name) }.getOrNull()

    override fun isProfileExists(name: String): Boolean =
        runCatching { database.exists(COLLECTION_PROFILES, name) }.getOrDefault(false)

    override fun saveProfile(profile: Profile) {
        database.set(COLLECTION_PROFILES, profile.nickName, profile)
    }

    override fun getIsland(id: String): IslandData? =
        runCatching { database.get<IslandData>(COLLECTION_ISLANDS, id) }.getOrNull()

    override fun isIslandExists(id: String): Boolean =
        runCatching { database.exists(COLLECTION_ISLANDS, id) }.getOrDefault(false)

    override fun saveIsland(island: IslandData) {
        database.set(COLLECTION_ISLANDS, island.id, island)
    }

    override fun removeIsland(island: IslandData) {
        database.remove(COLLECTION_ISLANDS, island.id)
    }
}