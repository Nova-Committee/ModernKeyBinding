package committee.nova.mkb.platform;

import com.blamejared.controlling.platform.IPlatformHelper;
import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.keybinding.KeyModifier;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class MKBPlatformHelper implements IPlatformHelper {
    @Override
    public boolean hasConflictingModifier(KeyBinding keybinding, KeyBinding other) {
        return ((IKeyBinding) keybinding).hasKeyCodeModifierConflict(other);
    }

    @Override
    public void setKey(GameOptions options, KeyBinding keybinding, InputUtil.Key key) {
        ((IKeyBinding) keybinding).setKeyModifierAndCode(KeyModifier.getActiveModifier(), key);
        IPlatformHelper.super.setKey(options, keybinding, key);
    }

    @Override
    public boolean isKeyCodeModifier(InputUtil.Key key) {
        return KeyModifier.isKeyCodeModifier(key);
    }

    @Override
    public void setToDefault(GameOptions options, KeyBinding keybinding) {
        ((IKeyBinding) keybinding).setToDefault();
    }
}
