package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GuiKeyBindingList.KeyEntry.class})
public abstract class MixinKeyEntry {
    @Shadow
    @Final
    private GuiButton btnReset;

    @Shadow
    @Final
    private KeyBinding field_148282_b;

    @Shadow
    @Final
    private GuiButton btnChangeKeyBinding;

    @Shadow(aliases = {"field_148284_a", "this$0"})
    private GuiKeyBindingList outer;

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 0))
    public void onDrawEntry2(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci) {
        btnReset.enabled = !((IKeyBinding) field_148282_b).isSetToDefaultValue();
    }

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 1))
    public void onDrawEntry3(GuiButton button, Minecraft mc, int i1, int i2) {
        btnChangeKeyBinding.displayString = ((IKeyBinding) field_148282_b).getDisplayName();
        boolean conflicted = false;
        boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G

        if (this.field_148282_b.getKeyCode() != 0) {
            for (KeyBinding keybinding : mc.gameSettings.keyBindings) {
                final IKeyBinding mixined = (IKeyBinding) keybinding;
                if (keybinding != this.field_148282_b && mixined.conflicts(this.field_148282_b)) {
                    conflicted = true;
                    keyCodeModifierConflict &= mixined.hasKeyCodeModifierConflict(this.field_148282_b);
                }
            }
        }

        if (((AccessorGuiBindingList) outer).getField_148191_k().buttonId == this.field_148282_b) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
        } else if (conflicted) {
            this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + this.btnChangeKeyBinding.displayString;
        }

        button.drawButton(mc, i1, i2);
    }

    @Inject(method = "mousePressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
    public void onMousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_, CallbackInfoReturnable<Boolean> cir) {
        ((IKeyBinding) field_148282_b).setToDefault();
    }
}
