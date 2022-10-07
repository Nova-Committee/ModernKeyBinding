package committee.nova.mkb.util;

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

    //public static int getFirstOn(String origin, char target) {
    //    final int length = origin.length();
    //    for (int i = 0; i < length; i++) {
    //        if (target == origin.charAt(i)) return i;
    //    }
    //    return -1;
    //}
}
