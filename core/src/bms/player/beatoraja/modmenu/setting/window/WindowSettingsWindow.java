package bms.player.beatoraja.modmenu.setting.window;

import bms.player.beatoraja.Config;
import bms.player.beatoraja.PlayerConfig;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.modmenu.setting.widget.*;
import bms.tool.util.Pair;

import java.util.Arrays;
import java.util.List;

public class WindowSettingsWindow extends TiledOptionBasedWindow {
	private final EnumComboWidget<Resolution> screenResolution = new EnumComboWidget<>("##Screen Resolution", Resolution.class, config::setResolution);
	// TODO: Boolean option is using "enableHttp" for a temporary placeholder
	private final CheckboxWidget enableFullscreen = new CheckboxWidget("##Enable Fullscreen", config::setEnableHttp);
	private final CheckboxWidget enableBoardlessWindow = new CheckboxWidget("##Enable Boardless Window", config::setEnableHttp);
	private final StringComboWidget frameLimiter = new StringComboWidget("##Frame Limiter", new String[]{"Not implemented yet"}, config::setMaxFramePerSecond);
	private final StringComboWidget customFPS = new StringComboWidget("##Set Custom FPS", new String[]{"Not implemented yet"}, config::setMaxFramePerSecond);
	private final CheckboxWidget vsync = new CheckboxWidget("##Vsync", config::setVsync);
	private final CheckboxWidget displayFPS = new CheckboxWidget("##Display FPS Counter", config::setDisplayFPS);
	private final StringComboWidget displayBGA = new StringComboWidget("##Display BGA", new String[]{"On", "Auto", "Off"}, StringComboWidget.PredefinedWidth.Short, config::setBga);
	private final StringComboWidget bgaExpand = new StringComboWidget("##BGA Expand", new String[]{"Full", "Keep Aspect Ratio", "Off"}, StringComboWidget.PredefinedWidth.Medium, config::setBgaExpand);
	private final DragIntegerWidget missLayerDuration = new DragIntegerWidget("##Miss Layer Duration", playerConfig::setMisslayerDuration);

	private final List<Pair<String, List<TiledOption<?>>>> options = List.of(
			Pair.of("Window", Arrays.asList(
					new TiledOption<>("Screen Resolution", config::getResolution, screenResolution),
					new TiledOption<>("Enable Fullscreen", config::isEnableHttp, enableFullscreen),
					new TiledOption<>("Enable Boardless Window", config::isEnableHttp, enableBoardlessWindow)
			)),
			Pair.of("Frame Limiter", Arrays.asList(
					new TiledOption<>("Frame Limiter", config::getMaxFramePerSecond, frameLimiter),
					new TiledOption<>("Set Custom FPS", config::getMaxFramePerSecond, customFPS),
					new TiledOption<>("Vertical Sync", config::isVsync, vsync),
					new TiledOption<>("Display FPS", config::isDisplayFPS, displayFPS)
			)),
			Pair.of("BGA", Arrays.asList(
					new TiledOption<>("Display BGA", config::getBga, displayBGA),
					new TiledOption<>("Miss Layer Duration", playerConfig::getMisslayerDuration, missLayerDuration),
					new TiledOption<>("BGA Expand", config::getBgaExpand, bgaExpand)
			))
	);

	public WindowSettingsWindow(Config config, PlayerConfig playerConfig) {
		super("Window", config, playerConfig);
	}

	@Override
	public List<Pair<String, List<TiledOption<?>>>> getOptions() {
		return options;
	}
}
