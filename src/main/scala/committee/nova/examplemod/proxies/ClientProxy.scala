package committee.nova.examplemod.proxies

import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

class ClientProxy extends CommonProxy {
  override def preInit(event: FMLPreInitializationEvent): Unit = {}

  override def init(event: FMLInitializationEvent): Unit = {}

  override def postInit(event: FMLPostInitializationEvent): Unit = {}
}
