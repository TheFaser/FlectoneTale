package net.flectone.tale.module.message.greeting.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.processing.formatter.ColorMessageFormatter;
import net.flectone.tale.processing.sender.MessageSender;
import net.flectone.tale.util.file.FileFacade;
import net.flectone.tale.util.logging.FLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerReadyListener {

    private final ColorMessageFormatter colorMessageFormatter;
    private final FileFacade fileFacade;
    private final MessageSender messageSender;
    private final FLogger fLogger;

    public void onPlayerReadyEvent(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        fLogger.info("[GREETING] " + event.getPlayer().getDisplayName());

        String format = fileFacade.config().greeting().format();
        if (StringUtils.isEmpty(format)) return;

        messageSender.sendMessage(event.getPlayer(),
                colorMessageFormatter.format(Strings.CS.replace(format, "<player>", player.getDisplayName()))
        );
    }

}
