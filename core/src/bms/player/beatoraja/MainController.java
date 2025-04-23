package bms.player.beatoraja;

import bms.player.beatoraja.AudioConfig.DriverType;
import bms.player.beatoraja.MainState.MainStateType;
import bms.player.beatoraja.MessageRenderer.Message;
import bms.player.beatoraja.audio.AudioDriver;
import bms.player.beatoraja.audio.GdxSoundDriver;
import bms.player.beatoraja.audio.PortAudioDriver;
import bms.player.beatoraja.config.KeyConfiguration;
import bms.player.beatoraja.config.SkinConfiguration;
import bms.player.beatoraja.decide.MusicDecide;
import bms.player.beatoraja.exceptions.PlayerConfigException;
import bms.player.beatoraja.external.DiscordListener;
import bms.player.beatoraja.external.ScreenShotFileExporter;
import bms.player.beatoraja.external.ScreenShotTwitterExporter;
import bms.player.beatoraja.input.BMSPlayerInputProcessor;
import bms.player.beatoraja.input.KeyCommand;
import bms.player.beatoraja.ir.*;
import bms.player.beatoraja.modmenu.ImGuiRenderer;
import bms.player.beatoraja.play.BMSPlayer;
import bms.player.beatoraja.play.TargetProperty;
import bms.player.beatoraja.result.CourseResult;
import bms.player.beatoraja.result.MusicResult;
import bms.player.beatoraja.select.MusicSelector;
import bms.player.beatoraja.select.bar.TableBar;
import bms.player.beatoraja.skin.SkinLoader;
import bms.player.beatoraja.skin.SkinObject.SkinOffset;
import bms.player.beatoraja.skin.SkinProperty;
import bms.player.beatoraja.song.SongData;
import bms.player.beatoraja.song.SongDatabaseAccessor;
import bms.player.beatoraja.song.SongInformationAccessor;
import bms.player.beatoraja.stream.StreamController;
import bms.tool.mdprocessor.MusicDownloadProcessor;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.StringBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * Beatoraja game controller
 *
 * @author exch
 */
@Getter
public class MainController implements ApplicationListener {

    public static final boolean debug = false;
    public static final int offsetCount = SkinProperty.OFFSET_MAX + 1;
    private static final String VERSION = "LR2oraja Endless Dream 0.2.1";

    /**
     * Game start time
     */
    private final long bootTime = System.currentTimeMillis();
    /**
     * A calendar singleton, not very useful
     */
    private final Calendar cl = Calendar.getInstance();
    /**
     * Current player config
     */
    private final PlayerConfig playerConfig;
    private final SkinOffset[] offset = new SkinOffset[offsetCount];
    private final Array<MainStateListener> stateListener = new Array<>();
    /**
     * A shared StringBuilder, to reduce the new-destroy cost,
     * because it's used in render steps
     */
    private final StringBuilder message = new StringBuilder();
    private final TimerManager timer;
    /**
     * Beatoraja's global configuration
     */
    private final Config config;
    private final boolean songUpdated;
    private final IRStatus[] ir;
    private final RivalDataAccessor rivals = new RivalDataAccessor();
    private final RankingDataCache irCache = new RankingDataCache();
    /**
     * 1曲プレイで指定したBMSファイル
     */
    private final Path bmsFile;
    /**
     * プレイデータアクセサ
     */
    private final PlayDataAccessor playData;
    private final SystemSoundManager sound;
    private final BMSPlayerMode auto;
    public ImGuiRenderer imGui;
    public List<IRSendStatus> irSendStatus = new ArrayList<>();
    protected TextureRegion black;
    protected TextureRegion white;
    /**
     * Beatoraja contains many scenes(select, play...), this is the ref to current running scene<br>
     * If you know some kind of `elm` structure program, you can easily understand it
     */
    private MainState currentState;
    /**
     * In-game message render, call
     * <pre><code>
     *      messageRenderer.addMessage(message, duration, color, type);
     * </code></pre>
     * to render an in-game message
     */
    private MessageRenderer messageRenderer;
    /**
     * Stream request polling controller<br>
     * See stream request module
     */
    private StreamController streamController;
    private long mouseMovedTime;
    private BMSPlayer bmsplayer;
    private MusicDecide decide;
    private MusicSelector selector;
    private MusicResult result;
    private CourseResult courseResult;
    /**
     * Keymap configuration
     * TODO: is it part of the player config?
     */
    private KeyConfiguration keymapConfig;
    private SkinConfiguration skinConfig;
    private AudioDriver audio;
    private PlayerResource playerResource;
    private BitmapFont systemFont;
    /**
     * songinfo.db file accessor
     */
    private SongInformationAccessor infoDatabase;
    private SpriteBatch spriteBatch;
    private BMSPlayerInputProcessor inputProcessor;
    /**
     * Whether to draw fps or not
     */
    private boolean showFps;
    private Thread screenshot;
    private MusicDownloadProcessor download;
    private long prevTime;
    private UpdateThread updateSong;
    private UpdateThread downloadIpfs;

