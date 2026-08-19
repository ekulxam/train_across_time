package survivalblock.train_across_time.plugin;

import groovyjarjarasm.asm.Opcodes;
import net.typho.asm_util.ClassTransformInfo;
import org.gradle.api.artifacts.transform.*;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.jspecify.annotations.NonNull;
import org.objectweb.asm.ClassReader;
import survivalblock.train_across_time.common.TATConstants;
import survivalblock.train_across_time.common.WatheTransformer;

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
        var numClasses = 0;

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
                        var entryName = entry.getName();

                        if (entry.getName().endsWith(".class")) {
                            numClasses++;

                            var info = new ClassTransformInfo.AgentTransform(bytes);

                            TRANSFORMER.transform(Opcodes.ASM9, false, info);

                            var transformedBytes = info.compile((name, b) -> TRANSFORMER.debugSaveClass(name, () -> b));

                            if (transformedBytes != null) {
                                if (!modified) {
                                    modified = true;
                                }

                                bytes = transformedBytes;
                            }
                        }

                        out.putNextEntry(new JarEntry(entryName));
                        out.write(bytes);
                        out.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (modified) {
            TATConstants.PLATFORM.info("Transformed jar " + inFile + " has " + numClasses + " class files");
        }
    }

    public static class Parameters implements TransformParameters {
    }
}
