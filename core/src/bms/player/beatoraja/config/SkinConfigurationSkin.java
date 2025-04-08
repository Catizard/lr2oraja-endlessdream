package bms.player.beatoraja.config;

import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinHeader;

public class SkinConfigurationSkin extends Skin {

    private String[] sampleBMS = {};
    private int defaultSkinType = 0;
    private int customPropertyCount = -1;
    private int customOffsetStyle = 0;

    public SkinConfigurationSkin(SkinHeader header) {
        super(header);
    }

    public String[] getSampleBMS() {
        return sampleBMS;
    }

    public void setSampleBMS(String[] sampleBMS) {
        this.sampleBMS = sampleBMS;
    }

    public int getDefaultSkinType() {
        return defaultSkinType;
    }

    public void setDefaultSkinType(int defaultSkinType) {
        this.defaultSkinType = defaultSkinType;
    }

    public int getCustomOffsetStyle() {
        return customOffsetStyle;
    }

    public void setCustomOffsetStyle(int customOffsetStyle) {
        this.customOffsetStyle = customOffsetStyle;
    }

    public int getCustomPropertyCount() {
        return customPropertyCount;
    }

    public void setCustomPropertyCount(int count) {
        this.customPropertyCount = count;
    }
}
