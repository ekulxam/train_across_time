package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @ModifyReturnValue(
            method = "getNameTag",
            at = @At("RETURN")
    )
    private @Nullable Component getNameTag(
            @Nullable Component original,
            @Local(argsOnly = true) Entity entity
    ) {
        if (original != null) {
            if (WatheClient.gameComponent.isRole(entity.getUUID(), WatheRoles.KILLER)) {
                return original.copy().withColor(-65536);
            }
        }

        return original;
    }
}
