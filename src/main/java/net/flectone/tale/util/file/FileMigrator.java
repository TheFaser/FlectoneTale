package net.flectone.tale.util.file;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FileMigrator {

    private final Provider<FileLoader> fileLoaderProvider;

}
