package net.flectone.tale.config.merger;

import net.flectone.tale.config.Config;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for merging {@link Config} configuration objects.
 * <p>
 * This interface defines mapping methods for deep merging plugin configurations,
 * handling nested structures through builder patterns.
 * </p>
 *
 * @author TheFaser
 * @since 1.7.1
 */
@Mapper(config = MapstructMergerConfig.class)
public interface ConfigMerger {

    @Mapping(target = "chat", expression = "java(mergeChat(target.build().chat().toBuilder(), source.chat()))")
    @Mapping(target = "greeting", expression = "java(mergeGreeting(target.build().greeting().toBuilder(), source.greeting()))")
    Config merge(@MappingTarget Config.ConfigBuilder target, Config source);

    Config.Chat mergeChat(@MappingTarget Config.Chat.ChatBuilder target, Config.Chat chat);

    Config.Greeting mergeGreeting(@MappingTarget Config.Greeting.GreetingBuilder target, Config.Greeting greeting);

}