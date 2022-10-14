package committee.nova.mkb.mixin;

import committee.nova.mkb.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    @Final
    public static boolean isRunningOnMac;

    @Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    public void inject$setIngameFocus(CallbackInfo ci) {
        if (!isRunningOnMac) Utilities.updateKeyBindState();
    }

    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I"))
    public int redirect$dispatchKeyPresses$1(KeyBinding instance) {
        final int original = instance.getKeyCode();
        return Utilities.isActiveIgnoreKeyCode(instance) ? original : original + Integer.MIN_VALUE;
    }

    @Inject(method = "dispatchKeypresses", at = @At("TAIL"))
    public void inject$dispatchKeyPresses(CallbackInfo ci) {
        final int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
        if (i == 0 || Keyboard.isRepeatEvent()) return;
        if (!(currentScreen instanceof GuiControls)) return;
        final GuiControls controls = (GuiControls) currentScreen;
        if (Sys.getTime() * 1000L / Sys.getTimerResolution() - controls.time < 20L) return;
        if (!Keyboard.getEventKeyState()) controls.buttonId = null;
    }
}
