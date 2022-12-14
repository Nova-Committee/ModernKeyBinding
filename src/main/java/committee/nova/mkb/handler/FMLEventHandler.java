package committee.nova.mkb.handler;

import committee.nova.mkb.ModernKeyBinding;
import committee.nova.mkb.config.Config;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FMLEventHandler {
    public static void register() {
        FMLCommonHandler.instance().bus().register(new FMLEventHandler());
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent event) {
        if (!event.modID.equals(ModernKeyBinding.MODID)) return;
        Config.getConfig().save();
        Config.sync();
    }
}
