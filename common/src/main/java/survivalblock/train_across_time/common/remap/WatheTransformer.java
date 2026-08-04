package survivalblock.train_across_time.common.remap;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import survivalblock.train_across_time.common.TATConstants;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Typho
 */
public class WatheTransformer {
    // FabricLoader.getInstance().isDevelopmentEnvironment() ? FabricLoader.getInstance().getGameDir().toAbsolutePath().resolve(".wathe_port_debug") : null
    public Path debugPath = TATConstants.PLATFORM.debugOutputPath();
    public ClassOutputInfo.UsedMappingsOutput usedMappingsOutput;
    public WatheMappingsCache mappingsCache = WatheMappingsCache.create();

    public WatheTransformer() {
        var mappingsOutputFile = System.getProperty("train_across_time:mappings_output_file");
        usedMappingsOutput = mappingsOutputFile == null ? ClassOutputInfo.UsedMappingsOutput.NONE : new ClassOutputInfo.UsedMappingsOutput() {
            public final WatheMappingsCache usedMappingsStorage = WatheMappingsCache.createStandalone();
            public final Path outputFile = Paths.get(mappingsOutputFile);

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
            public void endClass() {
                try (OutputStream out = Files.newOutputStream(this.outputFile)) {
                    this.usedMappingsStorage.save(new DataOutputStream(out));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        mappingsCache.reload(this);
    }

    public @Nullable TransformedClass transform(
            int api,
            boolean errorIfUnmapped,
            Consumer<ClassVisitor> visitor
    ) {
        var info = new ClassOutputInfo(usedMappingsOutput);

        try {
            var node = new ClassNode();
            visitor.accept(
                    new ClassNameVisitor(
                            api,
                            new MixinClassRemapper(
                                    new MixinClassRemapper(
                                            node,
                                            new WatheRemapper(api, info)
                                    ),
                                    mappingsCache.createRemapper(api, info, errorIfUnmapped)
                            ),
                            name -> {
                                info.className = name;

                                if (!TATConstants.shouldTransformClass(name)) {
                                    throw new EndClassVisitException();
                                }
                            }
                    )
            );

            var patch = WatheClassPatches.PATCHES.get(info.className);

            if (patch != null) {
                info.markChanged();
                patch.accept(node, info);
            }

            return new TransformedClass(node, info);
        } catch (EndClassVisitException ignored) {
        } catch (Exception e) {
            TATConstants.PLATFORM.error("Error while processing class " + info.className, e);
        }

        return null;
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
