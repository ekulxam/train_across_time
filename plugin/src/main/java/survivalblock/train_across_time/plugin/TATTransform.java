package survivalblock.train_across_time.plugin;

import groovyjarjarasm.asm.Opcodes;
import org.gradle.api.artifacts.transform.*;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.ClassReader;
import survivalblock.train_across_time.common.TATConstants;
import survivalblock.train_across_time.common.remap.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

@CacheableTransform
public abstract class TATTransform implements TransformAction<TATTransform.Parameters> {
    @Classpath
    @InputArtifact
    public abstract Provider<FileSystemLocation> getInput();

    public static final WatheTransformer TRANSFORMER = new WatheTransformer();

    @Override
    public void transform(@NonNull TransformOutputs outputs) {
        var inFile = getInput().get().getAsFile();
        var outFile = outputs.file(inFile.getName().substring(0, inFile.getName().lastIndexOf('.')) + "-tat-tweaked.jar");
        var modified = false;

        try (JarFile jar = new JarFile(inFile, false)) {
            try (JarOutputStream out = new JarOutputStream(new FileOutputStream(outFile))) {
                var iterator = jar.entries();

                while (iterator.hasMoreElements()) {
                    var entry = iterator.nextElement();

                    if (entry.getName().startsWith("META-INF/") && (entry.getName().endsWith(".SF") || entry.getName().endsWith(".RSA") || entry.getName().endsWith(".DSA"))) {
                        continue;
                    }

                    try (InputStream in = jar.getInputStream(entry)) {
                        var bytes = in.readAllBytes();
                        var newBytes = bytes;

                        if (entry.getName().endsWith(".class")) {
                            var transformed = TRANSFORMER.transform(Opcodes.ASM9, false, visitor -> {
                                new ClassReader(bytes).accept(visitor, 0);
                            });

                            if (transformed != null) {
                                newBytes = transformed.toByteArray();

                                if (newBytes == null) {
                                    newBytes = bytes;
                                } else {
                                    if (!modified) {
                                        modified = true;
                                        TATConstants.PLATFORM.info("Transforming jar " + inFile);
                                    }
                                }
                            }
                        }

                        out.putNextEntry(new JarEntry(entry));
                        out.write(newBytes);
                        out.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Parameters implements TransformParameters {
    }
}
