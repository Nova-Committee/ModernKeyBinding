package committee.nova.mkb.keybinding;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import static committee.nova.mkb.util.Utilities.isAltKeyDown;

public enum KeyModifier {
    CONTROL {
        @Override
        public boolean matches(int keyCode) {
            if (MinecraftClient.IS_MAC) {
                return keyCode == Keyboard.KEY_LMETA || keyCode == Keyboard.KEY_RMETA;
            } else {
                return keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL;
            }
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.hasControlDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameOptions.getFormattedNameForKeyCode(keyCode);
            String localizationFormatKey = MinecraftClient.IS_MAC ? "controls.mkb.control.mac" : "controls.mkb.control";
            return I18n.translate(localizationFormatKey, keyName);
        }
    },
    SHIFT {
        @Override
        public boolean matches(int keyCode) {
            return keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.hasShiftDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameOptions.getFormattedNameForKeyCode(keyCode);
            return I18n.translate("controls.mkb.shift", keyName);
        }
    },
    ALT {
        @Override
        public boolean matches(int keyCode) {
            return keyCode == Keyboard.KEY_LMENU || keyCode == Keyboard.KEY_RMENU;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return isAltKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameOptions.getFormattedNameForKeyCode(keyCode);
            return I18n.translate("controls.mkb.alt", keyName);
        }
    },
    NONE {
        @Override
        public boolean matches(int keyCode) {
            return false;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME)) {
                for (KeyModifier keyModifier : MODIFIER_VALUES) {
                    if (keyModifier.isActive(conflictContext)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            return GameOptions.getFormattedNameForKeyCode(keyCode);
        }
    };

    public static final KeyModifier[] MODIFIER_VALUES = {SHIFT, CONTROL, ALT};

    public static KeyModifier getActiveModifier() {
        for (KeyModifier keyModifier : MODIFIER_VALUES) {
            if (keyModifier.isActive(null)) {
                return keyModifier;
            }
        }
        return NONE;
    }

    public static boolean isKeyCodeModifier(int keyCode) {
        for (KeyModifier keyModifier : MODIFIER_VALUES) {
            if (keyModifier.matches(keyCode)) {
                return true;
            }
        }
        return false;
    }

    public static KeyModifier valueFromString(String stringValue) {
        try {
            return valueOf(stringValue);
        } catch (NullPointerException | IllegalArgumentException ignored) {
            return NONE;
        }
    }

    public abstract boolean matches(int keyCode);

    public abstract boolean isActive(@Nullable IKeyConflictContext conflictContext);

    public abstract String getLocalizedComboName(int keyCode);
}
