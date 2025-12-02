package bms.player.beatoraja.result;

import bms.player.beatoraja.MainController;
import bms.player.beatoraja.ScoreData;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyBoardInputProcesseor;
import bms.player.beatoraja.play.GrooveGauge;
import bms.player.beatoraja.skin.SkinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

import static bms.player.beatoraja.skin.SkinProperty.*;

/**
 * Preview old score on a chart, a cropped version of MusicResult
 */
public class PreviewMusicResult extends AbstractResult {
	private static final Logger logger = LoggerFactory.getLogger(PreviewMusicResult.class);

	private ResultKeyProperty property;

	public PreviewMusicResult(MainController main) {
		super(main);
	}

	@Override
	public void create() {
		property = ResultKeyProperty.get(resource.getBMSModel().getMode());
		if (property == null) {
			property = ResultKeyProperty.get(resource.getBMSModel().getMode());
		}
		gaugeType = resource.getGrooveGauge().getType();
		loadSkin(SkinType.RESULT);
	}

	@Override
	public void render() {
		long time = timer.getNowTime();
		timer.switchTimer(TIMER_RESULTGRAPH_BEGIN, true);
		timer.switchTimer(TIMER_RESULTGRAPH_END, true);

		if (((MusicResultSkin) getSkin()).getRankTime() == 0) {
			timer.switchTimer(TIMER_RESULT_UPDATESCORE, true);
		}
		if (time > getSkin().getInput()) {
			timer.switchTimer(TIMER_STARTINPUT, true);
		}
		getScoreDataProperty().update(resource.getScoreData());
	}

	public void input() {
		super.input();
		long time = timer.getNowTime();
		final BMSPlayerInputProcessor inputProcessor = main.getInputProcessor();

		if (!timer.isTimerOn(TIMER_FADEOUT) && timer.isTimerOn(TIMER_STARTINPUT)) {
			if (time > getSkin().getInput()) {
				boolean ok = false;
				for (int i = 0; i < property.getAssignLength(); i++) {
					if (property.getAssign(i) == ResultKeyProperty.ResultKey.CHANGE_GRAPH && inputProcessor.getKeyState(i) && inputProcessor.resetKeyChangedTime(i)) {
						if(gaugeType >= GrooveGauge.ASSISTEASY && gaugeType <= GrooveGauge.HAZARD) {
							gaugeType = (gaugeType + 1) % 6;
						} else {
							gaugeType = (gaugeType - 5) % 3 + 6;
						}
					} else if (property.getAssign(i) != null && inputProcessor.getKeyState(i) && inputProcessor.resetKeyChangedTime(i)) {
						ok = true;
					}
				}

				if (inputProcessor.isControlKeyPressed(KeyBoardInputProcesseor.ControlKeys.ESCAPE) || inputProcessor.isControlKeyPressed(KeyBoardInputProcesseor.ControlKeys.ENTER)) {
					ok = true;
				}

				if (resource.getScoreData() == null || ok) {
					if (((MusicResultSkin) getSkin()).getRankTime() != 0
							&& !timer.isTimerOn(TIMER_RESULT_UPDATESCORE)) {
						timer.switchTimer(TIMER_RESULT_UPDATESCORE, true);
					}
				}
			}
		}
	}

	@Override
	public ScoreData getNewScore() {
		return resource.getScoreData();
	}
}
