package survivalblock.train_across_time.mixin.nbt;

import dev.doctor4t.wathe.entity.NoteEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteEntity.class)
public abstract class NoteEntityMixin extends Entity {
    @Shadow
    @Final
    private static EntityDataAccessor<Integer> DIRECTION;

    @Shadow
    @Final
    private static EntityDataAccessor<String> LINE1;

    @Shadow
    @Final
    private static EntityDataAccessor<String> LINE2;

    @Shadow
    @Final
    private static EntityDataAccessor<String> LINE3;

    @Shadow
    @Final
    private static EntityDataAccessor<String> LINE4;

    public NoteEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readData(ValueInput input, CallbackInfo ci) {
        input.getInt("Direction").ifPresent(integer -> this.entityData.set(DIRECTION, integer));
        input.getString("Line1").ifPresent(string -> this.entityData.set(LINE1, string));
        input.getString("Line2").ifPresent(string -> this.entityData.set(LINE2, string));
        input.getString("Line3").ifPresent(string -> this.entityData.set(LINE3, string));
        input.getString("Line4").ifPresent(string -> this.entityData.set(LINE4, string));
    }
}
