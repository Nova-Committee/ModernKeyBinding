package committee.nova.examplemod.proxies

import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

class CommonProxy {
  def preInit(event: FMLPreInitializationEvent): Unit = {}

  def init(event: FMLInitializationEvent): Unit = {}

  def postInit(event: FMLPostInitializationEvent): Unit = {}
}
