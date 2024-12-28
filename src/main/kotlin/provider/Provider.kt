package net.guneyilmaz0.skyblocks.provider

import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.objects.Profile

abstract class Provider(protected val plugin: SkyBlockS) {

    init {
        this.initialize()
    }

    abstract fun initialize()

    abstract fun getProfile(name: String) : Profile?

    abstract fun isProfileExists(name: String) : Boolean

    abstract fun saveProfile(profile: Profile)

    abstract fun getIsland(id: String) : IslandData?

    abstract fun isIslandExists(id: String) : Boolean

    abstract fun saveIsland(island: IslandData)

    abstract fun removeIsland(island: IslandData)
}