package committee.nova.mkb.mixin;

import committee.nova.mkb.util.Utilities;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {
    @Redirect(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    public int redirect$keyTyped(KeyBinding instance) {
        final int original = instance.getKeyCode();
        return Utilities.isActiveIgnoreKeyCode(instance) ? original : original + Integer.MIN_VALUE;
    }

    @Redirect(method = "checkHotbarKeys", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    public int redirect$checkHotbarKeys(KeyBinding instance) {
        final int original = instance.getKeyCode();
        return Utilities.isActiveIgnoreKeyCode(instance) ? original : original + Integer.MIN_VALUE;
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    public int redirect$mouseClicked(KeyBinding instance) {
        final int original = instance.getKeyCode();
        return Utilities.isActiveIgnoreKeyCode(instance) ? original : original + Integer.MIN_VALUE;
    }
}
