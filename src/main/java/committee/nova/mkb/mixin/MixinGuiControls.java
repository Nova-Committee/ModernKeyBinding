package committee.nova.mkb.mixin;

import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiControls.class)
public abstract class MixinGuiControls {
    @Shadow
    public KeyBinding buttonId;

    @Shadow
    private GameSettings options;

    @Shadow
    private GuiButton field_146493_s;

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        ((IKeyBinding) buttonId).setKeyModifierAndCode(KeyModifier.getActiveModifier(), -100 + mouseButton);
    }

    @Inject(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
    public void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        final boolean flag = keyCode == 1;
        ((IKeyBinding) buttonId).setKeyModifierAndCode(flag ? KeyModifier.NONE : KeyModifier.getActiveModifier(), flag ? 0 : keyCode);
    }

    @Redirect(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyCode(I)V"))
    public void onActionPerformed(KeyBinding instance, int ignored) {
        ((IKeyBinding) instance).setToDefault();
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCodeDefault()I"))
    public int trapDrawScreen(KeyBinding instance) {
        return instance.getKeyCode();
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V"))
    public void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        final KeyBinding[] keyBindings = options.keyBindings;
        final int x = keyBindings.length;
        boolean flag1 = true;
        for (KeyBinding keybinding : keyBindings) {
            if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
                flag1 = false;
                break;
            }
        }
        field_146493_s.enabled = !flag1;
    }
}
