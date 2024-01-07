package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeybindsScreen.class)
public abstract class MixinKeybindsScreen extends GameOptionsScreen {

    @Shadow
    @Nullable
    public KeyBinding selectedKeyBinding;

    @Shadow
    public long lastKeyCodeUpdateTime;

    @Shadow
    private ControlsListWidget controlsList;

    public MixinKeybindsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void inject$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (selectedKeyBinding != null) {
            final IKeyBinding extended = (IKeyBinding) selectedKeyBinding;
            if (keyCode == 256) {
                extended.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputUtil.UNKNOWN_KEY);
                this.gameOptions.setKeyCode(selectedKeyBinding, InputUtil.UNKNOWN_KEY);
            } else {
                extended.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputUtil.fromKeyCode(keyCode, scanCode));
                this.gameOptions.setKeyCode(selectedKeyBinding, InputUtil.fromKeyCode(keyCode, scanCode));
            }
            if (!KeyModifier.isKeyCodeModifier(((IKeyBinding) selectedKeyBinding).getKey())) selectedKeyBinding = null;
            this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
            this.controlsList.update();
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
    }

    @Redirect(method = "init", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/option/KeybindsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
            ordinal = 0)
    )
    private Element redirect$init(KeybindsScreen instance, Element element) {
        return this.addDrawableChild(ButtonWidget.builder(Text.translatable("controls.resetAll"), b -> {
            for (KeyBinding keyBinding : this.gameOptions.allKeys) ((IKeyBinding) keyBinding).setToDefault();
            controlsList.update();
        }).dimensions(this.width / 2 - 155, this.height - 29, 150, 20).build());
    }
}
