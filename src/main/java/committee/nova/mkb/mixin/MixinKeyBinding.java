package committee.nova.mkb.mixin;

import committee.nova.mkb.ModernKeyBinding;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyBindingMap;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = KeyBinding.class, priority = 1024)
public abstract class MixinKeyBinding implements IKeyBinding {
    @Shadow
    private int code;
    @Mutable
    @Shadow
    @Final
    private int defaultCode;

    @Shadow
    public abstract int getDefaultCode();

    @Shadow
    private int timesPressed;

    @Shadow
    public abstract int getCode();

    @Shadow
    @Final
    private static List<KeyBinding> KEYS;
    @Shadow
    private boolean pressed;
    //Backporting missing starts:
    private static final KeyBindingMap newHash = new KeyBindingMap();
    KeyModifier keyModifierDefault;
    KeyModifier keyModifier;
    IKeyConflictContext keyConflictContext;

    @Override
    public boolean isActiveAndMatches(int keyCode) {
        return keyCode != 0 && keyCode == this.code && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    @Override
    public void setKeyConflictContext(IKeyConflictContext keyConflictContext) {
        this.keyConflictContext = keyConflictContext;
    }

    @Override
    public IKeyConflictContext getKeyConflictContext() {
        return keyConflictContext != null ? keyConflictContext : KeyConflictContext.UNIVERSAL;
    }

    @Override
    public KeyModifier getKeyModifierDefault() {
        return keyModifierDefault != null ? keyModifierDefault : KeyModifier.NONE;
    }

    @Override
    public KeyModifier getKeyModifier() {
        return keyModifier != null ? keyModifier : KeyModifier.NONE;
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, int keyCode) {
        this.code = keyCode;
        if (keyModifier.matches(keyCode)) {
            keyModifier = KeyModifier.NONE;
        }
        newHash.removeKey((KeyBinding) (Object) this);
        this.keyModifier = keyModifier;
        newHash.addKey(keyCode, (KeyBinding) (Object) this);
        //todo
    }

    @Override
    public void setInitialKeyModifierAndCode(KeyModifier keyModifier, int keyCode) {
        setKeyModifierAndCode(keyModifier, keyCode);
        this.keyModifierDefault = keyModifier;
        this.defaultCode = keyCode;
    }

    @Override
    public void setToDefault() {
        setKeyModifierAndCode(getKeyModifierDefault(), getDefaultCode());
    }

    @Override
    public void press() {
        ++timesPressed;
    }

    @Override
    public boolean isSetToDefaultValue() {
        return getCode() == getDefaultCode() && getKeyModifier() == getKeyModifierDefault();
    }

    @Override
    public boolean conflicts(KeyBinding other) {
        final IKeyBinding keyBinding = (IKeyBinding) other;
        if (getKeyConflictContext().conflicts(keyBinding.getKeyConflictContext()) || keyBinding.getKeyConflictContext().conflicts(getKeyConflictContext())) {
            final KeyModifier keyModifier = getKeyModifier();
            final KeyModifier otherKeyModifier = keyBinding.getKeyModifier();
            if (keyModifier.matches(other.getCode()) || otherKeyModifier.matches(code)) return true;
            if (code == other.getCode()) {
                return getKeyModifier() == otherKeyModifier ||
                        // IN_GAME key contexts have a conflict when at least one modifier is NONE.
                        // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
                        // GUI and other key contexts do not have this limitation.
                        (getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME) &&
                                (keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE));
            }
        }
        return false;
    }

    @Override
    public boolean hasKeyCodeModifierConflict(KeyBinding other) {
        final IKeyBinding keyBinding = (IKeyBinding) other;
        return (getKeyConflictContext().conflicts(keyBinding.getKeyConflictContext()) || keyBinding.getKeyConflictContext().conflicts(getKeyConflictContext()))
                && (getKeyModifier().matches(other.getCode()) || keyBinding.getKeyModifier().matches(code));
    }

    @Override
    public String getDisplayName() {
        return getKeyModifier().getLocalizedComboName(code);
    }

    //Modifying existing starts:
    @Inject(method = "<init>", at = @At("RETURN"))
    public void inject$init(String description, int keyCode, String category, CallbackInfo ci) {
        newHash.addKey(keyCode, (KeyBinding) (Object) this);
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onTick(int keyCode, CallbackInfo ci) {
        ci.cancel();
        if (keyCode == 0) return;
        if (ModernKeyBinding.nonConflictKeys()) {
            newHash.lookupActives(keyCode).forEach(k -> ((IKeyBinding) k).press());
            return;
        }
        final KeyBinding keybinding = newHash.lookupActive(keyCode);
        if (keybinding != null) ((IKeyBinding) keybinding).press();
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void inject$setKeyPressed(int keyCode, boolean pressed, CallbackInfo ci) {
        if (keyCode == 0) return;
        ci.cancel();
        for (final KeyBinding binding : newHash.lookupAll(keyCode))
            if (binding != null) ((AccessorKeyBinding) binding).setPressed(pressed);
    }

    /**
     * @author Tapio
     * @reason Use the new HASH
     */
    @Overwrite
    public static void updateKeysByCode() {
        newHash.clearMap();
        for (KeyBinding keybinding : KEYS) newHash.addKey(keybinding.getCode(), keybinding);
    }

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    public void inject$isKeyPressed(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.pressed && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext()));
    }
}
