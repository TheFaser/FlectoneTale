package net.flectone.tale.module.message.chat.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.config.Config;
import net.flectone.tale.processing.formatter.ColorMessageFormatter;
import net.flectone.tale.processing.sender.MessageSender;
import net.flectone.tale.util.DistanceUtil;
import net.flectone.tale.util.file.FileFacade;
import net.flectone.tale.util.logging.FLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerChatListener {

    private final DistanceUtil distanceUtil;
    private final ColorMessageFormatter colorMessageFormatter;
    private final FileFacade fileFacade;
    private final MessageSender messageSender;
    private final FLogger fLogger;

    public void onPlayerChatEvent(PlayerChatEvent event) {
        event.setCancelled(true);

        PlayerRef sender = event.getSender();
        String message = event.getContent();

        Pair<String, Config.Chat.Type> playerChat = getPlayerChat(message);
        if (playerChat == null) {
            messageSender.sendMessage(sender, colorMessageFormatter.format(fileFacade.config().chat().nullChat()));
            return;
        }

        fLogger.info("[CHAT] " + sender.getUsername() + ": " + event.getContent());

        Config.Chat.Type chatType = playerChat.getValue();

        String trigger = chatType.trigger();
        if (!StringUtils.isEmpty(trigger) && message.startsWith(trigger)) {
            message = message.substring(trigger.length()).trim();
        }

        List<PlayerRef> recipients = event.getTargets().stream()
                .filter(recipient -> recipient.equals(sender)
                        || chatType.range() == -1
                        || distanceUtil.distance(event.getSender(), recipient)
                        .filter(distance -> distance <= chatType.range()).isPresent()
                )
                .toList();

        String finalMessage = message;
        recipients.forEach(recipient ->  messageSender.sendMessage(recipient, formatMessage(sender, playerChat, finalMessage)));

        if (recipients.isEmpty() || recipients.size() == 1 && recipients.getFirst().equals(sender)) {

            if (StringUtils.isNotEmpty(chatType.nullReceiver())) {
                messageSender.sendMessage(sender, colorMessageFormatter.format(chatType.nullReceiver()));
            }

        }
    }

    private Message formatMessage(PlayerRef sender, Pair<String, Config.Chat.Type> playerChat, String message) {
        return colorMessageFormatter.format(StringUtils.replaceEach(
                playerChat.getRight().format(),
                new String[]{"<player>", "<message>"},
                new String[]{sender.getUsername(), colorMessageFormatter.stripColors(message)}
        ));
    }

    private Pair<String, Config.Chat.Type> getPlayerChat(String eventMessage) {
        String returnedChatName = null;
        Config.Chat.Type playerChat = null;
        int priority = Integer.MIN_VALUE;

        for (Map.Entry<String, Config.Chat.Type> entry : fileFacade.config().chat().types().entrySet()) {
            Config.Chat.Type chat = entry.getValue();
            String chatName = entry.getKey();

            if (!chat.enable()) continue;
            if (chat.trigger() != null
                    && !chat.trigger().isEmpty()
                    && !eventMessage.startsWith(chat.trigger())) continue;
            if (eventMessage.equals(chat.trigger())) continue;

            if (chat.priority() <= priority) continue;
//            if (!permissionChecker.check(fPlayer, permission().types().get(chatName))) continue;

            playerChat = chat;
            priority = chat.priority();
            returnedChatName = chatName;
        }

        return Pair.of(returnedChatName, playerChat);
    }

}
