package net.flectone.tale.util.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.config.Config;
import net.flectone.tale.config.merger.ConfigMergerImpl;
import net.flectone.tale.exception.FileLoadException;
import net.flectone.tale.model.file.FilePack;
import net.flectone.tale.util.constant.FilePath;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BinaryOperator;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileLoader {

    private final FileWriter fileWriter;
    private final ObjectMapper yamlMapper;
    private final @Named("projectPath") Path projectPath;
    private final ConfigMergerImpl configMerger;

    @Getter
    private FilePack defaultFiles;

    public void init() {
        if (defaultFiles != null) return;

        Config config = loadFromResource(FilePath.CONFIG.getPath(), Config.class);

        defaultFiles = new FilePack(config);

        fileWriter.save(defaultFiles, true);
    }

    public FilePack loadFiles(FilePack currentFiles) {
        currentFiles = currentFiles == null ? defaultFiles : currentFiles;

        Config config = loadAndMergeConfig(currentFiles);

        return new FilePack(config);
    }

    public Config loadAndMergeConfig(FilePack currentFiles) {
        currentFiles = currentFiles == null ? defaultFiles : currentFiles;
        return loadOrDefault(FilePath.CONFIG.getPath(), currentFiles.config(), (config1, config2) ->
                configMerger.merge(config1.toBuilder(), config2)
        );
    }

    public <T> T loadOrDefault(String path, T defaultFile, BinaryOperator<T> mergeOperator) {
        return loadOrDefault(path, defaultFile, mergeOperator, true);
    }

    public <T> T loadOrDefault(String path, T defaultFile, BinaryOperator<T> mergeOperator, boolean merge) {
        Path pathToFile = Paths.get(projectPath.toString(), path);
        if (!Files.exists(pathToFile)) return defaultFile;
        if (pathToFile.toFile().lastModified() == FileWriter.LAST_MODIFIED_TIME) return defaultFile;

        Optional<T> file = load(pathToFile, defaultFile);
        if (!merge && file.isPresent()) return file.get();

        return file
                .map(localFile -> mergeOperator.apply(defaultFile, localFile))
                .orElse(defaultFile);
    }

    public <T> T loadFromResource(String path, Class<T> type) {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream( "config/" + path)) {
            if (resourceAsStream == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }

            return yamlMapper.readValue(resourceAsStream, type);
        } catch (IOException e) {
            throw new FileLoadException(path, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> load(Path pathToFile, T defaultFile) {
        File file = pathToFile.toFile();

        try {
            return Optional.of((T) yamlMapper.readValue(file, defaultFile.getClass()));
        } catch (Exception e) {
            if (e instanceof MismatchedInputException mismatchedInputException
                    && mismatchedInputException.getMessage() != null
                    && mismatchedInputException.getMessage().contains("No content to map due to end-of-input")) {
                fileWriter.save(pathToFile, defaultFile);
            } else {
                throw new FileLoadException(file.getPath(), e);
            }
        }

        return Optional.empty();
    }

}
