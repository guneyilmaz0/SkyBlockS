package net.guneyilmaz0.skyblocks.island

import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.utils.Translator
import org.allaymc.api.entity.interfaces.EntityPlayer
import org.allaymc.api.player.Player
import org.allaymc.api.server.Server
import org.joml.Vector3d
import org.joml.Vector3i
import java.util.WeakHashMap

data class Island(val id: String) {
    companion object {
        val data: WeakHashMap<String, Island> = WeakHashMap()

        fun get(id: String): Island = data.computeIfAbsent(id) { Island(it) }
    }

    var database: IslandData = SkyBlockS.provider.getIsland(id)!!

    fun teleportPlayer(player: EntityPlayer) {
        player.teleport(Server.getInstance().worldPool.getWorld(id).spawnPoint)
    }

    fun getOnlineMembers(): List<EntityPlayer> {
        val islandMembers = database.members.toMutableList()
        islandMembers.add(database.owner)
        val onlineMembers = mutableListOf<EntityPlayer>()
        for (member in islandMembers) {
            val player = Server.getInstance().playerManager.getPlayerByName(member)
            if (player != null) onlineMembers.add(player.controlledEntity)
        }
        return onlineMembers
    }

    fun setSpawn(player: Player) {
        val world = Server.getInstance().worldPool.getWorld(id) ?: return
        val pos = player.controlledEntity.location
        world.worldData.spawnPoint = Vector3i(pos.x().toInt(), pos.y().toInt(), pos.z().toInt())
        player.sendMessage(Translator.translate(player, "spawn_set"))
    }

    fun isOwner(name: String): Boolean = database.owner == name

    fun isMember(name: String): Boolean = database.members.contains(name) || isOwner(name)

    fun close() {
        save()
        data.remove(id)
    }

    fun save() = SkyBlockS.provider.saveIsland(database)

    fun delete() {
        data.remove(id)
        SkyBlockS.provider.removeIsland(database)
    }
}