package survivalblock.train_across_time.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.train_across_time.TrainAcrossTime;

import java.util.function.Consumer;

@Mixin(targets = "dev.doctor4t.wathe.game.GameConstants")
public interface GameConstantsMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_156;method_654(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;"))
    private static Object a(Object o, Consumer<Object> consumer, Operation<Object> original) {
        TrainAcrossTime.SHOP_ENTRIES_INITIALIZER.set(() -> consumer.accept(o));
        return original.call(
                o,
                (Consumer<Object>) o1 -> {}
        );
    }
}
