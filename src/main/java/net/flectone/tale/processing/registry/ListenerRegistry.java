package net.flectone.tale.processing.registry;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.module.message.chat.listener.PlayerChatListener;
import net.flectone.tale.module.message.greeting.listener.PlayerReadyListener;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ListenerRegistry {

    private final JavaPlugin javaPlugin;
    private final Injector injector;

    public void registerAll() {

        PlayerChatListener playerChatListener = injector.getInstance(PlayerChatListener.class);
        javaPlugin.getEventRegistry().registerGlobal(PlayerChatEvent.class, playerChatListener::onPlayerChatEvent);

        PlayerReadyListener playerReadyListener = injector.getInstance(PlayerReadyListener.class);
        javaPlugin.getEventRegistry().registerGlobal(PlayerReadyEvent.class, playerReadyListener::onPlayerReadyEvent);

    }

}
