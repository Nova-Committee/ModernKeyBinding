## ModernKeyBinding

### Import as a gradle dependency

Check the latest version on [Nova Maven Repository](https://maven.nova-committee.cn/#/releases/committee/nova/mkb).

```groovy
repositories {
    //...
    maven {
        url "https://maven.nova-committee.cn/releases"
    }
}
```

#### Forge
```groovy
dependencies {
    //...
    implementation "committee.nova.mkb.forge:mkb-${mc_version}:${mod_version}" {
        transitive = false
    }
}
```

#### Fabric
```groovy
dependencies {
    //...
    modImplementation "committee.nova.mkb.fabric:mkb-${mc_version}:${mod_version}" {
        transitive = false
    }
}
```

### Register a keybinding

Register a keybinding that:

- Activates when Alt and C are pressed;

- Only available in GUI.

ClientProxy.java

```java
public class ClientProxy {
    public void init(final FMLInitializationEvent event) {
        //...
        yourKeyBinding = KeyBindingRegistry.INSTANCE.registerKeyBinding("key.exampleKey", KeyConflictContext.GUI, KeyModifier.ALT, Keyboard.KEY_C, "key.categories.example");
    }
}
```

### Change the properties of an existing keybinding

ClientProxy.java

```java
public class ClientProxy {
    //...
    public void postInit(final FMLPostInitializationEvent event) {
        final IKeyBinding extended = (IKeyBinding) yourKeyBinding;
        // Change the key's keyCode and modifier, as their default values.
        extended.setInitialKeyModifierAndCode(KeyModifier.ALT, Keyboard.KEY_E);
        // Change the key's keyCode and modifier.
        extended.setKeyModifierAndCode(KeyModifier.ALT, Keyboard.KEY_E);
        // Change the key's conflict context.
        extended.setKeyConflictContext(KeyConflictContext.IN_GAME);
    }
}
```
