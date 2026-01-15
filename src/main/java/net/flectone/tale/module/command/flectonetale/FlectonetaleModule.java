package net.flectone.tale.module.command.flectonetale;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import net.flectone.tale.FlectoneTaleAPI;
import net.flectone.tale.exception.ReloadException;
import net.flectone.tale.util.logging.FLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@Singleton
public class FlectonetaleModule extends AbstractCommand {

    private final FlectoneTaleAPI flectoneTaleAPI;
    private final FLogger fLogger;

    @Inject
    public FlectonetaleModule(FlectoneTaleAPI flectoneTaleAPI,
                              FLogger fLogger) {
        super("flectonetale", "FlectoneTale reload command, only executed in console");

        this.flectoneTaleAPI = flectoneTaleAPI;
        this.fLogger = fLogger;
    }

    @Override
    protected @Nullable CompletableFuture<Void> execute(@NotNull CommandContext commandContext) {
        if (commandContext.isPlayer()) return CompletableFuture.completedFuture(null);

        try {
            flectoneTaleAPI.reload();
        } catch (ReloadException e) {
            fLogger.warn(e);
        }

        return CompletableFuture.completedFuture(null);
    }

}
