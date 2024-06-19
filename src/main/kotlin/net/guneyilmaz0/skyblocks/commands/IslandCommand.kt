package net.guneyilmaz0.skyblocks.commands

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.form.window.FormWindowModal
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.island.IslandManager
import net.guneyilmaz0.skyblocks.utils.Translator

class IslandCommand : Command(
    "island",
    "Island command",
    "Usage: /island help",
    arrayOf("is")
) {

    init {
        this.commandParameters.clear()
    }

    override fun execute(sender: CommandSender, string: String, args: Array<String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }

        if (args.isNullOrEmpty()) {
            sender.sendMessage(usage)
            return false
        }

        when (args[0].lowercase()) {
            "create" -> createIsland(sender, args)
            "tp", "teleport" -> {
                val island = getIsland(sender)?: return false
                island.teleportPlayer(sender)
            }
            "delete" -> deleteIsland(sender)
            "invite" -> invitePlayer(sender, args)
            "accept" -> IslandManager.acceptInvite(sender)
            "kick" -> kickPlayerOnIsland(sender, args)
            "leave" -> leaveIsland(sender)
            "spawn", "setspawn" -> {
                val island = getIsland(sender)?: return false
                island.setSpawn(sender)
            }
            "help" -> {
                sender.sendMessage("/island create <type> - §eCreate an island")
                sender.sendMessage("/island delete - §eDelete your island")
                sender.sendMessage("/island invite <player> - §eInvite a player to your island")
                sender.sendMessage("/island accept - §eAccept an island invite")
                sender.sendMessage("/island kick <player> - §eKick a player from your island")
                sender.sendMessage("/island leave - §eLeave your island")
                sender.sendMessage("/island spawn - §eSet your island spawn point")
            }
            else -> sender.sendMessage(usage)
        }
        return true
    }

    private fun createIsland(player: Player, args: Array<String>) {
        val session = Session.get(player)
        if (session.getIsland() != null) {
            player.sendMessage(Translator.translate(player, "already_have_island"))
            return
        }

        if (args.size < 2) {
            player.sendMessage("§cUsage: /island create <type>")
            player.sendMessage("§gTypes: normal, snow, desert, end")
            return
        }

        val type = args[1].lowercase()
        if (type !in arrayOf("normal", "snow", "desert", "end")) {
            player.sendMessage(Translator.translate(player, "invalid_island_type"))
            return
        }

        IslandManager.createIsland(player, type)
    }

    private fun deleteIsland(player: Player) {
        val island = getIsland(player)?: return

        if (!island.isOwner(player.name)) {
            player.sendMessage(Translator.translate(player, "must_be_owner"))
            return
        }

        val form = FormWindowModal(
            Translator.translate(player, "form_delete_island_title"),
            Translator.translate(player, "form_delete_island_content"),
            Translator.translate(player, "form_delete_island_true"),
            Translator.translate(player, "form_delete_island_false")
        )
        player.showFormWindow(form, "delete_island".hashCode())
    }

    private fun invitePlayer(player: Player, args: Array<String>) {
        val island = getIsland(player)?: return

        if (!island.isOwner(player.name)) {
            player.sendMessage(Translator.translate(player, "must_be_owner"))
            return
        }

        if (args.size < 2) {
            player.sendMessage("§cUsage: /island invite <player>")
            return
        }

        val target = player.server.getPlayer(args[1]) ?: run {
            player.sendMessage(Translator.translate(player, "player_not_found"))
            return
        }

        if (island.isMember(target.name)) {
            player.sendMessage(Translator.translate(player, "player_already_member", target.name))
            return
        }

        IslandManager.inviteMember(player, target)
    }

    private fun kickPlayerOnIsland(player: Player, args: Array<String>) {
        val island = getIsland(player)?: return

        if (args.size < 2) {
            player.sendMessage("§cUsage: /island kick <player>")
            return
        }

        val target = player.server.getPlayer(args[1]) ?: run {
            player.sendMessage(Translator.translate(player, "player_not_found"))
            return
        }

        if (island.isMember(target.name)) {
            player.sendMessage(Translator.translate(player, "cannot_kick_member"))
            return
        }

        if (!target.getLevel().folderName.equals(island.id)) {
            player.sendMessage(Translator.translate(player, "player_not_on_island"))
            return
        }

        target.teleport(player.server.defaultLevel.spawnLocation)
        target.sendMessage(Translator.translate(target, "kicked_from_island"))
        player.sendMessage(Translator.translate(player, "player_kicked", target.name))
    }

    private fun leaveIsland(player: Player) {
        val session = Session.get(player)
        val island = getIsland(player)?: return

        if (island.isOwner(player.name)) {
            player.sendMessage(Translator.translate(player, "island_owner_leave"))
            return
        }

        island.database.members -= player.name
        session.islandId = null
        session.profile.islandId = null
        player.sendMessage(Translator.translate(player, "island_left"))
        player.teleport(player.server.defaultLevel.spawnLocation)
    }

    private fun getIsland(player: Player) : Island? {
        val session = Session.get(player)
        val island = session.getIsland() ?: run {
            player.sendMessage(Translator.translate(player, "island_not_found"))
            return null
        }
        return island
    }
}