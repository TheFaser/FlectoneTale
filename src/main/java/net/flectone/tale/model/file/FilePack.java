package net.flectone.tale.model.file;

import lombok.With;
import net.flectone.tale.config.Config;

@With
public record FilePack(
        Config config
) {
}
