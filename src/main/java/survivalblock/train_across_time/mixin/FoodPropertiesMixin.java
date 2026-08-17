/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.index.WatheDataComponentTypes;
import dev.doctor4t.wathe.item.CocktailItem;
import net.minecraft.util.Mth;
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

import java.util.UUID;

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
        if (!level.isClientSide()) {
            String poisoner = stack.get(WatheDataComponentTypes.POISONER);

            if (poisoner != null) {
                PlayerPoisonComponent component = PlayerPoisonComponent.KEY.get(player);
                int poisonTicks = component.poisonTicks;

                if (poisonTicks == -1) {
                    component.setPoisonTicks(level.getRandom().nextIntBetweenInclusive(PlayerPoisonComponent.clampTime.getA(), PlayerPoisonComponent.clampTime.getB()), UUID.fromString(poisoner));
                } else {
                    component.setPoisonTicks(Mth.clamp(poisonTicks - level.getRandom().nextIntBetweenInclusive(100, 300), 0, PlayerPoisonComponent.clampTime.getB()), UUID.fromString(poisoner));
                }
            }
        }

        if (!(stack.getItem() instanceof CocktailItem)) {
            PlayerMoodComponent.KEY.get(player).eatFood();
        }
    }
}
