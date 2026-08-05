package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.game.GameConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.train_across_time.util.TrainAcrossTimeMixinHelper;

import java.util.function.Consumer;

@Mixin(GameConstants.class)
public interface GameConstantsMixin {
    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Util;make(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;"
            )
    )
    private static Object delayShopEntriesInit(Object t, Consumer<Object> consumer, Operation<Object> original) {
        TrainAcrossTimeMixinHelper.SHOP_ENTRIES_INITIALIZER.set(() -> consumer.accept(t));
        return original.call(
                t,
                (Consumer<Object>) _ -> {}
        );
    }
}