    public MainController(Path f, Config config, PlayerConfig playerConfig, BMSPlayerMode auto, boolean songUpdated) {
        this.auto = auto;
        this.config = config;
        this.songUpdated = songUpdated;

        for (int i = 0; i < offset.length; i++) {
            offset[i] = new SkinOffset();
        }

        if (playerConfig == null) {
            try {
                playerConfig = PlayerConfig.readPlayerConfig(config.getPlayerpath(), config.getPlayername());
            } catch (PlayerConfigException e) {
                Logger.getGlobal().severe(e.getLocalizedMessage());
            }
        }
        this.playerConfig = playerConfig;

        this.bmsFile = f;

        if (config.isEnableIpfs()) {
            Path ipfspath = Paths.get("ipfs").toAbsolutePath();
            if (!ipfspath.toFile().exists() && !ipfspath.toFile().mkdirs()) {
                Logger.getGlobal().severe("Failed to create ipfs directory");
            }
            List<String> roots = new ArrayList<>(Arrays.asList(getConfig().getBmsroot()));
            if (ipfspath.toFile().exists() && !roots.contains(ipfspath.toString())) {
                roots.add(ipfspath.toString());
                getConfig().setBmsroot(roots.toArray(new String[0]));
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            if (config.isUseSongInfo()) {
                infoDatabase = new SongInformationAccessor(config.getSonginfopath());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        playData = new PlayDataAccessor(config);

        Array<IRStatus> irarray = new Array<>();

        if (playerConfig == null) {
            Logger.getGlobal().severe("Player config is missing, skipping ir connect steps. Please check your config files!");
        } else if (playerConfig.getIrconfig() == null) {
            Logger.getGlobal().severe("IR config from player config is empty, skipping ir connect steps. Please check your config files!");
        } else {
            for (IRConfig irconfig : playerConfig.getIrconfig()) {
                final IRConnection ir = IRConnectionManager.getIRConnection(irconfig.getIrname());
                if (ir != null) {
                    if (!irconfig.getUserid().isEmpty() && !irconfig.getPassword().isEmpty()) {
                        try {
                            IRResponse<IRPlayerData> response = ir.login(new IRAccount(irconfig.getUserid(), irconfig.getPassword(), ""));
                            if (response.isSucceeded()) {
                                irarray.add(new IRStatus(irconfig, ir, response.getData()));
                            } else {
                                Logger.getGlobal().warning("IRへのログイン失敗 : " + response.getMessage());
                            }
                        } catch (IllegalArgumentException e) {
                            Logger.getGlobal().info("trying pre-0.8.5 IR login method");
                            IRResponse<IRPlayerData> response = ir.login(irconfig.getUserid(), irconfig.getPassword());
                            if (response.isSucceeded()) {
                                irarray.add(new IRStatus(irconfig, ir, response.getData()));
                            } else {
                                Logger.getGlobal().warning("IRへのログイン失敗 : " + response.getMessage());
                            }
                        }
                    }
                }
            }
        }

        ir = irarray.toArray(IRStatus.class);

        rivals.update(this);

        switch (config.getAudioConfig().getDriver()) {
            case PortAudio:
                try {
                    audio = new PortAudioDriver(config);
                } catch (Throwable e) {
                    e.printStackTrace();
                    config.getAudioConfig().setDriver(DriverType.OpenAL);
                }
                break;
        }

        timer = new TimerManager();
        sound = new SystemSoundManager(this);

        if (config.isUseDiscordRPC()) {
            stateListener.add(new DiscordListener());
        }
    }

    public static String getVersion() {
        return VERSION;
    }

    public SkinOffset getOffset(int index) {
        return offset[index];
    }

    public SongDatabaseAccessor getSongDatabase() {
        return MainLoader.getScoreDatabaseAccessor();
    }

    public PlayDataAccessor getPlayDataAccessor() {
        return playData;
    }

    public RivalDataAccessor getRivalDataAccessor() {
        return rivals;
    }

    public RankingDataCache getRankingDataCache() {
        return irCache;
    }

    public void changeState(MainStateType state) {
        MainState newState = null;
        //@formatter:off
        switch (state) {
            case MUSICSELECT:
                if (this.bmsFile != null) {
                    exit();
                } else {
                    newState = selector;
                }
                break;
            case DECIDE: newState = decide; break;
            case PLAY:
                if (bmsplayer != null) {
                    bmsplayer.dispose();
                }
                bmsplayer = new BMSPlayer(this, playerResource);
                newState = bmsplayer;
                break;
            case RESULT: newState = result; break;
            case COURSERESULT: newState = courseResult; break;
            case CONFIG: newState = keymapConfig; break;
            case SKINCONFIG: newState = skinConfig; break;
        }
        //@formatter:on

        if (newState != null && currentState != newState) {
            if (currentState != null) {
                currentState.shutdown();
                currentState.setSkin(null);
            }
            newState.create();
            if (newState.getSkin() != null) {
                newState.getSkin().prepare(newState);
            }
            currentState = newState;
            timer.setMainState(newState);
            currentState.prepare();
            updateMainStateListener(0);
        }
        if (currentState.getStage() != null) {
            Gdx.input.setInputProcessor(new InputMultiplexer(currentState.getStage(), inputProcessor.getKeyBoardInputProcesseor()));
        } else {
            Gdx.input.setInputProcessor(inputProcessor.getKeyBoardInputProcesseor());
        }
    }

    @Override
    public void create() {
        final long t = System.currentTimeMillis();
        spriteBatch = new SpriteBatch();
        SkinLoader.initPixmapResourcePool(config.getSkinPixmapGen());


        ImGuiRenderer.init();

        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(config.getSystemfontpath()));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = 24;
            systemFont = generator.generateFont(parameter);
            generator.dispose();
        } catch (GdxRuntimeException e) {
            Logger.getGlobal().severe("System Font読み込み失敗");
        }
        messageRenderer = new MessageRenderer(config.getMessagefontpath());

        inputProcessor = new BMSPlayerInputProcessor(config, playerConfig);
        switch (config.getAudioConfig().getDriver()) {
            case OpenAL:
                audio = new GdxSoundDriver(config);
                break;
//		case AudioDevice:
//			audio = new GdxAudioDeviceDriver(config);
//			break;
        }

        playerResource = new PlayerResource(audio, config, playerConfig);
        selector = new MusicSelector(this, songUpdated);
        if (playerConfig.getRequestEnable()) {
            streamController = new StreamController(selector, (playerConfig.getRequestNotify() ? messageRenderer : null));
            streamController.run();
        }
        decide = new MusicDecide(this);
        result = new MusicResult(this);
        courseResult = new CourseResult(this);
        keymapConfig = new KeyConfiguration(this);
        skinConfig = new SkinConfiguration(this, playerConfig);
        if (bmsFile != null) {
            if (playerResource.setBMSFile(bmsFile, auto)) {
                changeState(MainStateType.PLAY);
            } else {
                // ダミーステートに移行してすぐexitする
                changeState(MainStateType.CONFIG);
                exit();
            }
        } else {
            changeState(MainStateType.MUSICSELECT);
        }

        Logger.getGlobal().info("初期化時間(ms) : " + (System.currentTimeMillis() - t));

        Thread polling = new Thread(() -> {
            long time = 0;
            for (; ; ) {
                final long now = System.nanoTime() / 1000000;
                if (time != now) {
                    time = now;
                    inputProcessor.poll();
                } else {
                    try {
                        Thread.sleep(0, 500000);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        });
        polling.start();

        Array<String> targetlist = new Array<>(playerConfig.getTargetlist());
        for (int i = 0; i < rivals.getRivalCount(); i++) {
            targetlist.add("RIVAL_" + (i + 1));
        }
        TargetProperty.setTargets(targetlist.toArray(String.class), this);

        Pixmap plainPixmap = new Pixmap(2, 1, Pixmap.Format.RGBA8888);
        plainPixmap.drawPixel(0, 0, Color.toIntBits(255, 0, 0, 0));
        plainPixmap.drawPixel(1, 0, Color.toIntBits(255, 255, 255, 255));
        Texture plainTexture = new Texture(plainPixmap);
        black = new TextureRegion(plainTexture, 0, 0, 1, 1);
        white = new TextureRegion(plainTexture, 1, 0, 1, 1);
        plainPixmap.dispose();

        Gdx.gl.glClearColor(0, 0, 0, 1);

        if (config.isEnableIpfs()) {
            download = new MusicDownloadProcessor(config.getIpfsUrl(), (md5) -> {
                SongData[] s = getSongDatabase().getSongDatas(md5);
                String[] result = new String[s.length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = s[i].getPath();
                }
                return result;
            });
            download.start(null);
        }

        if (ir.length > 0) {
            messageRenderer.addMessage(ir.length + " IR Connection Succeed", 5000, Color.GREEN, 1);

            Thread irResendProcess = new Thread(() -> {
                for (; ; ) {
                    final long now = System.currentTimeMillis();
                    try {
                        List<IRSendStatus> removeIrSendStatus = new ArrayList<IRSendStatus>();

                        for (IRSendStatus score : irSendStatus) {
                            long timeUntilNextTry = (long) (Math.pow(4, score.retry) * 1000);
                            if (score.retry != 0 && now - score.lastTry >= timeUntilNextTry) {
                                score.send();
                            }
                            if (score.isSent) {
                                removeIrSendStatus.add(score);
                            }
                            if (score.retry > getConfig().getIrSendCount()) {
                                removeIrSendStatus.add(score);
                                messageRenderer.addMessage("Failed to send a score for " + score.song.getTitle() + score.song.getSubtitle(), 5000, Color.RED, 1);
                            }
                        }
                        irSendStatus.removeAll(removeIrSendStatus);

                        try {
                            Thread.sleep(3000, 0);
                        } catch (InterruptedException ignored) {
                        }
                    } catch (Exception e) {
                        Logger.getGlobal().severe(e.getMessage());
                    }
                }
            });
            irResendProcess.start();
        }
    }

    @Override
    public void render() {
//		input.poll();
        timer.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render current scene
        currentState.render();

        // TODO: what is this?
        spriteBatch.begin();
        if (currentState.getSkin() != null) {
            currentState.getSkin().updateCustomObjects(currentState);
            currentState.getSkin().drawAllObjects(spriteBatch, currentState);
        }
        spriteBatch.end();

        final Stage stage = currentState.getStage();
        if (stage != null) {
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            stage.draw();
        }

        // show fps
        if (showFps && systemFont != null) {
            spriteBatch.begin();
            systemFont.setColor(Color.CYAN);
            message.setLength(0);
            systemFont.draw(spriteBatch, message.append("FPS ").append(Gdx.graphics.getFramesPerSecond()), 10,
                    config.getResolution().height - 2);
            if (debug) {
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Skin Pixmap Images ").append(SkinLoader.getResource().size()), 10,
                        config.getResolution().height - 26);
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Total Memory Used(MB) ").append(Runtime.getRuntime().totalMemory() / (1024 * 1024)), 10,
                        config.getResolution().height - 50);
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Total Free Memory(MB) ").append(Runtime.getRuntime().freeMemory() / (1024 * 1024)), 10,
                        config.getResolution().height - 74);
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Max Sprite In Batch ").append(spriteBatch.maxSpritesInBatch), 10,
                        config.getResolution().height - 98);
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Skin Pixmap Resource Size ").append(SkinLoader.getResource().size()), 10,
                        config.getResolution().height - 122);
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Stagefile Pixmap Resource Size ").append(selector.getStagefileResource().size()), 10,
                        config.getResolution().height - 146);
                message.setLength(0);
                systemFont.draw(spriteBatch, message.append("Banner Pixmap Resource Size ").append(selector.getBannerResource().size()), 10,
                        config.getResolution().height - 170);
            }

