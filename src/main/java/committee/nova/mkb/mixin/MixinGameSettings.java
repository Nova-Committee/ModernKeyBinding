package committee.nova.mkb.mixin;

import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import committee.nova.mkb.util.Utilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedReader;
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

    @ModifyArg(method = "saveOptions", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V", ordinal = 55))
    public String onSaveOptions(String x) {
        final int i = Utilities.getFirstOn(x, ':');
        if (i == -1) return x;
        int keyCode;
        try {
            keyCode = Integer.parseInt(x.substring(i + 1));
        } catch (NumberFormatException e) {
            return x;
        }
        final KeyBinding keyBinding = Utilities.getKeyBindingByCode(keyCode);
        if (keyBinding == null) return x;
        final IKeyBinding mixined = (IKeyBinding) keyBinding;
        return mixined.getKeyModifier() != KeyModifier.NONE ? x + ":" + mixined.getKeyModifier() : x;
    }

    // Trap redirect
    @Redirect(method = "loadOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyCode(I)V"))
    public void onLoadOptions1(KeyBinding instance, int p_151462_1_) {
        //Do nothing
    }

    @Inject(method = "loadOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundCategory;values()[Lnet/minecraft/client/audio/SoundCategory;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onLoadOptions2(CallbackInfo ci, BufferedReader bufferedreader, String s, String[] aString, KeyBinding[] aKeybinding, int i) {
        for (int j = 0; j < i; ++j) {
            KeyBinding keybind = aKeybinding[j];

            if (aString[0].equals("key_" + keybind.getKeyDescription())) {
                final String s2 = aString[1];
                final IKeyBinding mixined = (IKeyBinding) keybind;
                try {
                    if (s2.indexOf(':') != -1) {
                        String[] t = s2.split(":");
                        mixined.setKeyModifierAndCode(KeyModifier.valueFromString(t[1]), Integer.parseInt(t[0]));
                    } else mixined.setKeyModifierAndCode(KeyModifier.NONE, Integer.parseInt(s2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
