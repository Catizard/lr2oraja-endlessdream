package bms.player.beatoraja.audio;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.nio.file.Path;

public record SevenZArchiveContext(Path path, SevenZFile sevenZFile, SevenZArchiveEntry sevenZArchiveEntry) {}
