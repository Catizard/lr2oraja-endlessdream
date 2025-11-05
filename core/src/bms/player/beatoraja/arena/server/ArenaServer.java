package bms.player.beatoraja.arena.server;

/**
 * A simple static wrapper of server instance of JLR2ArenaEx
 */
public class ArenaServer {
    private static io.github.catizard.jlr2arenaex.server.ArenaServer server = null;

    public static void start() {
        stop();
        try {
            server = new io.github.catizard.jlr2arenaex.server.ArenaServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
        if (server != null) {
            try {
                server.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
