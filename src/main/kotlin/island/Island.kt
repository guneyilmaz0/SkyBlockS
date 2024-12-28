package net.guneyilmaz0.skyblocks.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Sound
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.utils.Translator
import java.util.*

data class Island(val id: String) {

    companion object {
        val data: WeakHashMap<String, Island> = WeakHashMap()

        fun get(id: String): Island = data.computeIfAbsent(id) { Island(it) }
    }

    var database: IslandData = SkyBlockS.provider.getIsland(id)!!

    fun teleportPlayer(player: Player) {
        player.teleport(Server.getInstance().getLevelByName(id).spawnLocation)
        player.level.addSound(player, Sound.MOB_SHULKER_TELEPORT, 1f, 1f, player)
    }

    fun isOwner(name: String): Boolean = database.owner == name

    fun isMember(name: String): Boolean = database.members.contains(name) || isOwner(name)

    fun getOnlineMembers(): List<Player> {
        val islandMembers = database.members.toMutableList()
        islandMembers.add(database.owner)
        val onlineMembers = mutableListOf<Player>()
        for (member in islandMembers) {
            val player = Server.getInstance().getPlayer(member)
            if (player != null && player.name.equals(member, true)) onlineMembers.add(player)
        }
        return onlineMembers
    }

    fun setSpawn(player: Player) {
        if (id != player.level.folderName) {
            player.sendMessage(Translator.translate(player, "not_on_island"))
            return
        }

        Server.getInstance().getLevelByName(id).setSpawnLocation(player.location)
        player.sendMessage(Translator.translate(player, "spawn_set"))
    }

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