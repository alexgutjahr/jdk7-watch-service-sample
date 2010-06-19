package de.alexander_hanschke.daemon.watch;

import java.nio.file.WatchEvent;

/**
 *
 * @author Alexander Hanschke <dev@alexander-hanschke.de>
 * @since 1.7.0-ea-b98
 */
public interface WatchEventListener {

    /**
     *
     * @param event the {@link WatchEvent}
     */
    public void onWatchEvent(WatchEvent event);

    /**
     *
     * @param event the {@link WatchEvent} under discussion
     * @return {@code true} if the listener can handle the provided {@code event},
     * {@code false} otherwise
     */
    public boolean canHandleWatchEvent(WatchEvent event);

}
