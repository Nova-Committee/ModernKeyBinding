package committee.nova.mkb.mixin;

import com.mojang.realmsclient.gui.ChatFormatting;
import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_1822;
import net.minecraft.client.gui.screen.options.ControlsListWidget;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.Tessellator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1822.class})
public abstract class MixinKeyEntry {
    @Shadow
    @Final
    private ButtonWidget field_7809;

    @Shadow
    @Final
    private KeyBinding field_7806;

    @Shadow
    @Final
    private ButtonWidget field_7808;

    @Shadow(aliases = {"field_7805", "this$0"})
    private ControlsListWidget outer;

    @Inject(method = "method_6700", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/MinecraftClient;II)V", ordinal = 0))
    public void inject$drawEntry$1(int j, int k, int l, int m, int tessellator, Tessellator n, int o, int bl, boolean par9, CallbackInfo ci) {
        field_7809.active = !((IKeyBinding) field_7806).isSetToDefaultValue();
    }

    @Inject(method = "method_6700", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/MinecraftClient;II)V", ordinal = 1))
    public void inject$drawEntry$2(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci) {
        field_7808.message = ((IKeyBinding) field_7806).getDisplayName();
        boolean conflicted = false;
        boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G

        final ControlsOptionsScreen controls = ((AccessorControlsListWidget) outer).getParent();
        final MinecraftClient mc = ((AccessorScreen) controls).getClient();
        //todo: is this ok?

        if (this.field_7806.getCode() != 0) {
            for (KeyBinding keybinding : mc.options.keysAll) {
                final IKeyBinding mixined = (IKeyBinding) keybinding;
                if (keybinding != this.field_7806 && mixined.conflicts(this.field_7806)) {
                    conflicted = true;
                    keyCodeModifierConflict &= mixined.hasKeyCodeModifierConflict(this.field_7806);
                }
            }
        }

        if (controls.selectedKeyBinding == this.field_7806) {
            this.field_7808.message = ChatFormatting.WHITE + "> " + ChatFormatting.YELLOW + this.field_7808.message + ChatFormatting.WHITE + " <";
        } else if (conflicted) {
            this.field_7808.message = (keyCodeModifierConflict ? ChatFormatting.GOLD : ChatFormatting.RED) + this.field_7808.message;
        }
    }

    @Inject(method = "method_6699", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/options/GameOptions;setKeyBindingCode(Lnet/minecraft/client/options/KeyBinding;I)V"))
    public void inject$mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_, CallbackInfoReturnable<Boolean> cir) {
        ((IKeyBinding) field_7806).setToDefault();
    }
}
