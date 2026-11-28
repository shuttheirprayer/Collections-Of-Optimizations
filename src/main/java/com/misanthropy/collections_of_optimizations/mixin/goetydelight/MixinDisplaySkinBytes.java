package com.misanthropy.collections_of_optimizations.mixin.goetydelight;

import com.misanthropy.collections_of_optimizations.CoOConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Pseudo
@Mixin(targets = "net.v_black_cat.goetydelight.entities.display_entity.DisplayEntityRender", remap = false)
public abstract class MixinDisplaySkinBytes {

    @Unique
    private static final Map<String, byte[]> coo$skinBytes = new ConcurrentHashMap<>();

    @Redirect(method = "isValidSkinSize", at = @At(value = "INVOKE", target = "Ljava/net/URL;openStream()Ljava/io/InputStream;"), require = 0)
    private static InputStream coo$keepSkinBytes(URL url) throws IOException {
        if (!CoOConfig.goetydelightReuseSkinDownload) {
            return url.openStream();
        }
        byte[] bytes;
        try (InputStream in = url.openStream()) {
            bytes = in.readAllBytes();
        }
        coo$skinBytes.put(url.toString(), bytes);
        return new ByteArrayInputStream(bytes);
    }

    @Redirect(method = "registerDynamicSkin", at = @At(value = "INVOKE", target = "Ljava/net/URL;openStream()Ljava/io/InputStream;"), require = 0)
    private static InputStream coo$reuseSkinBytes(URL url) throws IOException {
        byte[] bytes = coo$skinBytes.remove(url.toString());
        return bytes != null ? new ByteArrayInputStream(bytes) : url.openStream();
    }
}
