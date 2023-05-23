package committee.nova.mkb;

import committee.nova.mkb.config.Config;
import net.fabricmc.api.ClientModInitializer;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ModernKeyBinding implements ClientModInitializer {
    private static Config config;
    private static boolean nonConflictKeys;
    public static Logger LOGGER = LogManager.getLogManager().getLogger("mkb");

    @Override
    public void onInitializeClient() {
        config = Config.of("ModernKeyBinding-Config").provider(path ->
                "#ModernKeyBinding-Config" + "\n"
                        + "nonConflictKeys=false"
        ).request();
        nonConflictKeys = config.getOrDefault("nonConflictKeys", false);
    }

    public static Config getConfig() {
        return config;
    }

    public static boolean nonConflictKeys() {
        return nonConflictKeys;
    }
}
