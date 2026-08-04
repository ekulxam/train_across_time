package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
    @Inject(
            method = "onConsume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void onConsume(
            Level level,
            LivingEntity user,
            ItemStack stack,
            Consumable consumable,
            CallbackInfo ci,
            @Local Player player
    ) {
        // TODO
        /*
        if (!level.isClientSide()) {
            String poisoner = stack.getOrDefault(WatheDataComponentTypes.POISONER, null);

            if (poisoner != null) {
                int poisonTicks = PlayerPoisonComponent.KEY.get(player).poisonTicks;

                if (poisonTicks == -1) {
                    PlayerPoisonComponent.KEY.get(player).setPoisonTicks(level.getRandom().nextIntBetweenInclusive(PlayerPoisonComponent.clampTime.getA(), PlayerPoisonComponent.clampTime.getB()), UUID.fromString(poisoner));
                } else {
                    PlayerPoisonComponent.KEY.get(player).setPoisonTicks(Mth.clamp(poisonTicks - level.getRandom().nextIntBetweenInclusive(100, 300), 0, PlayerPoisonComponent.clampTime.getB()), UUID.fromString(poisoner));
                }
            }
        }

        if (!(stack.getItem() instanceof CocktailItem)) {
            PlayerMoodComponent.KEY.get(player).eatFood();
        }
         */
    }
}
