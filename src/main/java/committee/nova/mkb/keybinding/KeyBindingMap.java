package committee.nova.mkb.keybinding;

import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KeyBindingMap {
    private static final EnumMap<KeyModifier, Map<InputUtil.Key, Collection<KeyBinding>>> map = new EnumMap<>(KeyModifier.class);

    static {
        for (KeyModifier modifier : KeyModifier.values()) {
            map.put(modifier, new HashMap<>());
        }
    }

    @Nullable
    public KeyBinding lookupActive(InputUtil.Key keyCode) {
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            KeyBinding binding = getBinding(keyCode, activeModifier);
            if (binding != null) {
                return binding;
            }
        }
        return getBinding(keyCode, KeyModifier.NONE);
    }

    @Nullable
    private KeyBinding getBinding(InputUtil.Key keyCode, KeyModifier keyModifier) {
        Collection<KeyBinding> bindings = map.get(keyModifier).get(keyCode);
        if (bindings == null) return null;
        for (KeyBinding binding : bindings) if (((IKeyBinding) binding).isActiveAndMatches(keyCode)) return binding;
        return null;
    }

    public List<KeyBinding> lookupAll(InputUtil.Key keyCode) {
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        for (Map<InputUtil.Key, Collection<KeyBinding>> bindingsMap : map.values()) {
            Collection<KeyBinding> bindings = bindingsMap.get(keyCode);
            if (bindings != null) {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(InputUtil.Key keyCode, KeyBinding keyBinding) {
        KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        Map<InputUtil.Key, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.computeIfAbsent(keyCode, k -> new ArrayList<>());
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyBinding keyBinding) {
        final IKeyBinding extended = (IKeyBinding) keyBinding;
        KeyModifier keyModifier = extended.getKeyModifier();
        InputUtil.Key keyCode = extended.getKey();
        Map<InputUtil.Key, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty()) {
                bindingsMap.remove(keyCode);
            }
        }
    }

    public void clearMap() {
        for (Map<InputUtil.Key, Collection<KeyBinding>> bindings : map.values()) {
            bindings.clear();
        }
    }
}
