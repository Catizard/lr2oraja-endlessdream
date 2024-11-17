package bms.player.beatoraja.stream.command;

import bms.player.beatoraja.BMSPlayerMode;
import bms.player.beatoraja.select.MusicSelector;
import bms.player.beatoraja.select.bar.HashBar;
import bms.player.beatoraja.select.bar.SongBar;
import bms.player.beatoraja.song.SongData;

import java.util.*;

/**
 * Like StreamRequestCommand, but force playing it immediately
 */
public class StreamForceRequestCommand extends StreamCommand {
    private MusicSelector selector;
    private int maxLength = 30;
    private Thread updaterThread;
    private UpdateBar updater;

    public StreamForceRequestCommand(MusicSelector selector) {
        COMMAND_STRING = "!!req_force";
        this.selector = selector;
        this.maxLength = selector.main.getPlayerConfig().getMaxRequestCount();
        this.updater = new UpdateBar();
        updaterThread = new Thread(this.updater);
        updaterThread.start();
    }

    @Override
    public void run(String data) {
        if (data.length() != 64) {
            return;
        }

        updater.receive(data);
    }

    @Override
    public void dispose() {
        if (this.updaterThread != null) {
            updaterThread.interrupt();
        }
    }

    private class UpdateBar implements Runnable {
        final int MESSAGE_TIME = 3000;

        // sha256 queue
        private Queue<String> queue = new ArrayDeque<>();
        private final Object LOCK = new Object();

        public void receive(String sha256) {
            synchronized (LOCK) {
                queue.add(sha256);
                // TODO: notify the user?
            }
        }

        void update() {
            synchronized (LOCK) {
                // TODO: race?
                if (selector.main.getCurrentState() instanceof MusicSelector) {
                    if (!queue.isEmpty()) {
                        String sha256 = queue.poll();
                        SongData[] find = selector.getSongDatabase().getSongDatas(new String[]{escape(sha256)});
                        // TODO: find might not exist
                        if (find != null && find.length > 0) {
                            SongData songData = find[0];
                            SongBar hackBar = new SongBar(songData);
                            selector.pushPlayingList(hackBar);
                            selector.selectSong(BMSPlayerMode.PLAY);
                        }
                    }
                }
            }
        }

        private String escape(String before) {
            // とりあえずSQLに渡すのでエスケープする
            StringBuilder after = new StringBuilder();
            for (int i = 0; i < before.length(); i++) {
                char c = before.charAt(i);
                if (c == '_' || c == '%' || c == '\\') {
                    after.append('\\');
                }
                after.append(c);
            }
            return after.toString();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (LOCK) {
                        if (!queue.isEmpty()) {
                            update();
                        }
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
