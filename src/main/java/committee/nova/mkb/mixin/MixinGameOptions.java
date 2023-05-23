package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.options.KeyBindingOptions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;


@Mixin(GameOptions.class)
public abstract class MixinGameOptions {
    @Shadow
    @Final
    public KeyBinding[] allKeys;

    @Shadow
    @Final
    public KeyBinding forwardKey;

    @Shadow
    @Final
    public KeyBinding backKey;

    @Shadow
    @Final
    public KeyBinding leftKey;

    @Shadow
    @Final
    public KeyBinding rightKey;

    @Shadow
    @Final
    public KeyBinding jumpKey;

    @Shadow
    @Final
    public KeyBinding sneakKey;

    @Shadow
    @Final
    public KeyBinding sprintKey;

    @Shadow
    @Final
    public KeyBinding attackKey;

    @Shadow
    @Final
    public KeyBinding chatKey;

    @Shadow
    @Final
    public KeyBinding playerListKey;

    @Shadow
    @Final
    public KeyBinding commandKey;

    @Shadow
    @Final
    public KeyBinding togglePerspectiveKey;

    @Shadow
    @Final
    public KeyBinding smoothCameraKey;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$init(MinecraftClient client, File optionsFile, CallbackInfo ci) {
        KeyBindingOptions.read(this.allKeys);
        setKeybindProperties();
    }

    private void setKeybindProperties() {
        final KeyBinding[] keyBindings = {forwardKey, backKey, leftKey, rightKey, jumpKey, sneakKey,
                sprintKey, attackKey, chatKey, playerListKey, commandKey, togglePerspectiveKey, smoothCameraKey};
        for (final KeyBinding binding : keyBindings)
            ((IKeyBinding) binding).setKeyConflictContext(KeyConflictContext.IN_GAME);
    }

    //@Redirect(method = "accept", at = @At(value = "INVOKE",
    //        target = "Lnet/minecraft/client/option/GameOptions$Visitor;visitString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
    //        ordinal = 3))
    //private String redirect$write$trap(GameOptions.Visitor instance, String s, String s) {
    //    //Trap
    //    return x;
    //}

    //@Redirect(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setBoundKey(Lnet/minecraft/client/util/InputUtil$Key;)V"))
    //private void redirect$load$trap(KeyBinding instance, InputUtil.Key boundKey) {
    //    //Trap
    //}

    @Inject(method = "write", at = @At("HEAD"))
    private void inject$write(CallbackInfo ci) {
        try {
            KeyBindingOptions.write(this.allKeys);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
