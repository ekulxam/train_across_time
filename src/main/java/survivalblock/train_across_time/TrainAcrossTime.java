package survivalblock.train_across_time;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;

import java.util.Optional;
import java.util.UUID;

public class TrainAcrossTime implements ModInitializer {
    // see WatheClassPatches for PlayerBodyEntity
    @SuppressWarnings("unused")
    public static final EntityDataSerializer<Optional<UUID>> OPTIONAL_UUID = EntityDataSerializer.forValueType(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC));

    @Override
    public void onInitialize() {
    }
}
