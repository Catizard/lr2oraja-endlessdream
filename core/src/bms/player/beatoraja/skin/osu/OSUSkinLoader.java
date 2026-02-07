package bms.player.beatoraja.skin.osu;

import bms.player.beatoraja.Config;
import bms.player.beatoraja.MainState;
import bms.player.beatoraja.SkinConfig;
import bms.player.beatoraja.skin.Skin;
import bms.player.beatoraja.skin.SkinHeader;
import bms.player.beatoraja.skin.SkinType;
import bms.player.beatoraja.skin.json.JSONSkinLoader;
import bms.player.beatoraja.skin.json.JsonSkin;
import bms.player.beatoraja.skin.lua.SkinLuaAccessor;
import bms.player.beatoraja.skin.property.TimerPropertyFactory;
import bms.tool.util.Pair;
import com.badlogic.gdx.utils.ObjectMap;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OSUSkinLoader extends JSONSkinLoader {
	private final Logger logger = LoggerFactory.getLogger(OSUSkinLoader.class);
	private Function<String, String> orEmpty = (s) -> s == null || s.isEmpty() ? "" : s;

	public OSUSkinLoader() {
		super(new SkinLuaAccessor(false));
	}

	public OSUSkinLoader(MainState state, Config c) {
		super(state, c, new SkinLuaAccessor(false));
	}

	@Override
	public SkinHeader loadHeader(Path p) {
		SkinHeader header = null;
		try {
			sk = loadSkinArchitecture(p);
			header = loadJsonSkinHeader(sk, p);
			header.setType(SkinHeader.TYPE_OSU);
		} catch (Throwable e) {
			logger.error("Error loading osu skin's header", e);
		}
		return header;
	}

	@Override
	public Skin load(Path p, SkinType type, SkinConfig.Property property) {
		Skin skin = null;
		SkinHeader header = loadHeader(p);
		if (header == null) {
			return null;
		}

		filemap = new ObjectMap<>();
		sk = loadSkinArchitecture(p);
		skin = loadJsonSkin(header, sk, type, property, p);
		return skin;
	}

	private JsonSkin.Skin loadSkinArchitecture(Path p) {
		JsonSkin.Skin sk = new JsonSkin.Skin();
		sk.w = 640;
		sk.h = 480;
		Ini ini = null;
		List<Path> realFiles = null;
		try {
			List<String> lines = new BufferedReader(new FileReader(p.toFile())).lines().toList();
			ini = new Ini();
			org.ini4j.Config config = new org.ini4j.Config();
			config.setMultiSection(true);
			ini.setConfig(config);
			ini.load(new StringReader(lines.stream().filter(line -> !line.startsWith("//")).collect(Collectors.joining(System.lineSeparator()))));
			realFiles = Files.list(p.getParent()).toList();
		} catch (IOException e) {
			logger.error("Error loading osu skin", e);
		}
		if (ini == null) {
			return null;
		}
		List<JsonSkin.Destination> destinations = new ArrayList<>();
		Set<String> realFileNames = realFiles.stream().map(realFile -> realFile.getFileName().toString()).collect(Collectors.toSet());
		// [GENERAL]
		Ini.Section general = ini.get("General");
		String name = orEmpty.apply(general.get("Name"));
		String author = orEmpty.apply(general.get("Author"));
		sk.name = name;
		sk.author = author;
		// [Mania]
		sk.note = new JsonSkin.NoteSet();
		sk.note.id = "notes";
		List<Ini.Section> maniaSections = ini.getAll("Mania");
		for (Ini.Section maniaSection : maniaSections) {
			// TODO: How do we splits out the different key modes
			String keys = maniaSection.get("Keys");
			sk.type = SkinType.PLAY_7KEYS.getId();
			if (!"8".equals(keys)) {
				continue;
			}

			int columnStart = Integer.parseInt(maniaSection.getOrDefault("ColumnStart", "136"));
			int hitPosition = Integer.parseInt(maniaSection.getOrDefault("HitPosition", "402"));
			List<Integer> columnWidths = Arrays.stream(maniaSection.getOrDefault("ColumnWidth", "30").split(",")).map(Integer::parseInt).collect(Collectors.toList());
			while (columnWidths.size() < 8) {
				columnWidths.add(30);
			}
			int stageWidth = columnWidths.stream().mapToInt(a -> a).sum();
			int heightScale = Integer.parseInt(maniaSection.getOrDefault("WidthForNoteHeightScale", columnWidths.stream().mapToInt(a -> a).min().toString()));
			sk.note.heightScale = 0.01F * heightScale;

			// Stage hint
			String stageHint = maniaSection.getOrDefault("StageHint", "mania-stage-hint");
			destinations.add(new JsonSkin.Destination() {{
				id = "stage-hint";
				dst = new JsonSkin.Animation[]{
						new JsonSkin.Animation() {{
							x = columnStart;
							y = 480 - hitPosition;
							w = stageWidth;
							h = 20;
						}}
				};
			}});

			// Hit image
			String hit0 = maniaSection.getOrDefault("Hit0", "mania-hit0");
			String hit50 = maniaSection.getOrDefault("Hit50", "mania-hit50");
			String hit100 = maniaSection.getOrDefault("Hit100", "mania-hit100");
			String hit200 = maniaSection.getOrDefault("Hit200", "mania-hit200");
			String hit300 = maniaSection.getOrDefault("Hit300", "mania-hit300");
			String hit300g = maniaSection.getOrDefault("Hit300g", "mania-hit300g");

			// Hit position
			int scorePosition = 480 - Integer.parseInt(maniaSection.getOrDefault("ScorePosition", "480"));

			Function<String, JsonSkin.Destination> newDestination = dstID -> new JsonSkin.Destination() {{
				id = dstID;
				loop = -1;
				timer = TimerPropertyFactory.getTimerProperty(46);
				dst = new JsonSkin.Animation[]{
						new JsonSkin.Animation() {{
							time = 0;
							x = columnStart + stageWidth / 2;
							y = scorePosition;
							w = -1;
							h = -1;
						}},
						new JsonSkin.Animation() {{
							time = 500;
						}}
				};
			}};
			sk.judge = new JsonSkin.Judge[]{
					new JsonSkin.Judge() {{
						id = "judge";
						index = 0;
						images = new JsonSkin.Destination[]{
								newDestination.apply("judge-pg"),
								newDestination.apply("judge-gr"),
								newDestination.apply("judge-gd"),
								newDestination.apply("judge-bd"),
								newDestination.apply("judge-pr"),
								newDestination.apply("judge-ms"),
						};

						numbers = new JsonSkin.Destination[] {

						};

						shift = true;
					}}
			};
			destinations.add(new JsonSkin.Destination() {{
				id = "judge";
			}});

			List<NoteImage> noteImages = IntStream.range(0, 8).mapToObj(lane -> new NoteImage(lane, maniaSection)).toList();
			List<String> mentionedFiles = NoteImage.mentionedFiles(noteImages);
			if (mentionedFiles.contains(null)) {
				throw new IllegalArgumentException("Some of the note image files are not defined");
			}
			List<Pair<String, String>> matchedFiles = Stream.concat(
					mentionedFiles.stream().map(mentionedFile -> {
						if (realFileNames.contains(mentionedFile + ".png")) {
							return Pair.of(mentionedFile, mentionedFile + ".png");
						}
						if (realFileNames.contains(mentionedFile + "-0.png")) {
							return Pair.of(mentionedFile, mentionedFile + "-0.png");
						}
						throw new IllegalArgumentException("Unknown osu note file: " + mentionedFile);
					}),
					Stream.of(
							Pair.of("stage-hint", stageHint + ".png"),
							Pair.of("judge-pg", hit300g + ".png"),
							Pair.of("judge-gr", hit300 + ".png"),
							Pair.of("judge-gd", hit200 + ".png"),
							Pair.of("judge-bd", hit100 + ".png"),
							Pair.of("judge-pr", hit50 + ".png"),
							Pair.of("judge-ms", hit0 + ".png")
					)
			).toList();
			sk.filepath = matchedFiles.stream().map(mf -> {
				JsonSkin.Filepath filepath = new JsonSkin.Filepath();
				filepath.name = mf.getFirst();
				filepath.path = mf.getSecond();
				filepath.def = "Default";
				return filepath;
			}).toArray(JsonSkin.Filepath[]::new);
			sk.image = matchedFiles.stream().map(mf -> {
				JsonSkin.Image image = new JsonSkin.Image();
				image.id = mf.getFirst();
				image.src = mf.getFirst();
				image.x = 0;
				image.y = 0;
				image.w = -1;
				image.h = -1;
				return image;
			}).toArray(JsonSkin.Image[]::new);
			sk.source = matchedFiles.stream().map(mf -> {
				JsonSkin.Source source = new JsonSkin.Source();
				source.id = mf.getFirst();
				source.path = mf.getSecond();
				return source;
			}).toArray(JsonSkin.Source[]::new);

			// Notes
			destinations.add(new JsonSkin.Destination() {{
				id = "notes";
			}});
			sk.note.note = noteImages.stream().map(NoteImage::note).toArray(String[]::new);

			sk.note.lnstart = noteImages.stream().map(NoteImage::head).toArray(String[]::new);
			sk.note.lnbody = noteImages.stream().map(NoteImage::note).toArray(String[]::new);
			sk.note.lnactive = noteImages.stream().map(NoteImage::note).toArray(String[]::new);
			sk.note.lnend = noteImages.stream().map(NoteImage::tail).toArray(String[]::new);

			sk.note.hcnstart = noteImages.stream().map(NoteImage::head).toArray(String[]::new);
			sk.note.hcnbody = noteImages.stream().map(NoteImage::note).toArray(String[]::new);
			sk.note.hcnactive = noteImages.stream().map(NoteImage::note).toArray(String[]::new);
			sk.note.hcnend = noteImages.stream().map(NoteImage::tail).toArray(String[]::new);
			sk.note.hcndamage = noteImages.stream().map(NoteImage::note).toArray(String[]::new);
			sk.note.hcnreactive = noteImages.stream().map(NoteImage::note).toArray(String[]::new);

			sk.note.mine = noteImages.stream().map(NoteImage::note).toArray(String[]::new);

			sk.note.dst = IntStream.range(0, 8).mapToObj(lane -> new JsonSkin.Animation() {{
				// TODO: Correct this
				x = columnStart + lane * columnWidths.get(lane);
				y = 480 - hitPosition;
				w = columnWidths.get(lane);
				h = hitPosition;
			}}).toArray(JsonSkin.Animation[]::new);
			// TODO: Remove this 2p setup
			JsonSkin.Animation[] _2pDst = new JsonSkin.Animation[8];
			System.arraycopy(sk.note.dst, 0, _2pDst, 1, 7);
			_2pDst[0] = sk.note.dst[7];
			sk.note.dst = _2pDst;
		}
		sk.destination = destinations.toArray(new JsonSkin.Destination[0]);
		return sk;
	}
}
