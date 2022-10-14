package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GuiKeyBindingList.KeyEntry.class})
public abstract class MixinKeyEntry {

    @Shadow(aliases = {"field_148284_a", "this$0"})
    private GuiKeyBindingList outer;

    @Shadow
    @Final
    private GuiButton btnReset;

    @Shadow
    @Final
    private KeyBinding keybinding;

    @Shadow
    @Final
    private GuiButton btnChangeKeyBinding;

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 0))
    public void inject$drawEntry$1(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, CallbackInfo ci) {
        btnReset.enabled = !((IKeyBinding) keybinding).isSetToDefaultValue();
    }

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 1))
    public void inject$drawEntry$2(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, CallbackInfo ci) {
        btnChangeKeyBinding.displayString = ((IKeyBinding) keybinding).getDisplayName();
        boolean conflicted = false;
        boolean keyCodeModifierConflict = true; // less severe form of conflict, like SHIFT conflicting with SHIFT+G

        final GuiControls controls = ((AccessorGuiBindingList) outer).getField_148191_k();
        final Minecraft mc = controls.mc;

        if (this.keybinding.getKeyCode() != 0) {
            for (KeyBinding keybinding : mc.gameSettings.keyBindings) {
                final IKeyBinding mixined = (IKeyBinding) keybinding;
                if (keybinding != this.keybinding && mixined.conflicts(this.keybinding)) {
                    conflicted = true;
                    keyCodeModifierConflict &= mixined.hasKeyCodeModifierConflict(this.keybinding);
                }
            }
        }

        if (controls.buttonId == this.keybinding) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
        } else if (conflicted) {
            this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + this.btnChangeKeyBinding.displayString;
        }
    }

    @Inject(method = "mousePressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
    public void inject$mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_, CallbackInfoReturnable<Boolean> cir) {
        ((IKeyBinding) keybinding).setToDefault();
    }
}
