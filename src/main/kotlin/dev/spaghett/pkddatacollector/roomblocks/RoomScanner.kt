package dev.spaghett.pkddatacollector.roomblocks

import com.google.gson.GsonBuilder
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.Vec3
import kotlin.math.floor

class RoomScanner :  Command("scanroom") {

    private val mc = Minecraft.getMinecraft()
    private val roomData = mutableMapOf<String, RoomData>()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    init {
        register()
    }

    @DefaultHandler
    fun handle(name: String) {
        scanRoom(name.replace("_", " "))
    }

    @SubCommand("export")
    fun export() {
        if (roomData.isEmpty()) {
            mc.thePlayer.addChatMessage(ChatComponentText("§cNo rooms have been scanned yet."))
            return
        }

        exportRooms()
    }

    /**
     * This is assumed to be called standing at the block designated as the relative "start" block of the room,
     * this is the center plank block UNDER the wall of the room (where you appear in the IL world after clicking the sign).
     * Rooms are 31x50x47 (X Y Z) so you need to stand at the right place for this to work.
     */
    private fun scanRoom(name: String) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            println(ChatComponentText("§cPlayer or world is null"))
            return
        }

        val startPos = Vec3(floor(mc.thePlayer.posX), floor(mc.thePlayer.posY), floor(mc.thePlayer.posZ) + 1.0)

        val data = RoomData(
            name = name,
            columns = mutableMapOf()
        )

        for (x in -15..15) {
            for (z in 0..50) {
                val columnKey = "${x},${z}"
                val yMap = mutableMapOf<Int, String>()
                for (y in -8..38) {
                    val pos = BlockPos(startPos.addVector(x.toDouble(), y.toDouble(), z.toDouble()))
                    val state = mc.theWorld.getBlockState(pos)
                    val block = state.block

                    if (!block.isAir(mc.theWorld, pos) && block.registryName.split(":").last() != "standing_sign") {
                        val nameOnly = block.registryName.split(":").last()
                        yMap[y] = nameOnly
                    }
                }

                if (yMap.isNotEmpty()) {
                    data.columns[columnKey] = yMap
                }
            }
        }

        roomData[name] = data
        mc.thePlayer.addChatMessage(ChatComponentText("Room $name scanned and data (${data.columns.size} columns) collected."))
    }

    /**
     * Export all the scanned rooms to a JSON file.
     */
    private fun exportRooms() {
        try {
            val json = gson.toJson(roomData)
            val file = java.io.File("./room_data.json")
            file.writeText(json)
            mc.thePlayer.addChatMessage(ChatComponentText("Room data exported to ${file.absolutePath}"))
        } catch (e: Exception) {
            mc.thePlayer.addChatMessage(ChatComponentText("§cFailed to export room data: ${e.message}"))
        }
    }

}