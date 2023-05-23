package committee.nova.mkb.mixin;

import committee.nova.mkb.api.IKeyBinding;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen {
    @Shadow
    protected abstract boolean handleHotbarKeyPressed(int keyCode, int scanCode);

    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Shadow
    public abstract void close();

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;matchesMouse(I)Z"))
    public boolean redirect$mouseClicked(KeyBinding instance, int code) {
        return ((IKeyBinding) instance).isActiveAndMatches(InputUtil.Type.MOUSE.createFromCode(code));
    }

    @Redirect(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;matchesMouse(I)Z"))
    public boolean redirect$mouseReleased(KeyBinding instance, int code) {
        return ((IKeyBinding) instance).isActiveAndMatches(InputUtil.Type.MOUSE.createFromCode(code));
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void inject$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        final InputUtil.Key mouseKey = InputUtil.fromKeyCode(keyCode, scanCode);
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
            return;
        }
        if (client != null && ((IKeyBinding) client.options.inventoryKey).isActiveAndMatches(mouseKey)) {
            this.close();
            cir.setReturnValue(true);
            return;
        }
        boolean handled = this.handleHotbarKeyPressed(keyCode, scanCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (((IKeyBinding) this.client.options.pickItemKey).isActiveAndMatches(mouseKey)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
                handled = true;
            } else if (((IKeyBinding) this.client.options.dropKey).isActiveAndMatches(mouseKey)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, HandledScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
                handled = true;
            }
        } else if (((IKeyBinding) this.client.options.dropKey).isActiveAndMatches(mouseKey)) {
            handled = true;
            // From Forge MC-146650: Emulate MC bug, so we don't drop from hotbar when pressing drop without hovering over a item.
        }
        cir.setReturnValue(handled);
    }

    @Redirect(method = "handleHotbarKeyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;matchesKey(II)Z"))
    public boolean redirect$handleHotbarKeyPressed(KeyBinding instance, int keyCode, int scanCode) {
        return ((IKeyBinding) instance).isActiveAndMatches(InputUtil.fromKeyCode(keyCode, scanCode));
    }
}
