package net.flectone.tale.util.logging;

import com.alessiodp.libby.logging.LogLevel;
import com.alessiodp.libby.logging.adapters.LogAdapter;
import com.hypixel.hytale.logger.HytaleLogger;
import net.flectone.tale.BuildConfig;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.logging.Level;

public record FLogger(HytaleLogger hytaleLogger) implements LogAdapter {

    public void warn(String string) {
        hytaleLogger.at(Level.WARNING).log(string);
    }

    public void warn(Throwable throwable) {
        warn("", throwable);
    }

    public void warn(String string, Throwable throwable) {
        hytaleLogger.at(Level.WARNING).log(string, throwable);
    }

    public void info(String string) {
        hytaleLogger.at(Level.INFO).log(string);
    }

    @Override
    public void log(@NonNull LogLevel logLevel, @Nullable String s) {
        hytaleLogger.at(Level.parse(logLevel.name())).log(s);
    }

    @Override
    public void log(@NonNull LogLevel logLevel, @Nullable String s, @Nullable Throwable throwable) {
        hytaleLogger.at(Level.parse(logLevel.name())).log(s, throwable);
    }

    public void logEnabling() {
        info("Enabling...");
    }

    public void logEnabled() {
        info("FlectoneTale v" + BuildConfig.PROJECT_VERSION + " enabled");
    }

    public void logDisabling() {
        info("Disabling...");
    }

    public void logDisabled() {
        info("FlectoneTale v" + BuildConfig.PROJECT_VERSION + " disabled");
    }

    public void logReloading() {
        info("Reloading...");
    }

    public void logReloaded() {
        info("FlectoneTale v" + BuildConfig.PROJECT_VERSION + " reloaded");
    }

}
