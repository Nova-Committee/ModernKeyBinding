package committee.nova.mkb.api;

import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.settings.KeyBinding;

public interface KeyBindingRegistry {
    public static final KeyBindingRegistry INSTANCE = new KeyBindingRegistryImpl();

    KeyBinding createKeyBinding(String description, IKeyConflictContext ctx, KeyModifier modifier, int keyCode, String category);

    KeyBinding registerKeyBinding(String description, IKeyConflictContext ctx, KeyModifier modifier, int keyCode, String category);
}
