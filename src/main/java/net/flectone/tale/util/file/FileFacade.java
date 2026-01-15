package net.flectone.tale.util.file;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.BuildConfig;
import net.flectone.tale.config.*;
import net.flectone.tale.model.file.FilePack;
import net.flectone.tale.util.comparator.VersionComparator;

import java.io.IOException;
import java.util.function.UnaryOperator;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileFacade {

    private final FileLoader fileLoader;
    private final FileWriter fileWriter;
    private final FileMigrator fileMigrator;
    private final FilePathProvider filePathProvider;
    private final VersionComparator versionComparator;

    @Getter
    private String preInitVersion;
    private FilePack files;

    public void reload() throws IOException {
        fileLoader.init();

        preInitVersion = fileLoader.loadAndMergeConfig(files).version();
        boolean versionChanged = !preInitVersion.equals(BuildConfig.PROJECT_VERSION);

        // backup if version changed
        if (versionChanged) {
            backupFiles(preInitVersion);
        }

        // load local files
        updateFiles();

        // migrate if version changed
        if (versionChanged) {
            migrateFiles(preInitVersion);
        }

        saveFiles();

        // fix migration problems
        if (versionChanged) {
            updateFiles();
        }
    }

    public Config config() {
        return files.config();
    }

    public void saveFiles() {
        fileWriter.save(files, false);
    }

    public void updateFiles() {
        files = fileLoader.loadFiles(files);
    }

    public void updateFilePack(UnaryOperator<FilePack> filePackOperator) {
        files = filePackOperator.apply(files);
    }

    private void backupFiles(String preInitVersion) {

    }

    private void migrateFiles(String preInitVersion) {
        files = files.withConfig(files.config().withVersion(BuildConfig.PROJECT_VERSION));
    }
}
