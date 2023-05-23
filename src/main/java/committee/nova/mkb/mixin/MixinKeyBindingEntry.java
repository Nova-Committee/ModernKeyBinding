package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class MixinKeyBindingEntry {
    @Mutable
    @Shadow
    @Final
    private ButtonWidget resetButton;

    @Shadow(aliases = {"this$0", "field_2742"})
    private ControlsListWidget outerThis;

    @Shadow
    @Final
    private KeyBinding binding;

    @Shadow
    @Final
    private ButtonWidget editButton;

    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/option/ControlsListWidget;Lnet/minecraft/client/option/KeyBinding;Lnet/minecraft/text/Text;)V", at = @At("RETURN"))
    private void inject$init(ControlsListWidget controlsListWidget, KeyBinding binding, Text text, CallbackInfo ci) {
        resetButton = new ButtonWidget(0, 0, 50, 20, Text.translatable("controls.reset"), button -> {
            ((IKeyBinding) binding).setToDefault();
            ((AccessorScreen) ((AccessorControlsListWidget) outerThis).getParent()).getClient().options.setKeyCode(binding, binding.getDefaultKey());
            KeyBinding.updateKeysByCode();
        }) {
            @Override
            protected MutableText getNarrationMessage() {
                return Text.translatable("narrator.controls.reset", text);
            }
        };
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", ordinal = 1))
    private void inject$render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        boolean flag = ((AccessorControlsListWidget) outerThis).getParent().selectedKeyBinding == this.binding;
        boolean flag1 = false;
        boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G
        if (!this.binding.isUnbound()) {
            for (KeyBinding keybinding : ((AccessorScreen) ((AccessorControlsListWidget) outerThis).getParent()).getClient().options.allKeys) {
                if (keybinding != this.binding && this.binding.equals(keybinding)) {
                    flag1 = true;
                    keyCodeModifierConflict &= ((IKeyBinding) keybinding).hasKeyCodeModifierConflict(this.binding);
                }
            }
        }
        this.editButton.setMessage(this.binding.getBoundKeyLocalizedText());

        if (flag) {
            this.editButton.setMessage((Text.literal("> ")).append(this.editButton.getMessage().copy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW));
        } else if (flag1) {
            this.editButton.setMessage(this.editButton.getMessage().copy().formatted(keyCodeModifierConflict ? Formatting.GOLD : Formatting.RED));
        }
    }
}
