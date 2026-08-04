package survivalblock.train_across_time.agent;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.jetbrains.annotations.Nullable;
import survivalblock.train_across_time.common.TATConstants;

import java.nio.file.Path;

public class PlatformImpl implements TATConstants.Platform {
    public final LogCategory LOGGER = LogCategory.create("Train Across Time");

    @Override
    public @Nullable Path debugOutputPath() {
        return FabricLoader.getInstance().isDevelopmentEnvironment() ? FabricLoader.getInstance().getGameDir().toAbsolutePath().resolve(".wathe_port_debug") : null;
    }

    @Override
    public boolean compileMappings() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void info(String s) {
        Log.info(LOGGER, s);
    }

    @Override
    public void error(String s, Throwable throwable) {
        Log.error(LOGGER, s, throwable);
    }
}
