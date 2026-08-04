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
