package committee.nova.mkb.res;

import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Supplier;

public class MKBZipResourcePack extends ZipResourcePack {
    public MKBZipResourcePack(File file) {
        super(file);
    }

    @Override
    public String getName() {
        return "ModernKeyBinding Language Files";
    }

    @Override
    public BufferedImage getIcon() {
        // This is likely useless
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(DefaultResourcePack.class.getResourceAsStream("/" + (new Identifier("pack.png")).getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufferedImage;
    }

    public static Supplier<ResourcePack> getSupplier(File file) {
        return () -> new MKBZipResourcePack(file);
    }
}
