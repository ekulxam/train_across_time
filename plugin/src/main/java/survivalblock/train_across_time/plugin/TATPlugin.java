package survivalblock.train_across_time.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.attributes.Attribute;
import org.jspecify.annotations.NonNull;
import survivalblock.train_across_time.common.TATConstants;

public class TATPlugin implements Plugin<Project> {
    public final Attribute<Boolean> tweakedAttrib = Attribute.of(
            "train_across_time:tweaked",
            Boolean.class
    );

    @Override
    public void apply(@NonNull Project project) {
        project.getDependencies().getAttributesSchema().attribute(tweakedAttrib);
        project.getDependencies().getArtifactTypes().getByName(ArtifactTypeDefinition.JAR_TYPE).getAttributes().attribute(tweakedAttrib, false);

        project.getDependencies().registerTransform(TATTransform.class, transform -> {
            transform.getFrom().attribute(
                    ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                    ArtifactTypeDefinition.JAR_TYPE
            );
            transform.getFrom().attribute(tweakedAttrib, false);

            transform.getTo().attribute(
                    ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                    ArtifactTypeDefinition.JAR_TYPE
            );
            transform.getTo().attribute(tweakedAttrib, true);
        });

        ModuleDependency wathe = (ModuleDependency) project.getDependencies().create("dev.doctor4t:wathe:" + TATConstants.WATHE_VERSION);
        wathe.attributes(attributes -> attributes.attribute(tweakedAttrib, true));
        wathe.setTransitive(false);

        project.getDependencies().add("compileOnly", wathe);

        ModuleDependency ratatouille = (ModuleDependency) project.getDependencies().create("dev.doctor4t:ratatouille:" + TATConstants.RATATOUILLE_VERSION);
        ratatouille.attributes(attributes -> attributes.attribute(tweakedAttrib, true));
        ratatouille.setTransitive(false);

        project.getDependencies().add("compileOnly", ratatouille);
    }
}