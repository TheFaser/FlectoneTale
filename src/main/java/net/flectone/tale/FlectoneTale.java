package net.flectone.tale;

import net.flectone.tale.exception.ReloadException;

public interface FlectoneTale {

    <T> T get(Class<T> type);

    boolean isReady();

    void onEnable();

    void onDisable();

    void reload() throws ReloadException;

}