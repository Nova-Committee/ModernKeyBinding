package committee.nova.mkb;

import committee.nova.mkb.config.Config;
import committee.nova.mkb.handler.FMLEventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModernKeyBinding.MODID, useMetadata = true, guiFactory = "committee.nova.mkb.config.GuiFactory")
public class ModernKeyBinding {
    public static final String MODID = "mkb";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        if (e.getSide().isClient()) Config.init(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        if (e.getSide().isClient()) FMLEventHandler.register();
    }
}
