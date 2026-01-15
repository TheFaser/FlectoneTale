package net.flectone.tale.processing.sender;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessageSender {

    public void sendMessage(Player player, Message message) {
        player.sendMessage(message);
    }

    public void sendMessage(PlayerRef player, Message message) {
        player.sendMessage(message);
    }

}
