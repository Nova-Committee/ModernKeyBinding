package committee.nova.mkb.api;

import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public interface IKeyBinding {
    InputUtil.Key getKey();

    IKeyConflictContext getKeyConflictContext();

    KeyModifier getKeyModifierDefault();

    KeyModifier getKeyModifier();

    void setKeyConflictContext(IKeyConflictContext keyConflictContext);

    void setKeyModifierAndCode(KeyModifier keyModifier, InputUtil.Key keyCode);

    void press();

    default boolean isConflictContextAndModifierActive() {
        return getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    /**
     * Returns true when one of the bindings' key codes conflicts with the other's modifier.
     */
    default boolean hasKeyCodeModifierConflict(KeyBinding other) {
        final IKeyBinding extended = (IKeyBinding) other;
        if (getKeyConflictContext().conflicts(extended.getKeyConflictContext()) || extended.getKeyConflictContext().conflicts(getKeyConflictContext())) {
            return getKeyModifier().matches(extended.getKey()) || extended.getKeyModifier().matches(getKey());
        }
        return false;
    }

    /**
     * Checks that the key conflict context and modifier are active, and that the keyCode matches this binding.
     */
    default boolean isActiveAndMatches(InputUtil.Key keyCode) {
        return keyCode != InputUtil.UNKNOWN_KEY && keyCode.equals(getKey()) && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    default void setToDefault() {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyBinding().getDefaultKey());
    }

    default KeyBinding getKeyBinding() {
        return (KeyBinding) this;
    }
}
