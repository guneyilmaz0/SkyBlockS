package net.guneyilmaz0.skyblocks.objects

import net.guneyilmaz0.mongos4k.MongoSObject
import java.util.*

data class Profile(
    val uuid: UUID,
    var nickName: String,
    var islandId: String?,
    var selectedLang: String = "en"
) : MongoSObject()