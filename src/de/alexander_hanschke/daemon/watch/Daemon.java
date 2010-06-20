package de.alexander_hanschke.daemon.watch;


import java.nio.file.Path;
import java.nio.file.StandardWatchEventKind;
import java.nio.file.WatchEvent;
import java.util.Properties;
import java.util.regex.Pattern;


/**
 *
 * @author Alexander Hanschke <dev@alexander-hanschke.de>
 * @since 1.7.0-ea-b98
 */
public class Daemon {

    private static Properties properties = new Properties();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
         handleArguments(args);
         new Daemon().run();
    }

    private static Pattern pattern = Pattern.compile("-([a-zA-Z]+\\.{0,1})+=.+");

    private static void handleArguments(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
        }

        String key, value;
        WatchEvent.Kind eventKind = StandardWatchEventKind.ENTRY_CREATE;
        for (String arg : args) {
            if (pattern.matcher(arg).matches()) {
                key   = arg.split("-|=")[1];
                value = arg.split("=")[1];
            } else {
                continue;
            }
            
            switch(key) {
                case "watch.folder": {
                    properties.put(key, value);
                    break;
                }
                case "watch.event": {
                    switch(value) {
                        case "UPDATE": {
                            eventKind = StandardWatchEventKind.ENTRY_MODIFY;
                            break;
                        }
                        case "DELETE": {
                            eventKind = StandardWatchEventKind.ENTRY_DELETE;
                            break;
                        }
                    }

                    properties.put(key, eventKind);
                }
            }
        }
    }
    
    private static void printUsage() {
        System.out.println("USAGE: java -jar jdk7-watch-service-sample.jar "
                + "-watch.folder=/your/path -watch.event=(CREATE|UPDATE|DELETE)");
    }

    private void run() {
        FolderWatcher watcher = new FolderWatcher(properties);

        watcher.addWatchEventListener(new WatchEventListener() {

            @Override
            public void onWatchEvent(WatchEvent event) {
                System.out.println(((Path) event.context()).toAbsolutePath());
            }

            @Override
            public boolean canHandleWatchEvent(WatchEvent event) {
                return event.kind() == properties.get("watch.event");
            }

        });

        new Thread(watcher).start();
    }

}