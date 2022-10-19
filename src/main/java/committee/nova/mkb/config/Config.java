package committee.nova.mkb.config;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    private static Configuration config;
    private static boolean nonConflictKeys;

    public static void init(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "ModernKeyBinding.cfg"));
        sync();
    }

    public static void sync() {
        config.load();
        nonConflictKeys = config.getBoolean("nonConflictKeys", Configuration.CATEGORY_GENERAL, false, "No conflicts even multiple keybindings are using the same key.", "cfg.mkb.nonConflictKeys");
        config.save();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static boolean nonConflictKeys() {
        return nonConflictKeys;
    }
}
