package committee.nova.mkb.mixin;

import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiKeyBindingList.class)
public interface AccessorGuiBindingList {
    @Accessor
    GuiControls getField_148191_k();
}
