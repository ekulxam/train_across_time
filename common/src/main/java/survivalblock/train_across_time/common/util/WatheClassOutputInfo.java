package survivalblock.train_across_time.common.util;

import net.typho.asm_util.ClassOutputInfo;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassWriter;

public class WatheClassOutputInfo extends ClassOutputInfo {
    public final UsedMappingsOutput usedMappingsOutput;

    public WatheClassOutputInfo(UsedMappingsOutput usedMappingsOutput) {
        this.usedMappingsOutput = usedMappingsOutput;
    }

    @Override
    public @Nullable ClassWriter end() {
        var writer = super.end();

        if (writer != null && usedMappingsOutput != null) {
            usedMappingsOutput.endClass();
        }

        return writer;
    }
}
