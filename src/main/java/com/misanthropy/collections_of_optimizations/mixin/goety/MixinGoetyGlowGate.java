package com.misanthropy.collections_of_optimizations.mixin.goety;

import com.Polarice3.Goety.client.events.ClientEvents;
import com.Polarice3.Goety.common.blocks.entities.OminousIdolBlockEntity;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.magic.CommandFocus;
import com.Polarice3.Goety.common.items.magic.CommandHorn;
import com.Polarice3.Goety.common.items.magic.OrderFocus;
import com.Polarice3.Goety.common.items.magic.TransferScroll;
import com.Polarice3.Goety.utils.WandUtil;
import com.misanthropy.collections_of_optimizations.core.ClientTickStamp;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MixinGoetyGlowGate {

    @Unique
    private static int coo$stamp = Integer.MIN_VALUE;
    @Unique
    private static LocalPlayer coo$player;
    @Unique
    private static boolean coo$active;
    @Unique
    private static Item coo$focus;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void coo$goetyGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = (Minecraft) (Object) this;
        LocalPlayer player = minecraft.player;
        if (player == null || !(entity instanceof LivingEntity living)) {
            return;
        }
        int tick = ClientTickStamp.current();
        if (tick != coo$stamp || player != coo$player) {
            coo$stamp = tick;
            coo$player = player;
            coo$focus = WandUtil.findFocus(player).getItem();
            coo$active = player.hasEffect(GoetyEffects.TREMOR_SENSE.get())
                    || coo$focus instanceof CommandFocus
                    || coo$focus instanceof OrderFocus
                    || player.isHolding(ModItems.TRANSFER_SCROLL.get())
                    || player.isHolding(ModItems.COMMAND_HORN.get())
                    || player.isCrouching()
                    || player.isShiftKeyDown()
                    || ClientEvents.target != null;
        }
        if (!coo$active) {
            return;
        }

        if (player.hasEffect(GoetyEffects.TREMOR_SENSE.get()) && player.onGround() && living.onGround()) {
            MobEffectInstance instance = player.getEffect(GoetyEffects.TREMOR_SENSE.get());
            if (instance != null) {
                float amp = ((float) instance.getAmplifier() + 1.0F) / 2.0F;
                if (player.distanceTo(living) <= 32.0F * amp) {
                    cir.setReturnValue(true);
                }
            }
        }

        if (coo$focus instanceof CommandFocus) {
            LivingEntity servant = CommandFocus.getServantClient(player.level(), WandUtil.findFocus(player));
            if (servant != null && servant == living) {
                cir.setReturnValue(true);
            }
        } else if (coo$focus instanceof OrderFocus) {
            List<LivingEntity> list = OrderFocus.getServantsClient(player.level(), WandUtil.findFocus(player));
            if (!list.isEmpty() && list.contains(living)) {
                cir.setReturnValue(true);
            }
        }

        if (player.isHolding(ModItems.TRANSFER_SCROLL.get())) {
            ItemStack stack = player.getMainHandItem().is(ModItems.TRANSFER_SCROLL.get()) ? player.getMainHandItem() : player.getOffhandItem();
            if (TransferScroll.hasSummon(stack) && living == TransferScroll.getSummon(stack)) {
                cir.setReturnValue(true);
            }
        }

        if (player.isHolding(ModItems.COMMAND_HORN.get()) && CommandHorn.getEntities(player.level(), player).contains(living)) {
            cir.setReturnValue(true);
        }

        if (entity instanceof RaiderServant servant
                && servant.canLinkToIdol()
                && (player.isCrouching() || player.isShiftKeyDown())
                && minecraft.level != null
                && minecraft.hitResult instanceof BlockHitResult hit
                && minecraft.level.getBlockEntity(hit.getBlockPos()) instanceof OminousIdolBlockEntity idol
                && idol.getIds().contains(servant.getId())) {
            cir.setReturnValue(true);
        }

        if (ClientEvents.target == entity) {
            cir.setReturnValue(true);
        }
    }
}
