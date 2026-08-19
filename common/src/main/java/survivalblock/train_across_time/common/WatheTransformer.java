package survivalblock.train_across_time.common;

import net.typho.asm_util.ClassTransformInfo;
import net.typho.asm_util.remap.CompatClassRemapper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.*;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

/**
 * @author Typho
 */
public class WatheTransformer {
    public Path debugPath = TATConstants.PLATFORM.debugOutputPath();
    public UsedMappingsOutput usedMappingsOutput;
    public WatheMappingsCache mappingsCache = WatheMappingsCache.create();

    public WatheTransformer() {
        if (debugPath != null) {
            TATConstants.PLATFORM.info("Debug path: " + debugPath);
        }

        var mappingsOutputFile = System.getProperty("train_across_time.mappings_output_file");
        usedMappingsOutput = mappingsOutputFile == null ? UsedMappingsOutput.NONE : new UsedMappingsOutput() {
            public final WatheMappingsCache usedMappingsStorage = WatheMappingsCache.createStandalone();
            public final Path outputFile = Paths.get(mappingsOutputFile);

            {
                TATConstants.PLATFORM.info("Mappings output file: " + outputFile);
            }

            @Override
            public void useClass(String intermediary) {
                this.usedMappingsStorage.classes.put(intermediary, mappingsCache.classes.get(intermediary));
            }

            @Override
            public void useMethod(String intermediary) {
                this.usedMappingsStorage.methods.put(intermediary, mappingsCache.methods.get(intermediary));
            }

            @Override
            public void useField(String intermediary) {
                this.usedMappingsStorage.fields.put(intermediary, mappingsCache.fields.get(intermediary));
            }

            @Override
            public void save() {
                try (OutputStream out = Files.newOutputStream(this.outputFile)) {
                    this.usedMappingsStorage.save(new DataOutputStream(out));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mappingsCache.reload(this);
    }

    public void transform(
            int api,
            boolean errorIfUnmapped,
            ClassTransformInfo info
    ) {
        if (!TATConstants.shouldTransformClass(info.getNode().name)) {
            return;
        }

        try {
            var node = new ClassNode();
            info.getNode().accept(
                    new CompatClassRemapper(
                            new CompatClassRemapper(
                                    node,
                                    new WatheRemapper(api, info)
                            ),
                            mappingsCache.createRemapper(api, info, usedMappingsOutput, errorIfUnmapped)
                    )
            );
            info.setNode(node);

            var patch = WatheClassPatches.PATCHES.get(info.getNode().name);

            if (patch != null) {
                info.markChanged();
                patch.accept(info);
            }
        } catch (Throwable t) {
            TATConstants.PLATFORM.error("Error while transforming class " + info.getNode().name, t);
        }
    }

    public void debugSaveClass(
            String className,
            Supplier<byte @Nullable []> bytes
    ) {
        try {
            if (debugPath != null) {
                var b = bytes.get();

                if (b != null) {
                    var path = debugPath.resolve(className + ".class");

                    if (path.toFile().exists()) {
                        TATConstants.PLATFORM.warn("Transformed " + className + " more than once");
                    }

                    var folder = path.getParent().toFile();

                    if (folder.mkdirs() || folder.exists()) {
                        Files.write(path, b);
                    } else {
                        throw new FileNotFoundException("File with path " + path + " could not be written to");
                    }
                }
            }
        } catch (IOException e) {
            TATConstants.PLATFORM.error("Error while writing " + className + " to debug output", e);
        }
    }
}
