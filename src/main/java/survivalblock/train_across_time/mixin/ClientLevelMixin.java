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

import dev.doctor4t.wathe.index.WatheBlocks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow
    @Final
    @Mutable
    private static Set<Item> MARKER_PARTICLE_ITEMS;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void wathe$addCustomBlockMarkers(CallbackInfo ci) {
        MARKER_PARTICLE_ITEMS = new HashSet<>(MARKER_PARTICLE_ITEMS);
        MARKER_PARTICLE_ITEMS.add(WatheBlocks.BARRIER_PANEL.asItem());
        MARKER_PARTICLE_ITEMS.add(WatheBlocks.LIGHT_BARRIER.asItem());
    }
}
