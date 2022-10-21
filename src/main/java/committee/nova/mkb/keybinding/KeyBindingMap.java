package committee.nova.mkb.keybinding;

import committee.nova.mkb.api.IKeyBinding;
import committee.nova.mkb.util.IntHashMap;
import net.minecraft.client.options.KeyBinding;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KeyBindingMap {
    private static final EnumMap<KeyModifier, IntHashMap<Collection<KeyBinding>>> map = new EnumMap<>(KeyModifier.class);

    static {
        for (KeyModifier modifier : KeyModifier.values()) map.put(modifier, new IntHashMap<>());
    }

    @Nullable
    public KeyBinding lookupActive(int keyCode) {
        final KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            final KeyBinding binding = getBinding(keyCode, activeModifier);
            if (binding != null) return binding;
        }
        return getBinding(keyCode, KeyModifier.NONE);
    }


    public Set<KeyBinding> lookupActives(int keyCode) {
        final KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            final Set<KeyBinding> bindings = getBindings(keyCode, activeModifier);
            if (!bindings.isEmpty()) return bindings;
        }
        return getBindings(keyCode, KeyModifier.NONE);
    }


    @Nullable
    private KeyBinding getBinding(int keyCode, KeyModifier keyModifier) {
        final Collection<KeyBinding> bindings = map.get(keyModifier).lookup(keyCode);
        if (bindings == null) return null;
        for (final KeyBinding binding : bindings) {
            if (((IKeyBinding) binding).isActiveAndMatches(keyCode)) {
                return binding;
            }
        }
        return null;
    }

    public Set<KeyBinding> getBindings(int keyCode, KeyModifier keyModifier) {
        final Collection<KeyBinding> bindings = map.get(keyModifier).lookup(keyCode);
        final Set<KeyBinding> matched = new HashSet<>();
        if (bindings == null) return matched;
        for (final KeyBinding binding : bindings)
            if (((IKeyBinding) binding).isActiveAndMatches(keyCode)) matched.add(binding);
        return matched;
    }

    public List<KeyBinding> lookupAll(int keyCode) {
        final List<KeyBinding> matchingBindings = new ArrayList<>();
        for (final IntHashMap<Collection<KeyBinding>> bindingsMap : map.values()) {
            final Collection<KeyBinding> bindings = bindingsMap.lookup(keyCode);
            if (bindings != null) {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public void addKey(int keyCode, KeyBinding keyBinding) {
        final KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        final IntHashMap<Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        Collection<KeyBinding> bindingsForKey = bindingsMap.lookup(keyCode);
        if (bindingsForKey == null) {
            bindingsForKey = new ArrayList<>();
            bindingsMap.addKey(keyCode, bindingsForKey);
        }
        bindingsForKey.add(keyBinding);
    }

    public void removeKey(KeyBinding keyBinding) {
        final KeyModifier keyModifier = ((IKeyBinding) keyBinding).getKeyModifier();
        final int keyCode = keyBinding.getCode();
        final IntHashMap<Collection<KeyBinding>> bindingsMap = map.get(keyModifier);
        final Collection<KeyBinding> bindingsForKey = bindingsMap.lookup(keyCode);
        if (bindingsForKey != null) {
            bindingsForKey.remove(keyBinding);
            if (bindingsForKey.isEmpty()) bindingsMap.removeObject(keyCode);
        }
    }

    public void clearMap() {
        for (final IntHashMap<Collection<KeyBinding>> bindings : map.values()) bindings.clearMap();
    }
}
