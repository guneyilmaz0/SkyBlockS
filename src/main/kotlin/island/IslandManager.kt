package net.guneyilmaz0.skyblocks.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.level.Level
import cn.nukkit.level.Sound
import cn.nukkit.math.Vector3
import cn.nukkit.scheduler.AsyncTask
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.island.generators.*
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.utils.Translator
import net.guneyilmaz0.skyblocks.utils.Utils

object IslandManager {

    private var memberRequests: HashMap<String, String> = HashMap()

    fun createIsland(player: Player, type: String) {
        val id = Utils.createIslandId()
        SkyBlockS.provider.saveIsland(IslandData(id, player.name, type))

        Server.getInstance().scheduler.scheduleAsyncTask(SkyBlockS.instance, object : AsyncTask() {
            override fun onRun() {
                Server.getInstance().generateLevel(
                    id, 0, when (type) {
                        "desert" -> DesertIslandGenerator::class.java
                        else -> DefaultIslandGenerator::class.java
                    }
                )
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
        player.sendMessage(Translator.translate(player, "island_created"))
        level.addSound(player, Sound.RANDOM_LEVELUP, 1f, 1f, player)
    }

    fun inviteMember(player: Player, target: Player) {
        memberRequests[target.name] = player.name
        player.sendMessage(Translator.translate(player, "invite_sent", target.name))
        target.sendMessage(Translator.translate(target, "invite_received", player.name))
        target.getLevel().addSound(target, Sound.RANDOM_ORB, 1f, 1f, target)
    }

    fun acceptInvite(player: Player) {
        val inviter = memberRequests.remove(player.name) ?: {
            player.sendMessage(Translator.translate(player, "no_invites"))
        }

        val session = Session.get(player)
        if (session.getIsland() != null) {
            player.sendMessage(Translator.translate(player, "already_have_island"))
            return
        }

        val island = session.getIsland() ?: return
        island.database.members += player.name
        island.save()
        player.sendMessage(Translator.translate(player, "joined_island", inviter.toString()))
    }

    fun deleteIsland(player: Player) {
        val session = Session.get(player)
        val island = session.getIsland()
        if (island == null) {
            player.sendMessage(Translator.translate(player, "no_island"))
            return
        }

        if (!island.isOwner(player.name)) {
            player.sendMessage(Translator.translate(player, "must_be_owner"))
            return
        }

        for (member in island.getOnlineMembers()) {
            val memberSession = Session.get(member)
            memberSession.islandId = null
            memberSession.profile.islandId = null
            member.sendMessage(Translator.translate(member, "island_deleted"))
            island.database.members -= member.name
            member.teleport(Server.getInstance().defaultLevel.spawnLocation)
        }

        for (member in island.database.members) {
            val profile = SkyBlockS.provider.getProfile(member)
            profile?.islandId = null
        }

        val id = island.id
        island.delete()
        Server.getInstance().unloadLevel(Server.getInstance().getLevelByName(id))
        Utils.deleteLevel(id)
    }
}