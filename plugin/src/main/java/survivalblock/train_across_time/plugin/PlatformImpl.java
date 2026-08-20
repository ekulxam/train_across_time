package survivalblock.train_across_time.plugin;

import org.jetbrains.annotations.Nullable;
import survivalblock.train_across_time.common.TATConstants;

import java.nio.file.Path;

public class PlatformImpl implements TATConstants.Platform {
    @Override
    public @Nullable Path debugOutputPath() {
        return null;
    }

    @Override
    public boolean compileMappings() {
        return false;
    }

    @Override
    public void info(String s) {
        System.out.println("[Train Across Time] " + s);
    }

    @Override
    public void warn(String s) {
        System.out.println("[Train Across Time] " + s);
    }

    @Override
    public void error(String s, Throwable t) {
        System.err.println("[Train Across Time] " + s);
        t.printStackTrace(System.err);
    }
}
