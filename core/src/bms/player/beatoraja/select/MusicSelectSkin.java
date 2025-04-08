package bms.player.beatoraja.select;

import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinHeader;
import bms.player.beatoraja.skin.SkinText;
import com.badlogic.gdx.math.Rectangle;

/**
 * 選曲スキン
 *
 * @author exch
 */
public class MusicSelectSkin extends Skin {

    public SkinText searchText;
    /**
     * カーソルが合っているBarのindex
     */
    private int centerBar;
    /**
     * クリック可能なBarのindex
     */
    private int[] clickableBar = new int[0];
    private Rectangle search;

    public MusicSelectSkin(SkinHeader header) {
        super(header);
    }

    public int[] getClickableBar() {
        return clickableBar;
    }

    public void setClickableBar(int[] clickableBar) {
        this.clickableBar = clickableBar;
    }

    public int getCenterBar() {
        return centerBar;
    }

    public void setCenterBar(int centerBar) {
        this.centerBar = centerBar;
    }

    public Rectangle getSearchTextRegion() {
        return search;
    }

    public void setSearchTextRegion(Rectangle r) {
        search = r;
    }

}