            spriteBatch.end();
        }
        ImGuiRenderer.start();
        ImGuiRenderer.render();
        ImGuiRenderer.end();

        // show message
        spriteBatch.begin();
        messageRenderer.render(currentState, spriteBatch, 100, config.getResolution().height - 2);
        spriteBatch.end();

        // TODO renderループに入れるのではなく、MusicDownloadProcessorのListenerとして実装したほうがいいのでは
        if (download != null && download.isDownload()) {
            downloadIpfsMessageRenderer(download.getMessage());
        }

        final long time = System.currentTimeMillis();
        if (time > prevTime) {
            prevTime = time;
            currentState.input();
            // event - move pressed
            if (inputProcessor.isMousePressed()) {
                inputProcessor.setMousePressed();
                currentState.getSkin().mousePressed(currentState, inputProcessor.getMouseButton(), inputProcessor.getMouseX(), inputProcessor.getMouseY());
            }
            // event - move dragged
            if (inputProcessor.isMouseDragged()) {
                inputProcessor.setMouseDragged();
                currentState.getSkin().mouseDragged(currentState, inputProcessor.getMouseButton(), inputProcessor.getMouseX(), inputProcessor.getMouseY());
            }

            // マウスカーソル表示判定
            if (inputProcessor.isMouseMoved()) {
                inputProcessor.setMouseMoved(false);
                mouseMovedTime = time;
            }
            Gdx.input.setCursorCatched(currentState == bmsplayer && time > mouseMovedTime + 5000);
            // FPS表示切替
            if (inputProcessor.isActivated(KeyCommand.SHOW_FPS)) {
                showFps = !showFps;
            }
            // full-screen -> windowed
            if (inputProcessor.isActivated(KeyCommand.SWITCH_SCREEN_MODE)) {
                boolean fullscreen = Gdx.graphics.isFullscreen();
                Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
                if (fullscreen) {
                    Gdx.graphics.setWindowedMode(currentMode.width, currentMode.height);
                } else {
                    Gdx.graphics.setFullscreenMode(currentMode);
                }
                config.setDisplaymode(fullscreen ? Config.DisplayMode.WINDOW : Config.DisplayMode.FULLSCREEN);
            }

            // screen shot
            if (inputProcessor.isActivated(KeyCommand.SAVE_SCREENSHOT)) {
                if (screenshot == null || !screenshot.isAlive()) {
                    final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
                    screenshot = new Thread(() -> {
                        // 全ピクセルのアルファ値を255にする(=透明色を無くす)
                        for (int i = 3; i < pixels.length; i += 4) {
                            pixels[i] = (byte) 0xff;
                        }
                        new ScreenShotFileExporter().send(currentState, pixels);
                    });
                    screenshot.start();
                }
            }

            if (inputProcessor.isActivated(KeyCommand.POST_TWITTER)) {
                if (screenshot == null || !screenshot.isAlive()) {
                    final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), false);
                    screenshot = new Thread(() -> {
                        // 全ピクセルのアルファ値を255にする(=透明色を無くす)
                        for (int i = 3; i < pixels.length; i += 4) {
                            pixels[i] = (byte) 0xff;
                        }
                        new ScreenShotTwitterExporter(playerConfig).send(currentState, pixels);
                    });
                    screenshot.start();
                }
            }

            if (inputProcessor.isActivated(KeyCommand.TOGGLE_MOD_MENU)) {
                ImGuiRenderer.toggleMenu();
            }

            if (download != null && download.getDownloadpath() != null) {
                this.updateSong(download.getDownloadpath());
                download.setDownloadpath(null);
            }
            if (updateSong != null && !updateSong.isAlive()) {
                selector.getBarManager().updateBar();
                updateSong = null;
            }
        }
    }

    @Override
    public void dispose() {
        saveConfig();

        if (bmsplayer != null) {
            bmsplayer.dispose();
        }
        if (selector != null) {
            selector.dispose();
        }
        if (streamController != null) {
            streamController.dispose();
        }
        if (decide != null) {
            decide.dispose();
        }
        if (result != null) {
            result.dispose();
        }
        if (courseResult != null) {
            courseResult.dispose();
        }
        if (keymapConfig != null) {
            keymapConfig.dispose();
        }
        if (skinConfig != null) {
            skinConfig.dispose();
        }
        ImGuiRenderer.dispose();
        playerResource.dispose();
//		input.dispose();
        SkinLoader.getResource().dispose();
        ShaderManager.dispose();
        if (download != null) {
            download.dispose();
        }

        Logger.getGlobal().info("全リソース破棄完了");
    }

    @Override
    public void pause() {
        currentState.pause();
    }

    @Override
    public void resize(int width, int height) {
        currentState.resize(width, height);
    }

    @Override
    public void resume() {
        currentState.resume();
    }

    public void saveConfig() {
        Config.write(config);
        PlayerConfig.write(config.getPlayerpath(), playerConfig);
        Logger.getGlobal().info("設定情報を保存");
    }

    public void exit() {
        Gdx.app.exit();
    }

    public AudioDriver getAudioProcessor() {
        return audio;
    }

    public IRStatus[] getIRStatus() {
        return ir;
    }

    public SystemSoundManager getSoundManager() {
        return sound;
    }

    public MusicDownloadProcessor getMusicDownloadProcessor() {
        return download;
    }

    public void updateMainStateListener(int status) {
        for (MainStateListener listener : stateListener) {
            listener.update(currentState, status);
        }
    }

    public long getPlayTime() {
        return System.currentTimeMillis() - bootTime;
    }

    public Calendar getCurrentTime() {
        cl.setTimeInMillis(System.currentTimeMillis());
        return cl;
    }

    public long getNowTime() {
        return timer.getNowTime();
    }

    public long getNowTime(int id) {
        return timer.getNowTime(id);
    }

    public long getNowMicroTime() {
        return timer.getNowMicroTime();
    }

    public long getNowMicroTime(int id) {
        return timer.getNowMicroTime(id);
    }

    public long getMicroTimer(int id) {
        return timer.getMicroTimer(id);
    }

    public boolean isTimerOn(int id) {
        return getMicroTimer(id) != Long.MIN_VALUE;
    }

    public void updateSong(String path) {
        if (updateSong == null || !updateSong.isAlive()) {
            updateSong = new SongUpdateThread(path);
            updateSong.start();
        } else {
            Logger.getGlobal().warning("楽曲更新中のため、更新要求は取り消されました");
        }
    }

    public void updateTable(TableBar reader) {
        if (updateSong == null || !updateSong.isAlive()) {
            updateSong = new TableUpdateThread(reader);
            updateSong.start();
        } else {
            Logger.getGlobal().warning("楽曲更新中のため、更新要求は取り消されました");
        }
    }

    public void downloadIpfsMessageRenderer(String message) {
        if (downloadIpfs == null || !downloadIpfs.isAlive()) {
            downloadIpfs = new DownloadMessageThread(message);
            downloadIpfs.start();
        }
    }

    public static class IRSendStatus {
        public final IRConnection ir;
        public final SongData song;
        public final ScoreData score;
        public int retry = 0;
        public long lastTry = 0;
        public boolean isSent = false;

        public IRSendStatus(IRConnection ir, SongData song, ScoreData score) {
            this.ir = ir;
            this.song = song;
            this.score = score;
        }

        public boolean send() {
            Logger.getGlobal().info("IRへスコア送信中 : " + song.getTitle());
            lastTry = System.currentTimeMillis();
            IRResponse<Object> send1 = ir.sendPlayData(new IRChartData(song), new bms.player.beatoraja.ir.IRScoreData(score));
            retry++;
            if (send1.isSucceeded()) {
                Logger.getGlobal().info("IRスコア送信完了 : " + song.getTitle());
                isSent = true;
                return true;
            } else {
                Logger.getGlobal().warning("IRスコア送信失敗 : " + send1.getMessage());
                return false;
            }

        }
    }

    abstract static class UpdateThread extends Thread {

        protected String message;

        public UpdateThread(String message) {
            this.message = message;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IRStatus {
        public IRConfig config;
        public IRConnection connection;
        public IRPlayerData player;
    }

    /**
     * 楽曲データベース更新用スレッド
     *
     * @author exch
     */
    class SongUpdateThread extends UpdateThread {

        private final String path;

        public SongUpdateThread(String path) {
            super("updating folder : " + (path == null ? "ALL" : path));
            this.path = path;
        }

        public void run() {
            Message message = messageRenderer.addMessage(this.message, Color.CYAN, 1);
            getSongDatabase().updateSongDatas(path, config.getBmsroot(), false, getInfoDatabase());
            message.stop();
        }
    }

    /**
     * 難易度表更新用スレッド
     *
     * @author exch
     */
    class TableUpdateThread extends UpdateThread {

        private final TableBar accessor;

        public TableUpdateThread(TableBar bar) {
            super("updating table : " + bar.getAccessor().name);
            accessor = bar;
        }

        public void run() {
            Message message = messageRenderer.addMessage(this.message, Color.CYAN, 1);
            TableData td = accessor.getAccessor().read();
            if (td != null) {
                accessor.getAccessor().write(td);
                accessor.setTableData(td);
            }
            message.stop();
        }
    }

    class DownloadMessageThread extends UpdateThread {
        public DownloadMessageThread(String message) {
            super(message);
        }

        public void run() {
            Message message = messageRenderer.addMessage(this.message, Color.LIME, 1);
            while (download != null && download.isDownload() && download.getMessage() != null) {
                message.setText(download.getMessage());
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            message.stop();
        }
    }
}
