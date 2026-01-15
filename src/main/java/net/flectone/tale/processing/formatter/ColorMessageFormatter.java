package net.flectone.tale.processing.formatter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hypixel.hytale.server.core.Message;
import lombok.RequiredArgsConstructor;
import net.flectone.tale.util.logging.FLogger;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ColorMessageFormatter {

    private static final Pattern COLOR_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    private record MessageSegment(
            String text,
            Color color
    ) {}

    private final FLogger fLogger;

    public Message format(String input) {
        if (StringUtils.isEmpty(input)) return Message.empty();

        List<MessageSegment> segments = parseSegments(input);
        if (segments.isEmpty()) return Message.empty();

        Message result = Message.empty();
        segments.stream()
                .map(this::createMessage)
                .forEach(result::insert);

        return result;
    }

    private List<MessageSegment> parseSegments(String input) {
        List<MessageSegment> segments = new ArrayList<>();
        List<MatchResult> matches = COLOR_PATTERN.matcher(input).results().toList();

        if (matches.isEmpty()) {
            segments.add(new MessageSegment(input, null));
            return segments;
        }

        int lastIndex = 0;

        for (MatchResult match : matches) {
            String beforeColor = input.substring(lastIndex, match.start());
            if (StringUtils.isNotEmpty(beforeColor)) {
                segments.add(new MessageSegment(beforeColor, null));
            }

            String hex = match.group();
            Color color = parseHexColor(hex);
            lastIndex = match.end();

            int nextColorStart = matches.stream()
                    .filter(m -> m.start() > match.start())
                    .findFirst()
                    .map(MatchResult::start)
                    .orElse(input.length());

            String coloredText = input.substring(lastIndex, nextColorStart);
            if (StringUtils.isNotEmpty(coloredText)) {
                segments.add(new MessageSegment(coloredText, color));
            }

            lastIndex = nextColorStart;
        }

        if (lastIndex < input.length()) {
            String remaining = input.substring(lastIndex);
            segments.add(new MessageSegment(remaining, null));
        }

        return segments;
    }

    private Message createMessage(MessageSegment segment) {
        Message message = Message.raw(segment.text());
        if (segment.color() != null) {
            message.color(segment.color());
        }

        return message;
    }

    private Color parseHexColor(String hex) {
        try {
            String cleanHex = hex.startsWith("#") ? hex.substring(1) : hex;

            return switch (cleanHex.length()) {
                case 6 -> {
                    int rgb = Integer.parseInt(cleanHex, 16);
                    yield new Color(rgb);
                }
                case 8 -> {
                    long argb = Long.parseLong(cleanHex, 16);
                    int alpha = (int) ((argb >> 24) & 0xFF);
                    int red = (int) ((argb >> 16) & 0xFF);
                    int green = (int) ((argb >> 8) & 0xFF);
                    int blue = (int) (argb & 0xFF);
                    yield new Color(red, green, blue, alpha);
                }
                default -> null;
            };
        } catch (NumberFormatException e) {
            fLogger.warn(e);

            return null;
        }
    }

    public List<Message> parseToMessages(String input) {
        return parseSegments(input).stream()
                .map(this::createMessage)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public String stripColors(String input) {
        return COLOR_PATTERN.matcher(input).replaceAll("");
    }

}
