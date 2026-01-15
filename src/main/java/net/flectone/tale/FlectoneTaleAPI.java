package net.flectone.tale;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;
import net.flectone.tale.exception.ReloadException;
import net.flectone.tale.processing.registry.CommandRegistry;
import net.flectone.tale.processing.registry.ListenerRegistry;
import net.flectone.tale.util.file.FileFacade;
import net.flectone.tale.util.logging.FLogger;

@Singleton
public class FlectoneTaleAPI {

    @Getter
    private static FlectoneTale instance;

    @Inject
    public FlectoneTaleAPI(FlectoneTale instance) {
        FlectoneTaleAPI.instance = instance;
    }

    @SneakyThrows
    public void onEnable() {
        if (!instance.isReady()) return;

        FLogger fLogger = instance.get(FLogger.class);
        fLogger.logEnabling();

        FileFacade fileFacade = instance.get(FileFacade.class);
        fileFacade.reload();

        instance.get(ListenerRegistry.class).registerAll();
        instance.get(CommandRegistry.class).registerAll();

        // log plugin enabled
        fLogger.logEnabled();
    }

    public void onDisable() {
        if (!instance.isReady()) return;

        FLogger fLogger = instance.get(FLogger.class);

        // log plugin disabling
        fLogger.logDisabling();

        // log plugin disabled
        fLogger.logDisabled();
    }

    public void reload() throws ReloadException {
        if (!instance.isReady()) return;

        ReloadException reloadException = null;

        FLogger fLogger = instance.get(FLogger.class);

        // log plugin reloading
        fLogger.logReloading();

        // get file resolver for configuration
        FileFacade fileFacade = instance.get(FileFacade.class);

        try {
            // reload configuration files
            fileFacade.reload();
        } catch (Exception e) {
            reloadException = new ReloadException(e);
        }

        // log plugin reloaded
        fLogger.logReloaded();

        // throw reload exception if occurred
        if (reloadException != null) {
            throw reloadException;
        }
    }
}
