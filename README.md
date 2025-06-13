# LR2oraja \~Endless Dream\~ OSX Fix

This fix patch is for mac user who suffering from launching game itself and incorrect sound play issue.

## Building from source


**MacOS(arm chip):**

```sh
./gradlew core:shadowJar -Dplatform=macos -Darch=aarch64
```

**MacOS(x86-64 chip):**

```sh
./gradlew core:shadowJar -Dplatform=macos
```

> :warning:
> I don't own a x86-64 mac, this patch is tested by Hezzle, see https://github.com/exch-bms2/beatoraja/issues/838#issuecomment-2799932462

This task will create a jar located in `dist/` that can be used with any working installation of the game.

## Why Using Endless Dream?

There are two issues for `arm chip` mac user. One is fail to lanuch the game because `vanilla oraja` has `x86` required dependency. Trying to launch the game with `arm jdk` would fail into ` dll unfound exceptions`.The common solution for this problem is to have a `x86 jdk`, which could launch the game but the sounds are broken.

While `Endless Dream` involes `gralde` as dependency management tool, which makes it easier to upgrade or change dependency for `arm chip` mac. This is the major thing this patch does.

## I Really Want To Play Vanilla Oraja

There is a way to play the old version of `vanilla oraja`. You need an old version of `oracle jdk8`, which has no `arm` download option but only `x64`. This apporach was found by accident so don't give it too much hope.Besides, higher version of `vanilla oraja` now requires `jdk17` to play.

## Known Issues

Cannot play video contents' color correctly. Seems to be a FFMPEG problem but no idea how to fix it currently. See main branch for temporary fix.

## Special Thanks

Thanks for Seraxis, the original `Endless Dream` author. This code patch cannot be applied without mature build tool invovling.
Thanks for Hezzle, who confirms that this code patch is working on `x86-64` macos.
