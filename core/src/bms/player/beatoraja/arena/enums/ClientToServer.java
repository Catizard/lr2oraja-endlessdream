package bms.player.beatoraja.arena.enums;

public enum ClientToServer {
    CTS_SELECTED_BMS(1),
    CTS_PLAYER_SCORE(2),
    CTS_CHART_CANCELLED(3),
    CTS_LOADING_COMPLETE(4),
    CTS_USERNAME(5),
    CTS_MESSAGE(6),
    CTS_MISSING_CHART(7),
    CTS_SET_HOST(8),
    CTS_KICK_USER(9),
    CTS_ITEM(10),
    CTS_ITEM_SETTINGS(11);

    char value;

    ClientToServer(int value) {
        this.value = (char)value;
    }

    public char getValue() {
        return value;
    }
}
