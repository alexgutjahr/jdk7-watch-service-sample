package de.alexander_hanschke.daemon.watch;


import java.nio.file.StandardWatchEventKind;
import java.nio.file.WatchEvent;
import java.util.Properties;
import javax.swing.JOptionPane;


/**
 *
 * @author Alexander Hanschke <dev@alexander-hanschke.de>
 * @since 1.7.0-ea-b98
 */
public class Daemon {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        
        properties.setProperty("watch.folder",
                JOptionPane.showInputDialog(
                "Please specifiy the location of the folder to be watched"));

        FolderWatcher watcher = new FolderWatcher(properties);

        watcher.addWatchEventListener(new WatchEventListener() {

            @Override
            public void onWatchEvent(WatchEvent event) {
                System.out.println(event.context().toString());
            }

            @Override
            public boolean canHandleWatchEvent(WatchEvent event) {
                return event.kind() == StandardWatchEventKind.ENTRY_CREATE;
            }

        });

        new Thread(watcher).start();
    }

}