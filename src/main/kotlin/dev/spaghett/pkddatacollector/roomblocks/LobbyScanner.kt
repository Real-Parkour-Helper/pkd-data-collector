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

class LobbyScanner : Command("scanlobby") {
    private val mc = Minecraft.getMinecraft()
    private var roomData: RoomData? = null
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val includeMeta = listOf("stair", "plank", "log", "glass", "slab")

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
            name = "Lobby",
            columns = mutableMapOf()
        )

        for (x in -13..13) {
            for (z in -13..13) {
                val columnKey = "${x},${z}"
                val yMap = mutableMapOf<Int, String>()

                for (y in -2..7) {
                    val pos = BlockPos(startPos.addVector(x.toDouble(), y.toDouble(), z.toDouble()))
                    val state = mc.theWorld.getBlockState(pos)
                    val block = state.block

                    if (!block.isAir(mc.theWorld, pos)) {
                        var blockName = block.registryName.split(":").last()
                        blockName = "$blockName:${state.block.getMetaFromState(state)}"
                        yMap[y] = blockName
                    }
                }

                if (yMap.isNotEmpty()) {
                    data.columns[columnKey] = yMap
                }
            }
        }

        roomData = data
        mc.thePlayer.addChatMessage(ChatComponentText("§aLobby data scanned successfully!"))

        try {
            val json = gson.toJson(roomData)
            val file = File("./lobby_data.json")
            file.writeText(json)
            mc.thePlayer.addChatMessage(ChatComponentText("§aLobby data exported to ${file.absolutePath}"))
        } catch (e: Exception) {
            mc.thePlayer.addChatMessage(ChatComponentText("§cFailed to export room data."))
            e.printStackTrace()
        }
    }

}