package committee.nova.mkb.mixin;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(KeyBinding.class)
public interface AccessorKeyBinding {
    @Accessor("keybindArray")
    static List<KeyBinding> getKeybindArray() {
        throw new AssertionError();
    }

    @Accessor
    void setPressed(boolean pressed);
}
