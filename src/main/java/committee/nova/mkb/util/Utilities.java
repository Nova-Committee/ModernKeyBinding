package committee.nova.mkb.util;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.IKeyConflictContext;
import committee.nova.mkb.mixin.AccessorKeyBinding;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import static net.minecraft.client.settings.KeyBinding.setKeyBindState;

public class Utilities {
    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static void updateKeyBindState() {
        for (KeyBinding keybinding : AccessorKeyBinding.getKeybindArray()) {
            try {
                setKeyBindState(keybinding.getKeyCode(), keybinding.getKeyCode() < 256 && Keyboard.isKeyDown(keybinding.getKeyCode()));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }
    }

    public static boolean isActiveIgnoreKeyCode(KeyBinding binding) {
        final IKeyBinding mixined = (IKeyBinding) binding;
        final IKeyConflictContext ctx = mixined.getKeyConflictContext();
        return ctx.isActive() && mixined.getKeyModifier().isActive(ctx);
    }
}
