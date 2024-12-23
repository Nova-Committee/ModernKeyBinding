package committee.nova.mkb;

import committee.nova.mkb.compat.ControllingCompat;
import committee.nova.mkb.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ModernKeyBinding implements ClientModInitializer {
    private static Config config;
    private static boolean nonConflictKeys;

    @Override
    public void onInitializeClient() {
        config = Config.of("ModernKeyBinding-Config").provider(path ->
                "#ModernKeyBinding-Config" + "\n"
                        + "nonConflictKeys=true"
        ).request();
        nonConflictKeys = config.getOrDefault("nonConflictKeys", false);
        if (FabricLoader.getInstance().isModLoaded("controlling")) {
            try {
                ControllingCompat.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static boolean nonConflictKeys() {
        return nonConflictKeys;
    }
}
