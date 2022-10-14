package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;

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

    @Shadow
    public KeyBinding[] keyBindings;

    @Shadow
    private File optionsFile;

    @Shadow
    private Map<SoundCategory, Float> mapSoundLevels;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void inject$init$1(CallbackInfo ci) {
        setForgeKeyBindProperties();
    }

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("RETURN"))
    public void inject$init$2(Minecraft mc, File dir, CallbackInfo ci) {
        setForgeKeyBindProperties();
    }

    @Redirect(method = "saveOptions", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;println(Ljava/lang/String;)V", ordinal = 61))
    public void redirect$saveOptions$trap(PrintWriter instance, String s) {
        // Do nothing
    }

    @Inject(method = "saveOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundCategory;values()[Lnet/minecraft/client/audio/SoundCategory;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void inject$saveOptions(CallbackInfo ci, PrintWriter printWriter) {
        for (final KeyBinding binding : keyBindings) {
            final String x = "key_" + binding.getKeyDescription() + ":" + binding.getKeyCode();
            final IKeyBinding mixined = (IKeyBinding) binding;
            printWriter.println(mixined.getKeyModifier() != KeyModifier.NONE ? x + ":" + mixined.getKeyModifier() : x);
        }
    }

    @Redirect(method = "loadOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;setKeyCode(I)V"))
    public void redirect$loadOptions$trap(KeyBinding instance, int p_151462_1_) {
        // Do nothing
    }

    @Inject(method = "loadOptions", at = @At("HEAD"))
    public void inject$loadOptions(CallbackInfo ci) {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            final BufferedReader bufferedreader = new BufferedReader(new FileReader(this.optionsFile));
            String s;
            this.mapSoundLevels.clear();
            while ((s = bufferedreader.readLine()) != null) {
                String[] aString = s.split(":");
                KeyBinding[] var4 = this.keyBindings;
                int var5 = var4.length;
                int var6;
                for (var6 = 0; var6 < var5; ++var6) {
                    KeyBinding keybind = var4[var6];
                    if (aString[0].equals("key_" + keybind.getKeyDescription())) {
                        final String s2 = aString[1];
                        final IKeyBinding mixined = (IKeyBinding) keybind;
                        if (aString.length > 2) {
                            mixined.setKeyModifierAndCode(KeyModifier.valueFromString(aString[2]), Integer.parseInt(s2));
                        } else mixined.setKeyModifierAndCode(KeyModifier.NONE, Integer.parseInt(s2));
                    }
                }
            }
            bufferedreader.close();
        } catch (Exception e) {
            e.printStackTrace();
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
