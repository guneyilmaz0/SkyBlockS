package net.guneyilmaz0.skyblocks

import cn.nukkit.Player
import cn.nukkit.Server
import net.guneyilmaz0.skyblocks.island.Island
import net.guneyilmaz0.skyblocks.objects.Profile
import java.util.WeakHashMap

data class Session(val player: Player) {

    companion object {
        private val data = WeakHashMap<Player, Session>()

        fun get(player: Player): Session = data.computeIfAbsent(player) { Session(it) }
    }

    var profile: Profile = loadProfile()
    var islandId: String? = profile.islandId?.also { loadIslandIfNotLoaded(it) }

    private fun loadProfile(): Profile {
        return if (Profile.isProfileExists(player.name)) {
            Profile.getProfile(player.name)!!
        } else {
            Profile(player.uniqueId, player.name, null).also { it.save() }
        }
    }

    private fun loadIslandIfNotLoaded(islandId: String) {
        if (!Server.getInstance().isLevelLoaded(islandId)) {
            Server.getInstance().loadLevel(islandId)
        }
    }

    fun getIsland(): Island? = islandId?.let { Island.get(it) }

    fun save() {
        profile.save()
        getIsland()?.save()
    }

    fun close() {
        save()
        data.remove(player)
    }
}
