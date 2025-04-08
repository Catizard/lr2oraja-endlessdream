package bms.player.beatoraja.skin;

import bms.player.beatoraja.MainState;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

/**
 * スキンのソースイメージセット
 *
 * @author exch
 */
public interface SkinSourceSet extends Disposable {

    public boolean validate();

    public TextureRegion[] getImages(long time, MainState state);
}
