package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StickyKeyBinding.class)
public abstract class MixinStickyKeyBinding extends KeyBinding {
    public MixinStickyKeyBinding(String translationKey, int code, String category) {
        super(translationKey, code, category);
    }

    @Redirect(method = "setPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setPressed(Z)V", ordinal = 0))
    private void redirect$setPressed(KeyBinding instance, boolean pressed) {
        if (!((IKeyBinding) this).isConflictContextAndModifierActive()) return;
        super.setPressed(!isPressed());
    }


}
