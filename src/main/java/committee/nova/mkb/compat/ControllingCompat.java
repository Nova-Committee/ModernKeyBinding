package committee.nova.mkb.compat;

import com.blamejared.controlling.api.event.ControllingEvents;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;

public class ControllingCompat {
    public static void init() {
        ControllingEvents.HAS_CONFLICTING_MODIFIERS_EVENT
                .register(event -> ((IKeyBinding) event.thisMapping()).hasKeyCodeModifierConflict(event.otherMapping()));

        ControllingEvents.SET_KEY_EVENT.register(event -> {
            ((IKeyBinding) event.mapping()).setKeyModifierAndCode(KeyModifier.getActiveModifier(), event.key());
            return false;
        });

        ControllingEvents.IS_KEY_CODE_MODIFIER_EVENT
                .register(event -> KeyModifier.isKeyCodeModifier(event.key()));

        ControllingEvents.SET_TO_DEFAULT_EVENT.register(event -> {
            ((IKeyBinding) event.mapping()).setToDefault();
            return false;
        });
    }
}
