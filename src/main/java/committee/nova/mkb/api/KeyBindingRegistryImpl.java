package committee.nova.mkb.api;

import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.keybinding.KeyModifier;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

@SuppressWarnings("unused")
class KeyBindingRegistryImpl implements KeyBindingRegistry {
    @Override
    public KeyBinding createKeyBinding(String description, IKeyConflictContext ctx, KeyModifier modifier, int keyCode, String category) {
        final KeyBinding keyBinding = new KeyBinding(description, keyCode, category);
        final IKeyBinding mixined = (IKeyBinding) keyBinding;
        mixined.setKeyConflictContext(ctx);
        mixined.setInitialKeyModifierAndCode(modifier, keyCode);
        return (KeyBinding) mixined;
    }

    @Override
    public void registerKeyBinding(String description, IKeyConflictContext ctx, KeyModifier modifier, int keyCode, String category) {
        ClientRegistry.registerKeyBinding(createKeyBinding(description, ctx, modifier, keyCode, category));
    }
}
