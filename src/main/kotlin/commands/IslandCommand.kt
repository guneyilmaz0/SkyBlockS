package net.guneyilmaz0.skyblocks.commands

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import cn.nukkit.form.window.FormWindowModal
import net.guneyilmaz0.skyblocks.Session
import net.guneyilmaz0.skyblocks.SkyBlockS
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
        permission = SkyBlockS.instance.config.getString("island_permission","skyblocks.command.island")
    }

    override fun execute(sender: CommandSender, string: String, args: Array<String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }

        if (!testPermission(sender)) return false

        if (args.isNullOrEmpty()) {
            sender.sendMessage(usage)
            return false
        }

        when (args[0].lowercase()) {
            "create" -> createIsland(sender, args)
            "tp", "teleport" -> {
                val island = getIsland(sender) ?: return false
                island.teleportPlayer(sender)
            }

            "delete" -> deleteIsland(sender)
            "invite" -> invitePlayer(sender, args)
            "remove" -> removeMember(sender, args)
            "accept" -> IslandManager.acceptInvite(sender)
            "kick" -> kickPlayerOnIsland(sender, args)
            "leave" -> leaveIsland(sender)
            "spawn", "setspawn" -> {
                val island = getIsland(sender) ?: return false
                island.setSpawn(sender)
            }

            "lock" -> {
                val island = getIsland(sender) ?: return false
                island.database.lock = true
                sender.sendMessage(Translator.translate(sender, "island_locked"))
            }

            "unlock" -> {
                val island = getIsland(sender) ?: return false
                island.database.lock = false
                sender.sendMessage(Translator.translate(sender, "island_unlocked"))
            }

            "visit" -> visitIsland(sender, args)
            "language" -> setLanguage(sender, args)
            "help" -> sender.sendMessage(Translator.translate(sender, "island_help"))

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
            player.sendMessage(Translator.translate(player, "island_type_required"))
            return
        }

        val type = args[1].lowercase()
        if (type !in arrayOf("normal", "desert")) {
            player.sendMessage(Translator.translate(player, "invalid_island_type"))
            return
        }

        IslandManager.createIsland(player, type)
    }

    private fun deleteIsland(player: Player) {
        val island = getIsland(player) ?: return

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
        val island = getIsland(player) ?: return

        if (!island.isOwner(player.name)) {
            player.sendMessage(Translator.translate(player, "must_be_owner"))
            return
        }

        if (args.size < 2) {
            player.sendMessage(Translator.translate(player, "island_invite_usage"))
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

    private fun removeMember(player: Player, args: Array<String>) {
        val island = getIsland(player) ?: return

        if (!island.isOwner(player.name)) {
            player.sendMessage(Translator.translate(player, "must_be_owner"))
            return
        }

        if (args.size < 2) {
            player.sendMessage(Translator.translate(player, "island_remove_usage"))
            return
        }

        if (!island.database.members.contains(args[1])) {
            player.sendMessage(Translator.translate(player, "player_not_member", args[1]))
            return
        }

        if (island.isOwner(args[1])) {
            player.sendMessage(Translator.translate(player, "cannot_remove_owner"))
            return
        }

        val target = player.server.getPlayer(args[1])
        if (target == null) {
            val profile = SkyBlockS.provider.getProfile(args[1])
            profile!!.islandId = null
            SkyBlockS.provider.saveProfile(profile)
        } else {
            val targetSession = Session.get(target)
            targetSession.islandId = null
            targetSession.profile.islandId = null
            target.sendMessage(Translator.translate(target, "removed_from_island"))
        }

        island.database.members -= target.name
        player.sendMessage(Translator.translate(player, "player_removed", target.name))
    }

    private fun kickPlayerOnIsland(player: Player, args: Array<String>) {
        val island = getIsland(player) ?: return

        if (args.size < 2) {
            player.sendMessage(Translator.translate(player, "island_kick_usage"))
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
        val island = getIsland(player) ?: return

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

    private fun getIsland(player: Player): Island? {
        val session = Session.get(player)
        val island = session.getIsland() ?: run {
            player.sendMessage(Translator.translate(player, "island_not_found"))
            return null
        }
        return island
    }

    private fun visitIsland(player: Player, args: Array<String>) {
        if (args.size < 2) {
            player.sendMessage(Translator.translate(player, "island_visit_usage"))
            return
        }

        val target = player.server.getPlayer(args[1]) ?: run {
            player.sendMessage(Translator.translate(player, "player_not_found"))
            return
        }

        val island = Session.get(target).getIsland() ?: run {
            player.sendMessage(Translator.translate(player, "player_has_no_island", target.name))
            return
        }

        if (!island.isMember(player.name) && island.database.lock) {
            player.sendMessage(Translator.translate(player, "island_locked_target"))
            return
        }

        island.teleportPlayer(player)
        player.sendMessage(Translator.translate(player, "teleported_to_island", target.name))
    }

    private fun setLanguage(player: Player, args: Array<String>) {
        if (args.size < 2) {
            player.sendMessage(Translator.translate(player, "island_lang_usage"))
            return
        }

        val lang = args[1].lowercase()
        if (!Translator.isLanguageSupported(lang)) {
            player.sendMessage(Translator.translate(player, "language_not_supported",
                Translator.getSupportedLanguages().toString()
            ))
            return
        }

        val profile = Session.get(player).profile
        profile.selectedLang = lang
        player.sendMessage(Translator.translate(player, "language_changed", lang))
    }
}