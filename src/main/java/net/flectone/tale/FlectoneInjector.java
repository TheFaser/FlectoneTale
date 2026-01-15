package net.flectone.tale;

import com.alessiodp.libby.LibraryManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import lombok.SneakyThrows;
import net.flectone.tale.processing.resolver.LibraryResolver;
import net.flectone.tale.util.logging.FLogger;
import org.snakeyaml.engine.v2.api.LoadSettings;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.*;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlectoneInjector extends AbstractModule {

    private final FLogger fLogger;
    private final Path projectPath;
    private final LibraryManager libraryManager;
    private final LibraryResolver libraryResolver;
    private final JavaPlugin javaPlugin;
    private final FlectoneTale flectoneTale;

    public FlectoneInjector(FLogger fLogger,
                            Path projectPath,
                            LibraryManager libraryManager,
                            LibraryResolver libraryResolver,
                            JavaPlugin javaPlugin,
                            FlectoneTale flectoneTale) {
        this.fLogger = fLogger;
        this.projectPath = projectPath;
        this.libraryManager = libraryManager;
        this.libraryResolver = libraryResolver;
        this.javaPlugin = javaPlugin;
        this.flectoneTale = flectoneTale;
    }

    @SneakyThrows
    @Override
    protected void configure() {
        bind(Path.class).toInstance(projectPath);
        bind(Path.class).annotatedWith(Names.named("projectPath")).toInstance(projectPath);
        bind(Path.class).annotatedWith(Names.named("backupPath")).toInstance(projectPath.resolve("backups"));

        bind(FLogger.class).toInstance(fLogger);
        bind(JavaPlugin.class).toInstance(javaPlugin);
        bind(FlectoneTale.class).toInstance(flectoneTale);

        bind(LibraryManager.class).toInstance(libraryManager);
        bind(LibraryResolver.class).toInstance(libraryResolver);
        bind(ObjectMapper.class).toInstance(createMapper());
    }

    private ObjectMapper createMapper() {
        return YAMLMapper.builder(
                        YAMLFactory.builder()
                                .loadSettings(LoadSettings.builder()
                                        .setBufferSize(8192) // increase string limit
                                        .setAllowDuplicateKeys(true) // fix duplicate keys
                                        .build()
                                )
                                .build()
                )
                // mapper
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY) // disable auto sorting
                .disable(MapperFeature.DETECT_PARAMETER_NAMES) // [databind#5314]
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS) // fix enum names
                .enable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS) // fix custom classes deserialization
                // deserialization
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES) // jackson 2.x value
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS) // jackson 2.x value
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY) // convert single value to array
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) // fix empty null string
                // serialization
                .enable(SerializationFeature.INDENT_OUTPUT) // indent output for values
                .disable(YAMLWriteFeature.SPLIT_LINES) // fix split long values
                .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER) // fix header
                .disable(YAMLWriteFeature.USE_NATIVE_TYPE_ID) // fix type id like !!java.util.Hashmap
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                // enum
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING) // jackson 2.x value
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING) // jackson 2.x value
                // fix nulls
                .changeDefaultPropertyInclusion(config -> JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL)) // show only non-null values
                .changeDefaultNullHandling(config -> JsonSetter.Value.forValueNulls(Nulls.SKIP)) // skip null values deserialization
                .withConfigOverride(String.class, o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))) // fix null string
                .withConfigOverride(Collection.class, o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))) // fix null collection
                .withConfigOverride(List.class, o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))) // fix null list
                .withConfigOverride(Set.class, o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))) // fix null set
                .withConfigOverride(Map.class, o -> o.setNullHandling(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))) // fix null map
                .addModule(new SimpleModule().addDeserializer(String.class, new ValueDeserializer<>() {
                    // fix null values like "key: null"
                    // idk, why withConfigOverride(String.class, ...) doesn't fix it

                    @Override
                    public String deserialize(JsonParser p, DeserializationContext ctxt) {
                        return p.currentToken() == JsonToken.VALUE_NULL ? "" : p.getString();
                    }

                    @Override
                    public String getNullValue(DeserializationContext ctxt) {
                        return "";
                    }

                }))
                .build();
    }

}
