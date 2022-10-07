package committee.nova.mkb.mixin;

import committee.nova.mkb.keybinding.IKeyBinding;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiKeyBindingList.KeyEntry.class)
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
    private boolean keyCodeModifierConflict = false;

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 0))
    public void onDrawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci) {
        btnReset.enabled = !((IKeyBinding) field_148282_b).isSetToDefaultValue();
    }

    //@Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I", ordinal = 4))
    //public int trapDrawEntry(KeyBinding instance) {
    //    return Integer.MIN_VALUE;
    //}

    //@Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;getKeyCode()I", ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    //public void onDrawEntry1(
    //        int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci,
    //        boolean flag1, boolean flag2, KeyBinding[] akeybinding, int l1, int i2) {
    //    keyCodeModifierConflict = true;
    //    final int length = akeybinding.length;
    //    for (int j = 0; j < length; ++j) {
    //        KeyBinding binding = akeybinding[i2];
    //        final IKeyBinding mixined = (IKeyBinding) binding;
    //        if (binding != this.field_148282_b && mixined.conflicts(this.field_148282_b)) {
    //            flag2 = true;
    //            keyCodeModifierConflict &= mixined.hasKeyCodeModifierConflict(field_148282_b);
    //        }
    //    }
    //}
//
    //@Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    //public void onDrawEntry2(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci, boolean flag1, boolean flag2) {
    //    if (flag1 || !flag2) return;
    //    this.btnChangeKeyBinding.displayString = (keyCodeModifierConflict ? EnumChatFormatting.GOLD : EnumChatFormatting.RED) + this.btnChangeKeyBinding.displayString;
    //}
}
