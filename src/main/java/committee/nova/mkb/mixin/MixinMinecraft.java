package committee.nova.mkb.mixin;

import committee.nova.mkb.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    @Final
    public static boolean isRunningOnMac;

    @Shadow
    public GameSettings gameSettings;

    @Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    public void inject$setIngameFocus(CallbackInfo ci) {
        if (!isRunningOnMac) Utilities.updateKeyBindState();
    }
}
