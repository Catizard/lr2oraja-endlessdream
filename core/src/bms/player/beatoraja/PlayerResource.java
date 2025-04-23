package bms.player.beatoraja;

import bms.model.*;
import bms.player.beatoraja.CourseData.CourseDataConstraint;
import bms.player.beatoraja.TableData.TableFolder;
import bms.player.beatoraja.audio.AudioDriver;
import bms.player.beatoraja.ir.RankingData;
import bms.player.beatoraja.play.BMSPlayerRule;
import bms.player.beatoraja.play.GrooveGauge;
import bms.player.beatoraja.play.bga.BGAProcessor;
import bms.player.beatoraja.song.SongData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import lombok.Data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * プレイヤーのコンポーネント間でデータをやり取りするためのクラス
 *
 * @author exch
 */
@Data
public class PlayerResource {
    /**
     * 選曲中のBMS
     */
    private BMSModel model;

    private long marginTime;
    /**
     * 選択中のBMSの情報
     */
    private SongData songdata;
    /**
     * BMSModelの元々のモード
     */
    private bms.model.Mode originalMode;

    private PlayerData playerData = new PlayerData();

    private Config config;
    private PlayerConfig playerConfig;
    /**
     * プレイモード
     */
    private BMSPlayerMode playMode;

    private BMSResource bmsResource;

    /**
     * スコア
     */
    private ScoreData scoreData;
    /**
     * ライバルスコア
     */
    private ScoreData rivalScoreData;
    /**
     * ターゲットスコア
     */
    private ScoreData targetScoreData;

    private RankingData rankingData;
    /**
     * スコア更新するかどうか
     */
    private boolean updateScore = true;
    private boolean updateCourseScore = true;
    private GrooveGauge grooveGauge;
    /**
     * ゲージの遷移ログ
     */
    private FloatArray[] gauge;

    private ReplayData replayData;

    private Path[] bmsPaths;
    private boolean loop;

    /**
     * コース
     */
    private CourseData courseData;
    /**
     * コースのBMSモデル
     */
    private BMSModel[] course;
    /**
     * コース何曲目
     */
    private int courseindex;
    /**
     * コースゲージ履歴
     */
    private Array<FloatArray[]> coursegauge = new Array<FloatArray[]>();

    private Array<ReplayData> courseReplay = new Array<ReplayData>();
    private ScoreData courseScoreData;
    /**
     * コンボ数。コースプレイ時の引継ぎに使用
     */
    private int combo;
    /**
     * 最大コンボ数。コースプレイ時の引継ぎに使用
     */
    private int maxcombo;
    /**
     * 元々のゲージオプション
     */
    private int orgGaugeOption = 0;

    private int assist = 0;
    /**
     * 現在プレイしている楽曲を含む難易度表とレベル
     */
    private String tableName = "";
    private String tableLevel = "";

    public PlayerResource(AudioDriver audio, Config config, PlayerConfig pconfig) {
        this.config = config;
        this.playerConfig = pconfig;
        this.bmsResource = new BMSResource(audio, config, pconfig);
        this.orgGaugeOption = pconfig.getGauge();
    }

    public void clear() {
        course = null;
        courseindex = 0;
        courseScoreData = null;
        scoreData = null;
//		rscore = null;
        targetScoreData = null;
        gauge = null;
        courseReplay.clear();
        coursegauge.clear();
        combo = 0;
        maxcombo = 0;
        bmsPaths = null;
        setTableName("");
        setTableLevel("");
    }

    public boolean setBMSFile(final Path f, BMSPlayerMode mode) {
        // TODO play mode, リプレイデータでの読み込み分岐をここで行う
        this.playMode = mode;
        replayData = new ReplayData();
        model = loadBMSModel(f, playerConfig.getLnmode());
        if (model == null) {
            Logger.getGlobal().warning("楽曲が存在しないか、解析時にエラーが発生しました:" + f.toString());
            return false;
        }
        if (model.getAllTimeLines().length == 0) {
            return false;
        }

        originalMode = model.getMode();
        bmsResource.setBMSFile(model, f, config, mode);
        if (songdata != null) {
            songdata.setBMSModel(model);
        } else {
            songdata = new SongData(model, false);
        }
        if (tableName.length() == 0 || courseindex != 0) {
            setTableInfo();
        }
        return true;
    }

    public BMSModel loadBMSModel(Path f, int lnmode) {
        return loadBMSModel(new ChartInformation(f, lnmode, null));
    }

    public BMSModel loadBMSModel(int[] selectedRandom) {
        if (model != null) {
            ChartInformation info = model.getChartInformation();
            return loadBMSModel(new ChartInformation(info.path, info.lntype, selectedRandom));
        }
        return null;
    }

