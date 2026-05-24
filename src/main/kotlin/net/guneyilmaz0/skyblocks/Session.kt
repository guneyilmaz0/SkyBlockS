package net.guneyilmaz0.skyblocks

import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.objects.Profile
import org.allaymc.api.player.Player
import org.allaymc.api.registry.Registries
import org.allaymc.api.server.Server
import java.util.WeakHashMap

data class Session(val player: Player) {
    companion object {
        private val data = WeakHashMap<Player, Session>()

        fun get(player: Player): Session = data.computeIfAbsent(player) { Session(it) }
    }

    var profile: Profile = loadProfile()
    var islandId: String? = profile.islandId?.also { loadIslandIfNotLoaded(it) }

    private fun loadProfile(): Profile {
        return if (SkyBlockS.provider.isProfileExists(player.originName)) SkyBlockS.provider.getProfile(player.originName)!!
        else Profile(player.controlledEntity.uniqueId, player.originName, null).also { SkyBlockS.provider.saveProfile(it) }
    }

    private fun loadIslandIfNotLoaded(islandId: String) {
        val worldPool = Server.getInstance().worldPool
        if (worldPool.getWorld(islandId) != null) return

        val storageFactory = Registries.WORLD_STORAGE_FACTORIES.get("LEVELDB") ?: return

        worldPool.loadWorld(
            islandId,
            storageFactory.apply(worldPool.worldFolder.resolve(islandId)),
            null,
            null,
            null
        )
    }

    fun getIsland(): Island? = islandId?.let { Island.get(it) }

    fun playSound(sound: String, volume: Float = 1f) {

    }

    fun save() {
        SkyBlockS.provider.saveProfile(profile)
        getIsland()?.save()
    }

    fun close() {
        save()
        data.remove(player)
    }
}