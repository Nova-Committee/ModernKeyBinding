package committee.nova.mkb.keybinding;

import net.minecraft.client.settings.KeyBinding;

public interface IKeyBinding {
    // Should have more mixins?
    boolean isActiveAndMatches(int keyCode);

    void setKeyConflictContext(IKeyConflictContext keyConflictContext);

    IKeyConflictContext getKeyConflictContext();

    KeyModifier getKeyModifierDefault();

    KeyModifier getKeyModifier();

    void setKeyModifierAndCode(KeyModifier keyModifier, int keyCode);

    void setToDefault();

    boolean isSetToDefaultValue();

    boolean conflicts(KeyBinding other);

    boolean hasKeyCodeModifierConflict(KeyBinding other);

    String getDisplayName();
}
