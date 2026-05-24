package net.guneyilmaz0.skyblocks.island

import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
import net.guneyilmaz0.skyblocks.objects.IslandData
import net.guneyilmaz0.skyblocks.utils.Translator
import net.guneyilmaz0.skyblocks.utils.Utils
import org.allaymc.api.player.Player
import org.allaymc.api.registry.Registries
import org.allaymc.api.scheduler.Task
import org.allaymc.api.server.Server
import org.joml.Vector3i

object IslandManager {
    private val memberRequests = HashMap<String, String>()

    fun createIsland(player: Player, type: String) {
        val id = Utils.createIslandId()
        SkyBlockS.provider.saveIsland(IslandData(id, player.originName, type))

        Server.getInstance().scheduler.scheduleDelayed(SkyBlockS.instance, Task {
            val storage = Registries.WORLD_STORAGE_FACTORIES
                .get("LEVELDB")
                .apply(Server.getInstance().worldPool.worldFolder.resolve(id))

            val generatorClass = when (type) {
                "desert" -> "island_desert"
                else -> "island_default"
            }

            val generator = Registries.WORLD_GENERATOR_FACTORIES
                .get(generatorClass)
                .apply("")

            Server.getInstance().worldPool.loadWorld(id, storage, generator, null, null)
            completeCreateIsland(player, id)
            true
        }, 0, true)
    }

    private fun completeCreateIsland(player: Player, id: String) {
        val world = Server.getInstance().worldPool.getWorld(id) ?: return
        world.worldData.spawnPoint = Vector3i(7, 66, 7)

        val session = Session.get(player)
        session.islandId = id
        session.profile.islandId = id

        player.controlledEntity.teleport(world.spawnPoint)
        player.sendMessage(Translator.translate(player, "island_created"))
    }

    fun inviteMember(player: Player, target: Player) {
        memberRequests[target.originName] = player.originName
        player.sendMessage(Translator.translate(player, "invite_sent", target.originName))
        target.sendMessage(Translator.translate(target, "invite_received", player.originName))
//        target.getLevel().addSound(target, Sound.RANDOM_ORB, 1f, 1f, target)
    }

    fun acceptInvite(player: Player) {
        val inviter = memberRequests.remove(player.originName)
        if (inviter == null) {
            player.sendMessage(Translator.translate(player, "no_invites"))
            return
        }

        val session = Session.get(player)
        if (session.getIsland() != null) {
            player.sendMessage(Translator.translate(player, "already_have_island"))
            return
        }

        val inviterPlayer = Server.getInstance().playerManager.getPlayerByName(inviter)
        val island = if (inviterPlayer != null) {
            Session.get(inviterPlayer).getIsland()
        } else {
            val inviterProfile = SkyBlockS.provider.getProfile(inviter)
            inviterProfile?.islandId?.let { Island.get(it) }
        }

        if (island == null) {
            player.sendMessage(Translator.translate(player, "island_not_found"))
            return
        }

        island.database.members += player.originName
        island.save()
        
        session.islandId = island.id
        session.profile.islandId = island.id
        session.save()
        
        player.sendMessage(Translator.translate(player, "joined_island", inviter))
    }

    fun deleteIsland(player: Player) {
        val session = Session.get(player)
        val island = session.getIsland()
        if (island == null) {
            player.sendMessage(Translator.translate(player, "no_island"))
            return
        }

        if (!island.isOwner(player.originName)) {
            player.sendMessage(Translator.translate(player, "must_be_owner"))
            return
        }

        for (member in island.getOnlineMembers()) {
            val memberSession = Session.get(member.controller)
            memberSession.islandId = null
            memberSession.profile.islandId = null
            member.sendMessage(Translator.translate(member.controller, "island_deleted"))
            island.database.members -= member.controller.originName
            member.teleport(Server.getInstance().worldPool.defaultWorld.spawnPoint)
        }

        for (member in island.database.members) {
            val profile = SkyBlockS.provider.getProfile(member)
            profile?.islandId = null
        }

        val id = island.id
        island.delete()
        Server.getInstance().worldPool.unloadWorld(id)
        Utils.deleteLevel(id)
    }

}