package bms.player.beatoraja.decide;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor.ControlKeys;
import bms.player.beatoraja.skin.SkinType;

import static bms.player.beatoraja.SystemSoundManager.SoundType.DECIDE;
import static bms.player.beatoraja.skin.SkinProperty.TIMER_FADEOUT;
import static bms.player.beatoraja.skin.SkinProperty.TIMER_STARTINPUT;

/**
 * The transition between selecting song and playing song
 *
 * @author exch
 */
public class MusicDecide extends MainState {

    private boolean cancel;

    public MusicDecide(MainController main) {
        super(main);
    }

    public void create() {
        cancel = false;

        loadSkin(SkinType.DECIDE);

        resource.setOrgGaugeOption(resource.getPlayerConfig().getGauge());
    }

    public void prepare() {
        super.prepare();
        play(DECIDE);
    }

    public void render() {
        long nowTime = timer.getNowTime();
        if (nowTime > getSkin().getInput()) {
            timer.switchTimer(TIMER_STARTINPUT, true);
        }
        if (timer.isTimerOn(TIMER_FADEOUT)) {
            if (timer.getNowTime(TIMER_FADEOUT) > getSkin().getFadeout()) {
                main.changeState(cancel ? MainStateType.MUSICSELECT : MainStateType.PLAY);
            }
        } else {
            if (nowTime > getSkin().getScene()) {
                timer.setTimerOn(TIMER_FADEOUT);
            }
        }
    }

    public void input() {
        if (!timer.isTimerOn(TIMER_FADEOUT) && timer.isTimerOn(TIMER_STARTINPUT)) {
            BMSPlayerInputProcessor input = main.getInputProcessor();
            if (input.getKeyState(0) || input.getKeyState(2) || input.getKeyState(4) || input.getKeyState(6) || input.isControlKeyPressed(ControlKeys.ENTER)) {
                timer.setTimerOn(TIMER_FADEOUT);
            }
            if (input.isControlKeyPressed(ControlKeys.ESCAPE) || (input.startPressed() && input.isSelectPressed())) {
                cancel = true;
                main.getAudioProcessor().setGlobalPitch(1f);
                timer.setTimerOn(TIMER_FADEOUT);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
