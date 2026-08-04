package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.index.WatheDataComponentTypes;
import dev.doctor4t.wathe.item.CocktailItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
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
import survivalblock.train_across_time.util.TrainAcrossTimeMixinHelper;

import java.lang.reflect.Field;
import java.util.UUID;

import static survivalblock.train_across_time.TheTrainAcrossTimeConstants.logError;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
    @SuppressWarnings("unchecked")
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
        if (!level.isClientSide()) {
            DataComponentType<String> poisonerDataComponent = null;
            Tuple<Integer, Integer> clampTime = null;

            try {
                Field clampTimeField = PlayerPoisonComponent.class.getField("clampTime");
                clampTime = (Tuple<Integer, Integer>) clampTimeField.get(null);
            } catch (ReflectiveOperationException | ClassCastException e) {
                if (!TrainAcrossTimeMixinHelper.warnOnMissingPoisonBounds) {
                    TrainAcrossTimeMixinHelper.warnOnMissingPoisonBounds = true;
                    logError("Could not access PlayerPoisonComponent.clampTime! This will only log once!", e);
                }
            }

            try {
                Field poisonerField = WatheDataComponentTypes.class.getField("POISONER");
                poisonerDataComponent = (DataComponentType<String>) poisonerField.get(null);
            } catch (ReflectiveOperationException | ClassCastException e) {
                if (!TrainAcrossTimeMixinHelper.warnOnMissingPoisonDataComponent) {
                    TrainAcrossTimeMixinHelper.warnOnMissingPoisonDataComponent = true;
                    logError("Could not access WatheDataComponentTypes.POISONER! This will only log once!", e);
                }
            }

            if (poisonerDataComponent != null && clampTime != null) {
                String poisoner = stack.get(poisonerDataComponent);

                if (poisoner != null) {
                    PlayerPoisonComponent poisonComponent = PlayerPoisonComponent.KEY.get(player);
                    int poisonTicks = poisonComponent.poisonTicks;

                    int updated;

                    if (poisonTicks == -1) {
                        updated = level.getRandom().nextIntBetweenInclusive(clampTime.getA(), clampTime.getB());
                    } else {
                        updated = Mth.clamp(poisonTicks - level.getRandom().nextIntBetweenInclusive(100, 300), 0, clampTime.getB());
                    }

                    poisonComponent.setPoisonTicks(updated, UUID.fromString(poisoner));
                }
            }
        }

        //noinspection ConstantValue
        if (!(((Object) stack.getItem()) instanceof CocktailItem)) {
            PlayerMoodComponent.KEY.get(player).eatFood();
        }
    }
}
