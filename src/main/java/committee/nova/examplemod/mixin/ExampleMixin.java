package committee.nova.examplemod.mixin;

import committee.nova.examplemod.ExampleMod$;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public abstract class ExampleMixin {
    @Inject(method = "initGui", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        ExampleMod$.MODULE$.LOGGER().info("Line printed by example mod mixin :D");
    }
}
