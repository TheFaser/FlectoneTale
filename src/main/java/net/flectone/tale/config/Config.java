package net.flectone.tale.config;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Builder;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@With
@Builder(toBuilder = true)
@Jacksonized
public record Config(

        @JsonPropertyDescription(" Don't change it if you don't know what it is")
        String version,

        Chat chat,

        Greeting greeting

) {

    @With
    @Builder(toBuilder = true)
    @Jacksonized
    public record Chat(
            String nullChat,
            Map<String, Type> types
    ) {

        @With
        @Builder(toBuilder = true)
        @Jacksonized
        public record Type(
                Boolean enable,
                Integer range,
                Integer priority,
                String trigger,
                String nullReceiver,
                String format
        ) {
        }
    }

    @With
    @Builder(toBuilder = true)
    @Jacksonized
    public record Greeting(
            String format
    ) {
    }
}