    public BMSModel loadBMSModel(ChartInformation info) {
        ChartDecoder decoder = ChartDecoder.getDecoder(info.path);
        if (decoder == null) {
            return null;
        }
        BMSModel model = decoder.decode(info);
        if (model == null) {
            return null;
        }

        marginTime = BMSModelUtils.setStartNoteTime(model, 1000);
        BMSPlayerRule.validate(model);

        // 地雷ノートに爆発音が定義されていない場合、デフォルト爆発音をセットする
        final int lanes = model.getMode().key;
        final int wavcount = model.getWavList().length;
        for (TimeLine tl : model.getAllTimeLines()) {
            for (int i = 0; i < lanes; i++) {
                final Note n = tl.getNote(i);
                if (n != null) {
                    if (n instanceof MineNote && n.getWav() < 0) {
                        n.setWav(wavcount);
                    }
                }
            }
        }

        return model;
    }

    public BMSModel getBMSModel() {
        return model;
    }

    public BGAProcessor getBGAManager() {
        return bmsResource.getBGAProcessor();
    }

    public boolean mediaLoadFinished() {
        return bmsResource.mediaLoadFinished();
    }

    public boolean setCourseBMSFiles(Path[] files) {
        Array<BMSModel> models = new Array();
        for (Path f : files) {
            BMSModel model = loadBMSModel(f, playerConfig.getLnmode());
            if (model == null) {
                return false;
            }
            models.add(model);
        }
        course = models.toArray(BMSModel.class);
        updateCourseScore = true;
        return true;
    }

    public BMSModel[] getCourseBMSModels() {
        return course;
    }

    public void setAutoPlaySongs(Path[] paths, boolean loop) {
        this.bmsPaths = paths;
        this.loop = loop;
    }

    public boolean nextSong() {
        if (bmsPaths == null) {
            return false;
        }
        final int orgindex = courseindex;
        do {
            if (courseindex == bmsPaths.length) {
                if (loop) {
                    courseindex = 0;
                } else {
                    return false;
                }
            }
            songdata = null;
            if (setBMSFile(bmsPaths[courseindex++], BMSPlayerMode.AUTOPLAY)) {
                return true;
            }
        } while (orgindex != courseindex);
        return false;
    }

    public boolean nextCourse() {
        courseindex++;
        if (courseindex == course.length) {
            return false;
        } else {
            songdata = null;
            setBMSFile(Paths.get(course[courseindex].getPath()), playMode);
            return true;
        }
    }

    public int getCourseIndex() {
        return courseindex;
    }

    public void reloadBMSFile() {
        if (model != null) {
            model = loadBMSModel(Paths.get(model.getPath()), playerConfig.getLnmode());
        }
        final String name = tableName;
        final String lev = tableLevel;
        clear();
        tableName = name;
        tableLevel = lev;
    }

    public String getCourseTitle() {
        return courseData != null ? courseData.getName() : null;
    }

    public CourseDataConstraint[] getConstraint() {
        return courseData != null ? courseData.getConstraint() : new CourseDataConstraint[0];
    }

    public ReplayData[] getCourseReplay() {
        return courseReplay.toArray(ReplayData.class);
    }

    public void addCourseReplay(ReplayData rd) {
        courseReplay.add(rd);
    }

    public Array<FloatArray[]> getCourseGauge() {
        return coursegauge;
    }

    public void addCourseGauge(FloatArray[] gauge) {
        coursegauge.add(gauge);
    }

    public void dispose() {
        if (bmsResource != null) {
            bmsResource.dispose();
            bmsResource = null;
        }
    }

    public BMSResource getBMSResource() {
        return bmsResource;
    }

    public String getTableFullName() {
        return tableLevel + tableName;
    }

    private void setTableInfo() {
        final String[] urls = this.getConfig().getTableURL();
        final TableDataAccessor tdaccessor = new TableDataAccessor(config.getTablepath());
        final TableData[] tds = tdaccessor.readAll();
        for (String url : urls) {
            for (TableData td : tds) {
                if (td.getUrl().equals(url)) {
                    final TableFolder[] tfs = td.getFolder();
                    for (TableFolder tf : tfs) {
                        final SongData[] tss = tf.getSong();
                        for (SongData ts : tss) {
                            if ((ts.getMd5().length() != 0 && this.getSongdata().getMd5().length() != 0 &&
                                    ts.getMd5().equals(this.getSongdata().getMd5())) ||
                                    (ts.getMd5().length() != 0 && this.getSongdata().getMd5().length() != 0 &&
                                            ts.getSha256().equals(this.getSongdata().getSha256()))) {
                                setTableName(td.getName());
                                setTableLevel(tf.getName());
                                return;
                            }
                        }
                    }
                }
            }
        }
        setTableName("");
        setTableLevel("");
    }
}