package committee.nova.mkb.mixin;

import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyBindingMap;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IKeyBinding {
    @Shadow
    public abstract int getKeyCode();

    @Shadow
    private int keyCode;

    @Shadow
    public abstract int getKeyCodeDefault();

    @Mutable
    @Shadow
    @Final
    private String keyDescription;

    @Mutable
    @Shadow
    @Final
    private int keyCodeDefault;

    @Mutable
    @Shadow
    @Final
    private String keyCategory;

    @Shadow
    @Final
    private static Set<String> keybindSet;

    @Shadow
    @Final
    private static List<KeyBinding> keybindArray;


    @Shadow
    private boolean pressed;
    //Backporting missing starts:
    private static final KeyBindingMap newHash = new KeyBindingMap();
    KeyModifier keyModifierDefault = KeyModifier.NONE;
    KeyModifier keyModifier = KeyModifier.NONE;
    IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;

    /**
     * Convenience constructor for creating KeyBindings with keyConflictContext set.
     */
    public MixinKeyBinding(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
        this(description, keyConflictContext, KeyModifier.NONE, keyCode, category);
    }

    /**
     * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
     */
    public MixinKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        this.keyConflictContext = keyConflictContext;
        this.keyModifier = keyModifier;
        this.keyModifierDefault = keyModifier;
        if (this.keyModifier.matches(keyCode)) {
            this.keyModifier = KeyModifier.NONE;
        }
        //todo: is this correct?
        keybindArray.add((KeyBinding) (Object) this);
        //todo
        newHash.addKey(keyCode, (KeyBinding) (Object) this);
        keybindSet.add(category);
    }

    @Override
    public boolean isActiveAndMatches(int keyCode) {
        return keyCode != 0 && keyCode == this.getKeyCode() && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    @Override
    public void setKeyConflictContext(IKeyConflictContext keyConflictContext) {
        this.keyConflictContext = keyConflictContext;
    }

    @Override
    public IKeyConflictContext getKeyConflictContext() {
        return keyConflictContext;
    }

    @Override
    public KeyModifier getKeyModifierDefault() {
        return keyModifierDefault;
    }

    @Override
    public KeyModifier getKeyModifier() {
        return keyModifier;
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, int keyCode) {
        this.keyCode = keyCode;
        if (keyModifier.matches(keyCode)) {
            keyModifier = KeyModifier.NONE;
        }
        newHash.removeKey((KeyBinding) (Object) this);
        this.keyModifier = keyModifier;
        newHash.addKey(keyCode, (KeyBinding) (Object) this);
        //todo
    }

    @Override
    public void setToDefault() {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyCodeDefault());
    }

    @Override
    public boolean isSetToDefaultValue() {
        return getKeyCode() == getKeyCodeDefault() && getKeyModifier() == getKeyModifierDefault();
    }

    @Override
    public boolean conflicts(KeyBinding other) {
        final IKeyBinding keyBinding = (IKeyBinding) other;
        if (getKeyConflictContext().conflicts(keyBinding.getKeyConflictContext()) || keyBinding.getKeyConflictContext().conflicts(getKeyConflictContext())) {
            final KeyModifier keyModifier = getKeyModifier();
            final KeyModifier otherKeyModifier = keyBinding.getKeyModifier();
            if (keyModifier.matches(other.getKeyCode()) || otherKeyModifier.matches(getKeyCode())) return true;
            if (getKeyCode() == other.getKeyCode()) {
                return keyModifier == otherKeyModifier ||
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
                && (getKeyModifier().matches(other.getKeyCode()) || keyBinding.getKeyModifier().matches(getKeyCode()));
    }

    @Override
    public String getDisplayName() {
        return getKeyModifier().getLocalizedComboName(getKeyCode());
    }

    //Modifying existing starts:
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(String description, int keyCode, String category, CallbackInfo ci) {
        newHash.addKey(keyCode, (KeyBinding) (Object) this);
    }

    @Redirect(method = "onTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IntHashMap;lookup(I)Ljava/lang/Object;"))
    private static Object redirect$onTick(IntHashMap m, int i) {
        return newHash.lookupActive(i);
    }

    @Redirect(method = "setKeyBindState", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/IntHashMap;lookup(I)Ljava/lang/Object;"))
    private static Object redirect$setKeyBindState(IntHashMap m, int i) {
        return newHash.lookupAll(i);
    }

    /**
     * @author Tapio
     * @reason Use the new HASH
     */
    @Overwrite
    public static void resetKeyBindingArrayAndHash() {
        newHash.clearMap();
        for (KeyBinding keybinding : keybindArray) newHash.addKey(keybinding.getKeyCode(), keybinding);
    }

    @Inject(method = "getIsKeyPressed", at = @At("RETURN"), cancellable = true)
    public void onIsKeyPressed(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.pressed && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext()));
    }
}
