package net.flectone.tale;

import com.alessiodp.libby.LibraryManager;
import com.alessiodp.libby.StandaloneLibraryManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import net.flectone.tale.exception.ReloadException;
import net.flectone.tale.processing.resolver.LibraryResolver;
import net.flectone.tale.util.logging.FLogger;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class FlectoneTaleImpl extends JavaPlugin implements FlectoneTale {

    private final Path projectPath;

    private Injector injector;

    public FlectoneTaleImpl(@Nonnull JavaPluginInit init) {
        super(init);

        projectPath = init.getFile().getParent().toAbsolutePath().resolve("FlectoneTale");
    }

    @Override
    protected void setup() {
        FLogger fLogger = new FLogger(this.getLogger());

        LibraryManager libraryManager = new StandaloneLibraryManager(fLogger, projectPath, "libraries");
        LibraryResolver libraryResolver = new LibraryResolver(libraryManager);
        libraryResolver.addLibraries();
        libraryResolver.resolveRepositories();
        libraryResolver.loadLibraries();

        injector = Guice.createInjector(new FlectoneInjector(fLogger, projectPath, libraryManager, libraryResolver, this, this));

        onEnable();
    }

    @Override
    public <T> T get(Class<T> type) {
        if (injector == null) {
            throw new IllegalStateException("FlectoneTale not initialized yet");
        }

        return injector.getInstance(type);
    }

    @Override
    public boolean isReady() {
        return injector != null;
    }

    @Override
    public void onEnable() {
        if (!isReady()) return;

        injector.getInstance(FlectoneTaleAPI.class).onEnable();
    }

    @Override
    public void onDisable() {
        if (!isReady()) return;

        injector.getInstance(FlectoneTaleAPI.class).onDisable();
    }

    @Override
    public void reload() throws ReloadException {
        if (!isReady()) return;

        injector.getInstance(FlectoneTaleAPI.class).reload();
    }
}
