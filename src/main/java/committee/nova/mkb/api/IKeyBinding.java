package committee.nova.mkb.api;

import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.settings.KeyBinding;

public interface IKeyBinding {
    boolean isActiveAndMatches(int keyCode);

    void setKeyConflictContext(IKeyConflictContext keyConflictContext);

    IKeyConflictContext getKeyConflictContext();

    KeyModifier getKeyModifierDefault();

    KeyModifier getKeyModifier();

    void setKeyModifierAndCode(KeyModifier keyModifier, int keyCode);

    void setInitialKeyModifierAndCode(KeyModifier keyModifier, int keyCode);

    void setToDefault();

    boolean isSetToDefaultValue();

    boolean conflicts(KeyBinding other);

    boolean hasKeyCodeModifierConflict(KeyBinding other);

    String getDisplayName();
}
