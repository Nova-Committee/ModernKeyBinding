package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.api.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyBindingMap;
import committee.nova.mkb.keybinding.KeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IKeyBinding {
    @Shadow
    private InputUtil.Key boundKey;

    @Shadow
    private int timesPressed;
    @Shadow
    @Final
    private static Map<String, KeyBinding> keysById;
    @Shadow
    private boolean pressed;
    @Shadow
    @Final
    private InputUtil.Key defaultKey;
    private static final KeyBindingMap MAP = new KeyBindingMap();

    private KeyModifier keyModifierDefault;
    private KeyModifier keyModifier;
    private IKeyConflictContext keyConflictContext;

    @Override
    public InputUtil.Key getKey() {
        return boundKey;
    }

    @Override
    public IKeyConflictContext getKeyConflictContext() {
        return keyConflictContext == null ? KeyConflictContext.UNIVERSAL : keyConflictContext;
    }

    @Override
    public KeyModifier getKeyModifierDefault() {
        return keyModifierDefault == null ? KeyModifier.NONE : keyModifierDefault;
    }

    @Override
    public KeyModifier getKeyModifier() {
        return keyModifier == null ? KeyModifier.NONE : keyModifier;
    }

    @Override
    public void setKeyConflictContext(IKeyConflictContext keyConflictContext) {
        this.keyConflictContext = keyConflictContext;
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, InputUtil.Key keyCode) {
        this.boundKey = keyCode;
        if (keyModifier.matches(keyCode))
            keyModifier = KeyModifier.NONE;
        MAP.removeKey((KeyBinding) (Object) this);
        this.keyModifier = keyModifier;
        MAP.addKey(keyCode, (KeyBinding) (Object) this);
    }

    @Override
    public void press() {
        ++timesPressed;
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("RETURN"))
    public void inject$init(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        MAP.addKey(this.boundKey, (KeyBinding) (Object) this);
    }

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void inject$onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        ci.cancel();
        KeyBinding keyBinding = MAP.lookupActive(key);
        if (keyBinding == null) return;
        ((IKeyBinding) keyBinding).press();
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void inject$setKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        ci.cancel();
        KeyBinding keyBinding = MAP.lookupActive(key);
        if (keyBinding == null) return;
        keyBinding.setPressed(pressed);
    }

    @Inject(method = "updateKeysByCode", at = @At("HEAD"), cancellable = true)
    private static void updateKeysByCode(CallbackInfo ci) {
        ci.cancel();
        MAP.clearMap();
        for (KeyBinding keybinding : keysById.values()) MAP.addKey(((IKeyBinding) keybinding).getKey(), keybinding);
    }

    @Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
    private void inject$isPressed(CallbackInfoReturnable<Boolean> cir) {
        if (((KeyBinding) (Object) this) instanceof StickyKeyBinding) {
            final StickyKeyBinding sticky = (StickyKeyBinding) (Object) this;
            cir.setReturnValue(pressed && (isConflictContextAndModifierActive() || ((AccessorStickyKeyBinding) sticky).getToggleGetter().getAsBoolean()));
            return;
        }
        cir.setReturnValue(pressed && isConflictContextAndModifierActive());
    }

    @Inject(method = "isDefault", at = @At("RETURN"), cancellable = true)
    private void inject$isDefault(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(boundKey.equals(defaultKey) && getKeyModifier() == getKeyModifierDefault());
    }

    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void inject$equals(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
        final IKeyBinding extended = (IKeyBinding) other;
        if (!getKeyConflictContext().conflicts(extended.getKeyConflictContext()) && !extended.getKeyConflictContext().conflicts(getKeyConflictContext()))
            return;
        KeyModifier keyModifier = getKeyModifier();
        KeyModifier otherKeyModifier = extended.getKeyModifier();
        if (keyModifier.matches(extended.getKey()) || otherKeyModifier.matches(getKey())) {
            cir.setReturnValue(true);
        } else if (getKey().equals(extended.getKey())) {
            // IN_GAME key contexts have a conflict when at least one modifier is NONE.
            // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
            // GUI and other key contexts do not have this limitation.
            cir.setReturnValue(keyModifier == otherKeyModifier ||
                    (getKeyConflictContext().conflicts(KeyConflictContext.IN_GAME) &&
                            (keyModifier == KeyModifier.NONE || otherKeyModifier == KeyModifier.NONE)));
        }
    }

    @Inject(method = "getBoundKeyLocalizedText", at = @At("RETURN"), cancellable = true)
    private void inject$getBoundKeyLocalizedText(CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(getKeyModifier().getCombinedName(boundKey, () -> this.boundKey.getLocalizedText()));
    }
}
