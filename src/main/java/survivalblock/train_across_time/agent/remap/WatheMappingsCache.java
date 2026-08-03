package survivalblock.train_across_time.agent.remap;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.lib.mappingio.MappedElementKind;
import net.fabricmc.loader.impl.lib.mappingio.MappingVisitor;
import net.fabricmc.loader.impl.lib.mappingio.format.tiny.Tiny2FileReader;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.commons.Remapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WatheMappingsCache {
    public static final Path MAPPINGS_TINY_LOCATION = Paths.get("mappings.tiny");
    public static final WatheMappingsCache INSTANCE;

    public static WatheMappingsCache create() {
        return new Prod();
    }

    public final Map<String, String> classes = new HashMap<>();
    public final Map<String, String> methods = new HashMap<>();
    public final Map<String, String> fields = new HashMap<>();

    public Remapper createRemapper(int api, ClassOutputInfo info) {
        return new Remapper(api) {
            @Override
            public String map(String internalName) {
                if (internalName.startsWith("net/minecraft/class_")) {
                    var newName = classes.get(internalName);

                    if (newName == null) {
                        info.addError("No class mapping for " + internalName);
                    } else {
                        info.markChanged();
                        info.usedMappingsOutput.useClass(internalName);
                        return newName;
                    }
                }

                return super.map(internalName);
            }

            @Override
            public String mapMethodName(String owner, String name, String descriptor) {
                if (name.startsWith("method_")) {
                    var newName = methods.get(name);

                    if (newName == null) {
                        info.addError("No method mapping for " + name);
                    } else {
                        info.markChanged();
                        info.usedMappingsOutput.useMethod(name);
                        return newName;
                    }
                }

                return super.mapMethodName(owner, name, descriptor);
            }

            @Override
            public String mapFieldName(String owner, String name, String descriptor) {
                if (name.startsWith("field_")) {
                    var newName = fields.get(name);

                    if (newName == null) {
                        info.addError("No field mapping for " + name);
                    } else {
                        info.markChanged();
                        info.usedMappingsOutput.useField(name);
                        return newName;
                    }
                }

                return super.mapFieldName(owner, name, descriptor);
            }
        };
    }

    public void load(DataInput in) throws IOException {
        var numClasses = in.readInt();

        for (int i = 0; i < numClasses; i++) {
            classes.put(in.readUTF(), in.readUTF());
        }

        var numMethods = in.readInt();

        for (int i = 0; i < numMethods; i++) {
            methods.put(in.readUTF(), in.readUTF());
        }

        var numFields = in.readInt();

        for (int i = 0; i < numFields; i++) {
            fields.put(in.readUTF(), in.readUTF());
        }
    }

    public void save(DataOutput out) throws IOException {
        out.writeInt(classes.size());

        for (Map.Entry<String, String> entry : classes.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }

        out.writeInt(methods.size());

        for (Map.Entry<String, String> entry : methods.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }

        out.writeInt(fields.size());

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }

    public void clear() {
        classes.clear();
        methods.clear();
        fields.clear();
    }

    public abstract void reload();

    private static class Dev extends WatheMappingsCache {
        public final MappingVisitor MAPPING_VISITOR = new MappingVisitor() {
            public int intermediaryIndex;
            public int namedIndex;

            public MappedElementKind kind;
            public String intermediary;
            public String named;

            public void put(MappedElementKind kind, String value, int index) {
                this.kind = kind;

                if (index == intermediaryIndex) {
                    intermediary = value;
                } else if (index == namedIndex) {
                    named = value;
                }
            }

            public void flush() {
                if (intermediary != null && named != null) {
                    switch (kind) {
                        case CLASS -> classes.put(intermediary, named);
                        case METHOD -> methods.put(intermediary, named);
                        case FIELD -> fields.put(intermediary, named);
                    }

                    intermediary = null;
                    named = null;
                }
            }

            @Override
            public void visitNamespaces(String s, List<String> list) {
                intermediaryIndex = s.equals("intermediary") ? 0 : (list.indexOf("intermediary") + 1);
                namedIndex = s.equals("named") ? 0 : (list.indexOf("named") + 1);
            }

            @Override
            public boolean visitClass(String s) {
                put(MappedElementKind.CLASS, s, 0);
                flush();
                return true;
            }

            @Override
            public boolean visitField(String s, @Nullable String s1) {
                put(MappedElementKind.FIELD, s, 0);
                flush();
                return true;
            }

            @Override
            public boolean visitMethod(String s, @Nullable String s1) {
                put(MappedElementKind.METHOD, s, 0);
                flush();
                return true;
            }

            @Override
            public boolean visitMethodArg(int i, int i1, @Nullable String s) {
                return false;
            }

            @Override
            public boolean visitMethodVar(int i, int i1, int i2, int i3, @Nullable String s) {
                return false;
            }

            @Override
            public void visitDstName(MappedElementKind kind, int i, String s) {
                put(kind, s, i + 1);
                flush();
            }

            @Override
            public void visitComment(MappedElementKind mappedElementKind, String s) {
            }
        };

        @Override
        public void reload() {
            clear();

            try (InputStream in = Files.newInputStream(MAPPINGS_TINY_LOCATION)) {
                Tiny2FileReader.read(new InputStreamReader(in), MAPPING_VISITOR);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            var extra = System.getProperty("train_across_time:extra_mappings");

            if (extra != null) {
                for (String path : extra.split(",")) {
                    try (InputStream in = Files.newInputStream(Paths.get(path))) {
                        load(new DataInputStream(in));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static class Prod extends WatheMappingsCache {
        @Override
        public void reload() {
            clear();

            try (InputStream in = WatheMappingsCache.class.getClassLoader().getResourceAsStream("mappings.bin")) {
                if (in != null) {
                    load(new DataInputStream(in));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            var extra = System.getProperty("train_across_time:extra_mappings");

            if (extra != null) {
                for (String path : extra.split(",")) {
                    try (InputStream in = Files.newInputStream(Paths.get(path))) {
                        load(new DataInputStream(in));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    static {
        boolean tryWrite = FabricLoader.getInstance().isDevelopmentEnvironment() && Files.exists(MAPPINGS_TINY_LOCATION);
        INSTANCE = tryWrite ? new Dev() : new Prod();
        INSTANCE.reload();
    }
}
