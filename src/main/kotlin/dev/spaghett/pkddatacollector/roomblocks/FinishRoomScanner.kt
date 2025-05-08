package dev.spaghett.pkddatacollector.roomblocks

import com.google.gson.GsonBuilder
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import net.minecraft.client.Minecraft
import net.minecraft.util.BlockPos
import net.minecraft.util.ChatComponentText
import net.minecraft.util.Vec3
import java.io.File
import kotlin.math.floor

class FinishRoomScanner :  Command("scanfinishroom") {

    private val mc = Minecraft.getMinecraft()
    private var roomData: RoomData? = null
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val includeMeta = listOf("stair", "slab")

    init {
        register()
    }

    @DefaultHandler
    fun handle() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            println("§cPlayer or world is null")
            return
        }

        val startPos = Vec3(floor(mc.thePlayer.posX), floor(mc.thePlayer.posY) - 1.0, floor(mc.thePlayer.posZ))

        val data = RoomData(
            name = "FinishRoom",
            columns = mutableMapOf()
        )

        for (x in -10..10) {
            for (z in -5..32) {
                val columnKey = "${x},${z}"
                val yMap = mutableMapOf<Int, String>()

                for (y in -4..13) {
                    val pos = BlockPos(startPos.addVector(x.toDouble(), y.toDouble(), z.toDouble()))
                    val state = mc.theWorld.getBlockState(pos)
                    val block = state.block

                    if (!block.isAir(mc.theWorld, pos)) {
                        var blockName = block.registryName.split(":").last()

                        for (meta in includeMeta) {
                            if (meta in blockName) {
                                blockName = "$blockName:${state.block.getMetaFromState(state)}"
                            }
                        }

                        yMap[y] = blockName
                    }
                }

                if (yMap.isNotEmpty()) {
                    data.columns[columnKey] = yMap
                }
            }
        }

        roomData = data
        mc.thePlayer.addChatMessage(ChatComponentText("§aFinish room data scanned successfully!"))

        try {
            val json = gson.toJson(roomData)
            val file = File("./finish_room_data.json")
            file.writeText(json)
            mc.thePlayer.addChatMessage(ChatComponentText("§aRoom data exported to ${file.absolutePath}"))
        } catch (e: Exception) {
            mc.thePlayer.addChatMessage(ChatComponentText("§cFailed to export room data."))
            e.printStackTrace()
        }
    }
}