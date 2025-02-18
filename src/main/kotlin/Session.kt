package net.guneyilmaz0.skyblocks

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.network.protocol.PlaySoundPacket
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
        return if (SkyBlockS.provider.isProfileExists(player.name)) SkyBlockS.provider.getProfile(player.name)!!
        else Profile(player.uniqueId, player.name, null).also { SkyBlockS.provider.saveProfile(it) }
    }

    private fun loadIslandIfNotLoaded(islandId: String) {
        if (!Server.getInstance().isLevelLoaded(islandId)) Server.getInstance().loadLevel(islandId)
    }

    fun getIsland(): Island? = islandId?.let { Island.get(it) }

    fun playSound(sound: String, volume: Float = 1f) {
        val packet = PlaySoundPacket()
        packet.name = sound
        packet.volume = volume
        packet.pitch = 1f
        packet.x = player.floorX
        packet.y = player.floorY
        packet.z = player.floorZ
        player.dataPacket(packet)
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
