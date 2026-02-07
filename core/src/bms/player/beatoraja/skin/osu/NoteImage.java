package bms.player.beatoraja.skin.osu;

import org.ini4j.Profile;

import java.util.List;
import java.util.stream.Stream;

public record NoteImage(int lane, String note, String head, String tail) {
	public NoteImage(int lane, Profile.Section section) {
		this(
				lane,
				section.getOrDefault(noteImage(lane), String.format("mania-note%d", lane)),
				section.get(headImage(lane), String.format("mania-note%dH", lane)),
				section.get(tailImage(lane), String.format("mania-note%dL", lane))
		);
	}

	public static List<String> mentionedFiles(List<NoteImage> noteImages) {
		return noteImages.stream().flatMap(noteImage -> Stream.of(
				noteImage.note,
				noteImage.head,
				noteImage.tail
		)).distinct().toList();
	}

	private static String noteImage(int lane) {
		return String.format("NoteImage%d", lane);
	}

	private static String headImage(int lane) {
		return String.format("NoteImage%dH", lane);
	}

	private static String tailImage(int lane) {
		return String.format("NoteImage%dL", lane);
	}
}
