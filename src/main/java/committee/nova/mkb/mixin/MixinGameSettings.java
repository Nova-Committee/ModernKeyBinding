package committee.nova.mkb.mixin;

import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyConflictContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameSettings.class)
public abstract class MixinGameSettings {


    @Shadow
    public KeyBinding keyBindForward;

    @Shadow
    public KeyBinding keyBindLeft;

    @Shadow
    public KeyBinding keyBindBack;

    @Shadow
    public KeyBinding keyBindRight;

    @Shadow
    public KeyBinding keyBindJump;

    @Shadow
    public KeyBinding keyBindSneak;

    @Shadow
    public KeyBinding keyBindSprint;

    @Shadow
    public KeyBinding keyBindAttack;

    @Shadow
    public KeyBinding keyBindChat;

    @Shadow
    public KeyBinding keyBindPlayerList;

    @Shadow
    public KeyBinding keyBindCommand;

    @Shadow
    public KeyBinding keyBindTogglePerspective;

    @Shadow
    public KeyBinding keyBindSmoothCamera;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void onInit1(CallbackInfo ci) {
        setForgeKeyBindProperties();
    }

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("RETURN"))
    public void onInit2(Minecraft mc, File dir, CallbackInfo ci) {
        setForgeKeyBindProperties();
    }

    private void setForgeKeyBindProperties() {
        setKeyConflictContext(keyBindForward, keyBindLeft, keyBindBack, keyBindRight, keyBindJump, keyBindSneak,
                keyBindSprint, keyBindAttack, keyBindChat, keyBindPlayerList, keyBindCommand, keyBindTogglePerspective, keyBindSmoothCamera);
    }

    private static void setKeyConflictContext(KeyBinding... keybindingArray) {
        final IKeyConflictContext inGame = KeyConflictContext.IN_GAME;
        for (final KeyBinding keyBinding : keybindingArray) {
            final IKeyBinding mixined = (IKeyBinding) keyBinding;
            mixined.setKeyConflictContext(inGame);
        }
    }
}
