package survivalblock.train_across_time.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.LibraryElements;
import org.jspecify.annotations.NonNull;

public class TATPlugin implements Plugin<Project> {
    public final Attribute<Boolean> tweakedAttrib = Attribute.of(
            "train_across_time:tweaked",
            Boolean.class
    );

    @Override
    public void apply(@NonNull Project project) {
        project.getDependencies().getAttributesSchema().attribute(tweakedAttrib);
        project.getDependencies().getArtifactTypes().configureEach(artifact -> {
            //var elements = artifact.getAttributes().getAttribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE);

            //if (elements != null && elements.getName().equals(LibraryElements.JAR)) {
                artifact.getAttributes().attribute(tweakedAttrib, false);
            //} else {
            //    artifact.getAttributes().attribute(tweakedAttrib, true);
            //}
        });
        project.getConfigurations().getByName("compileClasspath").getAttributes().attribute(tweakedAttrib, true);
        project.getDependencies().registerTransform(TATTransform.class, transform -> {
            transform.getFrom().attribute(tweakedAttrib, false);
            transform.getTo().attribute(tweakedAttrib, true);
        });
    }
}
