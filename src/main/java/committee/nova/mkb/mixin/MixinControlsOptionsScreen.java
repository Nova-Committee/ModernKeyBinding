package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ControlsOptionsScreen.class)
public abstract class MixinControlsOptionsScreen extends GameOptionsScreen {
    @Shadow
    public KeyBinding focusedBinding;

    @Shadow
    public long time;

    public MixinControlsOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void inject$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (focusedBinding != null) {
            final IKeyBinding extended = (IKeyBinding) focusedBinding;
            if (keyCode == 256) {
                extended.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputUtil.UNKNOWN_KEY);
                this.gameOptions.setKeyCode(focusedBinding, InputUtil.UNKNOWN_KEY);
            } else {
                extended.setKeyModifierAndCode(KeyModifier.getActiveModifier(), InputUtil.fromKeyCode(keyCode, scanCode));
                this.gameOptions.setKeyCode(focusedBinding, InputUtil.fromKeyCode(keyCode, scanCode));
            }
            if (!KeyModifier.isKeyCodeModifier(((IKeyBinding) focusedBinding).getKey())) focusedBinding = null;
            this.time = Util.getMeasuringTimeMs();
            KeyBinding.updateKeysByCode();
            cir.setReturnValue(true);
            return;
        }
        cir.setReturnValue(super.keyPressed(keyCode, scanCode, modifiers));
    }

    @Redirect(method = "init", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/option/ControlsOptionsScreen;addButton(Lnet/minecraft/client/gui/widget/ClickableWidget;)Lnet/minecraft/client/gui/widget/ClickableWidget;",
            ordinal = 2)
    )
    private ClickableWidget redirect$init(ControlsOptionsScreen instance, ClickableWidget clickableWidget) {
        return addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), button -> {
            for (KeyBinding keyBinding : this.gameOptions.keysAll) ((IKeyBinding) keyBinding).setToDefault();
            KeyBinding.updateKeysByCode();
        }));
    }
}
