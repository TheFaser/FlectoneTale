package net.flectone.tale.processing.registry;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.module.command.flectonetale.FlectonetaleModule;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CommandRegistry {

    private final JavaPlugin javaPlugin;
    private final Injector injector;

    public void registerAll() {

        FlectonetaleModule flectonetaleModule = injector.getInstance(FlectonetaleModule.class);
        javaPlugin.getCommandRegistry().registerCommand(flectonetaleModule);

    }

}