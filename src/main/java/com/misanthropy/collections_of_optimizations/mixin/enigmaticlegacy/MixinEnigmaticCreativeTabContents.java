package com.misanthropy.collections_of_optimizations.mixin.enigmaticlegacy;

import com.aizistral.enigmaticlegacy.api.items.ICreativeTabMember;
import com.misanthropy.collections_of_optimizations.CoOConfig;
import com.misanthropy.collections_of_optimizations.core.EnigmaticTabMembers;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.aizistral.enigmaticlegacy.EnigmaticLegacy", remap = false)
public abstract class MixinEnigmaticCreativeTabContents {

    @Inject(method = "onCreativeTabContents", at = @At("HEAD"), cancellable = true, require = 0)
    private void coo$onlyThisTabsMembers(BuildCreativeModeTabContentsEvent event, CallbackInfo ci) {
        if (!CoOConfig.enigmaticlegacyGroupTabMembers) {
            return;
        }
        ci.cancel();
        for (ICreativeTabMember member : EnigmaticTabMembers.of(event.getTab())) {
            member.getCreativeTabStacks().forEach(event::accept);
        }
    }
}
