package de.alexander_hanschke.daemon.watch;

import java.io.IOException;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Alexander Hanschke <dev@alexander-hanschke.de>
 * @since 1.7.0-ea-b98
 */
public class FolderWatcher implements Runnable {

    private Properties properties;

    private WatchKey watchKey;

    private WatchService watchService;    

    private final List<WatchEventListener> listeners = new ArrayList<>();

    public FolderWatcher(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void run() {
        System.out.println(
                String.format("daemon started - watching folder %s for changes of kind %s",
                properties.getProperty("watch.folder"), properties.get("watch.event")));

        try {
            watchService = FileSystems.getDefault().newWatchService();
            watchKey = Paths.get(properties.getProperty("watch.folder")).register(watchService,
                    (WatchEvent.Kind) properties.get("watch.event"));
        } catch (IOException e) {
            throw new RuntimeException("init failed!", e);
        }

        while (true) {
            try {
                watchKey = watchService.take();

                for (WatchEvent watchEvent : watchKey.pollEvents()) {
                    handleWatchEvent(watchEvent);
                }

                watchKey.reset();
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupt!", e);
            }
        }
    }

    public void addWatchEventListener(WatchEventListener watchEventListener) {
        synchronized(listeners) {
            listeners.add(watchEventListener);
        }
    }

    private void handleWatchEvent(WatchEvent event) {
        List<WatchEventListener> currentListeners;

        synchronized(listeners) {
            currentListeners = new ArrayList<>(listeners);
        }

        for (WatchEventListener listener : currentListeners) {
            if (listener.canHandleWatchEvent(event)) {
                listener.onWatchEvent(event);
            }
        }
    }

}