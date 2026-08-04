package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.doctor4t.ratatouille.index.RatatouilleItems")
public interface RatatouilleItemsMixin {
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/fabricmc/loader/api/FabricLoader;isDevelopmentEnvironment()Z"
            )
    )
    private static boolean noArmorItem(boolean original) {
        return false;
    }
}
