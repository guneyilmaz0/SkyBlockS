package net.guneyilmaz0.skyblocks.objects

import net.guneyilmaz0.mongos4k.MongoSObject

data class IslandData(
    val id: String,
    var owner: String,
    var type: String,
    var members: List<String> = mutableListOf(),
    var lock: Boolean = false,
    //TODO level system
) : MongoSObject()