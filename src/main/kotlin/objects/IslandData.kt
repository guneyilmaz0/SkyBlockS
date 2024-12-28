package net.guneyilmaz0.skyblocks.objects

import net.guneyilmaz0.mongos.MongoSObject

data class IslandData(
    val id: String,
    var owner: String,
    var type: String,
    var members: List<String> = mutableListOf(),
    var lock: Boolean = false
) : MongoSObject() {
    override fun toString(): String = super.toString()
}