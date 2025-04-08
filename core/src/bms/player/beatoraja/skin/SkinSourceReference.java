package bms.player.beatoraja.skin;

import bms.player.beatoraja.MainState;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SkinSourceReference implements SkinSource {

    private final int id;

    public SkinSourceReference(int id) {
        this.id = id;
    }

    @Override
    public void dispose() {
    }

    public boolean validate() {
        return true;
    }

    @Override
    public TextureRegion getImage(long time, MainState state) {
        return state.getImage(id);
    }
}
