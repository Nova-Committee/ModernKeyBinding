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

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V", ordinal = 0))
    public void onDrawEntry1(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci) {
        btnReset.enabled = !((IKeyBinding) field_148282_b).isSetToDefaultValue();
    }

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiButton;drawButton(Lnet/minecraft/client/Minecraft;II)V"))
    public void onDrawEntry2(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_, CallbackInfo ci) {
        btnChangeKeyBinding.displayString = ((IKeyBinding) field_148282_b).getDisplayName();
    }

    @Inject(method = "mousePressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/GameSettings;setOptionKeyBinding(Lnet/minecraft/client/settings/KeyBinding;I)V"))
    public void onMousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_, CallbackInfoReturnable<Boolean> cir) {
        ((IKeyBinding) field_148282_b).setToDefault();
    }
}
