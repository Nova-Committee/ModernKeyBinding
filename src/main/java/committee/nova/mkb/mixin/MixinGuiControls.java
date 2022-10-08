package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiControls.class)
public abstract class MixinGuiControls extends GuiScreen {
    @Shadow
    public KeyBinding buttonId;

    @Shadow
    private GameSettings options;

    @Shadow
    private GuiButton field_146493_s;

    @Shadow
    public long field_152177_g;

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
    public void inject$mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        ((IKeyBinding) buttonId).setKeyModifierAndCode(KeyModifier.getActiveModifier(), -100 + mouseButton);
    }

    /**
     * @author Tapio
     * @reason Set keybinding with modifier
     */
    @Overwrite
    public void keyTyped(char typedChar, int keyCode) {
        if (buttonId == null) {
            if (keyCode == 1) {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
            }
            return;
        }
        final IKeyBinding mixined = (IKeyBinding) buttonId;
        if (keyCode == 1) {
            mixined.setKeyModifierAndCode(KeyModifier.NONE, 0);
            this.options.setOptionKeyBinding(this.buttonId, 0);
        } else if (keyCode != 0) {
            mixined.setKeyModifierAndCode(KeyModifier.getActiveModifier(), keyCode);
            this.options.setOptionKeyBinding(this.buttonId, keyCode);
        } else if (typedChar > 0) {
            mixined.setKeyModifierAndCode(KeyModifier.getActiveModifier(), typedChar + 256);
            this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
        }

        if (!KeyModifier.isKeyCodeModifier(keyCode)) this.buttonId = null;
        this.field_152177_g = Minecraft.getSystemTime();
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    @Redirect(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyCode(I)V"))
    public void redirect$actionPerformed(KeyBinding instance, int ignored) {
        ((IKeyBinding) instance).setToDefault();
    }

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCodeDefault()I"))
    public int redirect$drawScreen$trap(KeyBinding instance) {
        return instance.getKeyCode();
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V"))
    public void inject$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        final KeyBinding[] keyBindings = options.keyBindings;
        final int x = keyBindings.length;
        boolean flag1 = false;
        for (KeyBinding keybinding : keyBindings) {
            if (!((IKeyBinding) keybinding).isSetToDefaultValue()) {
                flag1 = true;
                break;
            }
        }
        field_146493_s.enabled = flag1;
    }
}
