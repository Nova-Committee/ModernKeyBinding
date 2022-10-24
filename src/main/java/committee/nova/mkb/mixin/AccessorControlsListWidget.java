package committee.nova.mkb.mixin;

import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsListWidget.class)
public interface AccessorControlsListWidget {
    @Accessor
    ControlsOptionsScreen getParent();
}
