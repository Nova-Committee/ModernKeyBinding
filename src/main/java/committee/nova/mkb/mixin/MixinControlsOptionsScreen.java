package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsOptionsScreen.class)
public abstract class MixinControlsOptionsScreen extends Screen {
    @Shadow
    public KeyBinding selectedKeyBinding;

    @Shadow
    private GameOptions options;

    @Shadow
    public long time;

    @Shadow
    private ButtonWidget resetButton;

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyBindingCode(Lnet/minecraft/client/options/KeyBinding;I)V"))
    public void inject$mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        ((IKeyBinding) selectedKeyBinding).setKeyModifierAndCode(KeyModifier.getActiveModifier(), -100 + mouseButton);
    }

    /**
     * @author Tapio
     * @reason Set keybinding with modifier
     */
    @Overwrite
    public void keyPressed(char typedChar, int keyCode) {
        if (selectedKeyBinding == null) {
            if (keyCode == 1) {
                this.client.openScreen(null);
                this.client.closeScreen();
            }
            return;
        }
        final IKeyBinding mixined = (IKeyBinding) selectedKeyBinding;
        if (keyCode == 1) {
            mixined.setKeyModifierAndCode(KeyModifier.NONE, 0);
            this.options.setKeyBindingCode(this.selectedKeyBinding, 0);
        } else if (keyCode != 0) {
            mixined.setKeyModifierAndCode(KeyModifier.getActiveModifier(), keyCode);
            this.options.setKeyBindingCode(this.selectedKeyBinding, keyCode);
        } else if (typedChar > 0) {
            mixined.setKeyModifierAndCode(KeyModifier.getActiveModifier(), typedChar + 256);
            this.options.setKeyBindingCode(this.selectedKeyBinding, typedChar + 256);
        }

        if (!KeyModifier.isKeyCodeModifier(keyCode)) this.selectedKeyBinding = null;
        this.time = MinecraftClient.getTime();
        KeyBinding.updateKeysByCode();
    }

    @Inject(method = "buttonClicked", at = @At("HEAD"), cancellable = true)
    public void inject$actionPerformed(ButtonWidget button, CallbackInfo ci) {
        if (button.id != 201) return;
        final Screen current = client.currentScreen;
        final ConfirmScreen confirm = new ConfirmScreen((yes, key) -> {
            if (yes) {
                KeyBinding[] keyBindings = this.client.options.keysAll;
                for (final KeyBinding keyBinding : keyBindings) ((IKeyBinding) keyBinding).setToDefault();
                KeyBinding.updateKeysByCode();
            }
            client.openScreen(current);
        }, I18n.translate("menu.mkb.reset"), "", 13468);
        client.openScreen(confirm);
        ci.cancel();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/KeyBinding;getDefaultCode()I"))
    public int redirect$drawScreen$trap(KeyBinding instance) {
        return instance.getCode();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(IIF)V"))
    public void inject$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        final KeyBinding[] keyBindings = options.keysAll;
        final int x = keyBindings.length;
        boolean flag1 = false;
        for (KeyBinding keybinding : keyBindings) {
            if (!((IKeyBinding) keybinding).isSetToDefaultValue()) {
                flag1 = true;
                break;
            }
        }
        resetButton.active = flag1;
    }
}
