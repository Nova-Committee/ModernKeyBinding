package committee.nova.mkb.config;

import committee.nova.mkb.ModernKeyBinding;
import cpw.mods.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

public class GuiScreenConfig extends GuiConfig {
    public GuiScreenConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement<>(Config.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                ModernKeyBinding.MODID,
                ModernKeyBinding.MODID,
                false,
                false,
                "Modern KeyBinding Config");
    }
}
