## ModernKeyBinding

### Import as a gradle dependency

Check the lastest version on [JitPack](https://jitpack.io/#Nova-Committee/ModernKeyBinding "JitPack").

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    ...
    implementation 'com.github.Nova-Committee:ModernKeyBinding:Forge-1.7.10-1.1.0'
}
```

### Register a keybinding

Register a keybinding that:

- Activates when Alt and C are pressed;

- Only available in GUI.

ClientProxy.java
```java
public void init(final FMLInitializationEvent event) {
    ...
    yourKeyBinding = KeyBindingRegistry.INSTANCE.registerKeyBinding("key.exampleKey", KeyConflictContext.GUI, KeyModifier.ALT, Keyboard.KEY_C, "key.categories.example");
}
```

### Change the properties of an existing keybinding

ClientProxy.java
```java
public void postInit(final FMLPostInitializationEvent event) {
    final IKeyBinding extended = (IKeyBinding) yourKeyBinding;
    // Change the key's keyCode and modifier, as their default values.
    extended.setInitialKeyModifierAndCode(KeyModifier.ALT, Keyboard.KEY_E);
    // Change the key's keyCode and modifier.
    extended.setKeyModifierAndCode(KeyModifier.ALT, Keyboard.KEY_E);
    // Change the key's conflict context.
    extended.setKeyConflictContext(KeyConflictContext.IN_GAME);
}
```
