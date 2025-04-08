package bms.player.beatoraja.skin.lr2;

import bms.player.beatoraja.Config;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.Resolution;
import bms.player.beatoraja.decide.MusicDecideSkin;
import bms.player.beatoraja.skin.SkinHeader;
import com.badlogic.gdx.utils.IntIntMap;

import java.io.IOException;

/**
 * LR2ディサイドスキンローダー
 *
 * @author exch
 */
public class LR2DecideSkinLoader extends LR2SkinCSVLoader<MusicDecideSkin> {

    public LR2DecideSkinLoader(Resolution src, final Config c) {
        super(src, c);
    }

    public MusicDecideSkin loadSkin(MainState decide, SkinHeader header, IntIntMap option) throws IOException {
        return this.loadSkin(new MusicDecideSkin(header), decide, option);
    }
}
