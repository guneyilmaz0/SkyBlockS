package net.guneyilmaz0.skyblocks.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Sound
import cn.nukkit.math.Vector3
import cn.nukkit.scheduler.AsyncTask
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyblockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.utils.Utils

object IslandManager {

    private var memberRequests: HashMap<String, String> = HashMap()

    fun createIsland(player: Player, type: String) {
        val id = Utils.createIslandId()
        val data = IslandData.createDefault(player, id, type)
        IslandData.saveIsland(data)

        Server.getInstance().scheduler.scheduleAsyncTask(SkyblockS.instance, object : AsyncTask() {
            override fun onRun() {
                Server.getInstance().generateLevel(id, 0, IslandGenerator::class.java)
                completeCreateIsland(player, id)
            }
        })
    }

    private fun completeCreateIsland(player: Player, id: String) {
        val level = Server.getInstance().getLevelByName(id)
        level.setSpawnLocation(Vector3(7.0, 66.0, 7.0))
        val session = Session.get(player)
        session.islandId = id
        session.profile.islandId = id
        player.teleport(Vector3(7.0, 66.0, 7.0))
        player.sendMessage("§aYour island has been created successfully!")
        level.addSound(player, Sound.RANDOM_LEVELUP, 1f, 1f, player)
    }

    fun inviteMember(player: Player, target: Player) {
        memberRequests[target.name] = player.name
        player.sendMessage("§a${target.name} has been invited to the island.")
        target.sendMessage("§aYou have been invited to join §b${player.name}§a's island.")
    }

    fun acceptInvite(player: Player) {
        val inviter = memberRequests.remove(player.name) ?: {
            player.sendMessage("§cYou don't have any pending invites.")
        }

        val session = Session.get(player)
        if (session.getIsland() != null) {
            player.sendMessage("§cYou already have an island.")
            return
        }

        val island = session.getIsland() ?: return
        island.database.members += player.name
        island.save()
        player.sendMessage("§aYou have joined §b$inviter§a's island.")
    }

}