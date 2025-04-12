package dev.spaghett.pkddatacollector

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = DataCollector.MOD_ID,
    name = DataCollector.MOD_NAME,
    version = DataCollector.VERSION
)
class DataCollector {

    companion object {
        const val MOD_ID = "pkd_data_collector"
        const val MOD_NAME = "PkdDataCollector"
        const val VERSION = "0.1.0"
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
    }
}
