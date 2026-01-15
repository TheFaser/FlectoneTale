package net.flectone.tale.util.file;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.config.*;
import net.flectone.tale.util.constant.FilePath;

import java.nio.file.Path;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FilePathProvider {

    private final @Named("projectPath") Path projectPath;

    public Path get(Object file) {
        return switch (file) {
            case Config ignored -> projectPath.resolve(FilePath.CONFIG.getPath());
            default -> throw new IllegalArgumentException("Incorrect file format: " + file);
        };
    }

}
