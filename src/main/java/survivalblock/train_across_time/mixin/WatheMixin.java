package survivalblock.train_across_time.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.train_across_time.TrainAcrossTime;

@Mixin(targets = "dev.doctor4t.wathe.Wathe")
public class WatheMixin {
    @Inject(method = "onInitialize", at = @At("RETURN"))
    private void initShopEntriesAfterRegistration(CallbackInfo ci) {
        TrainAcrossTime.SHOP_ENTRIES_INITIALIZER.run();
    }
}
