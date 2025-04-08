package bms.player.beatoraja.input;

/**
 * 入力デバイス
 *
 * @author excln
 */
public abstract class BMSPlayerInputDevice {

    /**
     * 入力デバイスの種類
     */
    public final Type type;
    protected final BMSPlayerInputProcessor bmsPlayerInputProcessor;

    protected BMSPlayerInputDevice(BMSPlayerInputProcessor bmsPlayerInputProcessor, Type type) {
        this.bmsPlayerInputProcessor = bmsPlayerInputProcessor;
        this.type = type;
    }

    /**
     * デバイスの入力状態をクリアする
     */
    public abstract void clear();

    /**
     * 入力デバイスのタイプ
     *
     * @author excln
     */
    public enum Type {
        KEYBOARD,
        BM_CONTROLLER,
        MIDI
    }
}
