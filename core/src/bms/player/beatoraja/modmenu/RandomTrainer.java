package bms.player.beatoraja.modmenu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.logging.Logger;

public class RandomTrainer {
    private static final ArrayList<Boolean> laneMask = new ArrayList<>(Arrays.asList(false, false, false, false, false, false, false));
    private static final ArrayDeque<RandomHistoryEntry> laneOrderHistory = new ArrayDeque<RandomHistoryEntry>();
    private static String laneOrder = "1234567";
    private static boolean blackWhitePermute;
    private static boolean active;
    private static HashMap<Integer, Long> randomSeedMap;

    public RandomTrainer() {
        if (randomSeedMap == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                ObjectInputStream ois = new ObjectInputStream(cl.getResourceAsStream("resources/randomtrainer.dat"));

                randomSeedMap = (HashMap<Integer, Long>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | NullPointerException | IOException ex) {
                Logger.getGlobal().severe("RandomTrainer: randomtrainer.dat corrupted or missing from jar");
                ex.printStackTrace();
            }
        }
    }

    public static String getLaneOrder() {
        if (blackWhitePermute) {
            List<Character> black = new ArrayList<Character>();
            List<Character> white = new ArrayList<Character>();
            laneOrder.chars()
                    .mapToObj(Character::toChars)
                    .map(cs -> cs[0])
                    .forEach(c -> {
                        if ((Character.getNumericValue(c) % 2) == 0) {
                            black.add(c);
                        } else {
                            white.add(c);
                        }
                    });
            Collections.shuffle(black);
            Collections.shuffle(white);

            StringBuilder newLaneOrder = new StringBuilder(laneOrder);
            for (int i = 0; i < laneOrder.length(); i++) {
                char current = laneOrder.charAt(i);
                if ((Character.getNumericValue(current) % 2) == 0) {
                    newLaneOrder.setCharAt(i, black.remove(0));
                } else {
                    newLaneOrder.setCharAt(i, white.remove(0));
                }
            }
            laneOrder = newLaneOrder.toString();
        }
        return laneOrder;
    }

    public static void setLaneOrder(String number) {
        laneOrder = number;
    }

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        RandomTrainer.active = active;
    }

    public static HashMap<Integer, Long> getRandomSeedMap() {
        return randomSeedMap;
    }

    public static void setBlackWhitePermute(boolean blackWhitePermute) {
        RandomTrainer.blackWhitePermute = blackWhitePermute;
    }

    public static ArrayDeque<RandomHistoryEntry> getRandomHistory() {
        return laneOrderHistory;
    }

    public static void addRandomHistory(RandomHistoryEntry histEntry) {
        laneOrderHistory.addFirst(histEntry);
    }


    public class RandomHistoryEntry {
        private String title;

        private String random;

        public RandomHistoryEntry(String title, String random) {
            this.title = title;
            this.random = random;
        }

        public String getTitle() {
            return title;
        }

        public String getRandom() {
            return random;
        }
    }
}
