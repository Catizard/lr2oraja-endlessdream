package bms.player.beatoraja.modmenu.widget;

import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinHeader;
import bms.player.beatoraja.skin.json.JSONSkinLoader;
import bms.player.beatoraja.skin.json.JsonSkin;

import java.util.List;

/**
 * Holding meta data of registered resources of a skin
 */
public record SkinResources(
		// Should be avoided using this reference
		Skin skinRef,
		JSONSkinLoader skinLoader,
		SkinHeader skinHeader,
		List<JsonSkin.Font> skinFonts
) {
	public static SkinResources createSkinResources(Skin skin) {
		return new SkinResources(
				skin,
				skin.getLoader(),
				skin.header,
				skin.getLoader().getSkinFonts()
		);
	}
}
