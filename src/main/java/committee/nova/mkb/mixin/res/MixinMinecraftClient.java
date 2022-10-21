package committee.nova.mkb.mixin.res;

import committee.nova.mkb.ModernKeyBinding;
import committee.nova.mkb.res.MKBZipResourcePack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow
    private List<ResourcePack> resourcePacks;

    /*************************
     * @reason very hacky solution to get custom lang file loaded
     * @author piper74
     **************************/

    @Inject(method = "method_5574", at = @At("TAIL"))
    private void method_5574(CallbackInfo ci) {
        MKBZipResourcePack mkbZipResourcePack;
        try {
            String string = ModernKeyBinding.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            mkbZipResourcePack = new MKBZipResourcePack(new File(string));
            this.resourcePacks.add(mkbZipResourcePack);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
