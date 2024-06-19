package net.guneyilmaz0.skyblocks.island

import cn.nukkit.Player
import cn.nukkit.Server
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.objects.IslandData
import java.util.*

data class Island(val id: String) {

    companion object {
        val data: WeakHashMap<String, Island> = WeakHashMap()

        fun get(id: String): Island = data.computeIfAbsent(id) { Island(it) }
    }

    var database: IslandData = IslandData.getIslandData(id)!!

    fun teleportPlayer(player: Player) = player.teleport(Server.getInstance().getLevelByName(id).spawnLocation)

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
            player.sendMessage("§cYou must be on the island to use this command.")
            return
        }

        Server.getInstance().getLevelByName(id).setSpawnLocation(player.location)
        player.sendMessage("§aIsland spawn location updated.")
    }

    fun kickPlayer(player: Player, target: Player) {
        if (isMember(target.name)) {
            player.sendMessage("§cYou can't kick a member off their own island.")
            return
        }

        if (target.level.folderName != id) {
            player.sendMessage("§cPlayer has already left the island.")
            return
        }

        player.sendMessage("§a${target.name} §ckicked off the island.")
        target.sendMessage("§c${player.name} §ckicked you off their island.")
        target.teleport(Server.getInstance().defaultLevel.spawnLocation)
    }

    fun leaveIsland(player: Player) {
        if (isOwner(player.name)) {
            player.sendMessage("§cYou can't leave your own island.")
            return
        }

        database.members -= player.name
        val session = Session.get(player)
        session.islandId = null
        session.profile.islandId = null
        player.sendMessage("§cYou left the island.")
        player.teleport(Server.getInstance().defaultLevel.spawnLocation)
    }

    fun close() {
        save()
        data.remove(id)
    }

    fun save() = IslandData.saveIsland(database)

    fun delete() {
        data.remove(id)
        IslandData.removeIsland(id)
    }

}