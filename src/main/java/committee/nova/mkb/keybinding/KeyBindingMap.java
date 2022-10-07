package committee.nova.mkb.keybinding;

import net.minecraft.client.settings.KeyBinding;

import javax.annotation.Nullable;
import java.util.*;

public class KeyBindingMap {
    private static final EnumMap<KeyModifier, HashMap<Integer, Collection<KeyBinding>>> map = new java.util.EnumMap<>(KeyModifier.class);

    static {
        for (KeyModifier modifier : KeyModifier.values()) {
            map.put(modifier, new HashMap<>());
        }

    }

    @Nullable
    public KeyBinding lookupActive(int keyCode) {
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
    private KeyBinding getBinding(int keyCode, KeyModifier keyModifier) {
        Collection<KeyBinding> bindings = map.get(keyModifier).get(keyCode);
        if (bindings != null) {
            for (KeyBinding binding : bindings) {
                if (((IKeyBinding) binding).isActiveAndMatches(keyCode)) {
                    return binding;
                }
            }
        }
        return null;
    }

    public List<KeyBinding> lookupAll(int keyCode) {
        List<KeyBinding> matchingBindings = new ArrayList<KeyBinding>();
        for (HashMap<Integer, Collection<KeyBinding>> bindingsMap : map.values()) {
            Collection<KeyBinding> bindings = bindingsMap.get(keyCode);
            if (bindings != null) {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(int keyCode, KeyBinding keyBinding) {
        KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        HashMap<Integer, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.computeIfAbsent(keyCode, k -> new ArrayList<>());
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyBinding keyBinding) {
        KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        int keyCode = keyBinding.getKeyCode();
        HashMap<Integer, Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.get(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty()) {
                bindingsMap.remove(keyCode);
            }
        }
    }

    public void clearMap() {
        for (HashMap<Integer, Collection<KeyBinding>> bindings : map.values()) {
            bindings.clear();
        }
    }
}
