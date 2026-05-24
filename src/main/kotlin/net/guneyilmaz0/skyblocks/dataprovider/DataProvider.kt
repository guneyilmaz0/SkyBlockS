package net.guneyilmaz0.skyblocks.dataprovider

import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile

interface DataProvider {
    fun connect()
    fun disconnect()

    fun getProfile(name: String): Profile?
    fun isProfileExists(name: String): Boolean
    fun saveProfile(profile: Profile)

    fun getIsland(id: String): IslandData?
    fun isIslandExists(id: String): Boolean
    fun saveIsland(island: IslandData)
    fun removeIsland(island: IslandData)
}