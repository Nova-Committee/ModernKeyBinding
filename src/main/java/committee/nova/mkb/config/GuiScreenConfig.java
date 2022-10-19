package committee.nova.mkb.config;

import committee.nova.mkb.ModernKeyBinding;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiScreenConfig extends GuiConfig {
    public GuiScreenConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement(Config.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                ModernKeyBinding.MODID,
                ModernKeyBinding.MODID,
                false,
                false,
                "Modern KeyBinding Config");
    }
}
