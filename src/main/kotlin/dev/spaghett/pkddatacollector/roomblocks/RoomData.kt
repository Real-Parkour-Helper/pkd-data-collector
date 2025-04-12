package dev.spaghett.pkddatacollector.roomblocks

data class RoomData(
    val name: String,
    var columns: MutableMap<String, MutableMap<Int, String>>
)
