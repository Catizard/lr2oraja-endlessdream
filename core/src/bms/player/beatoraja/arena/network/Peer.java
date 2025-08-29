package bms.player.beatoraja.arena.network;

import org.msgpack.value.ArrayValue;
import org.msgpack.value.Value;

public class Peer {
    private String userName;
    private String selectedMD5;
    private boolean ready;
    private Score score;
    private int option;
    private int gague;

    public Peer() {

    }

    public Peer(Value value) {
        ArrayValue arr = value.asArrayValue();
        this.userName = arr.get(0).asStringValue().asString();
        this.selectedMD5 = arr.get(1).asStringValue().asString();
        this.ready = arr.get(2).asBooleanValue().getBoolean();
        this.score = new Score(arr.get(3));
        this.option = arr.get(4).asIntegerValue().toInt();
        this.gague = arr.get(5).asIntegerValue().toInt();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSelectedMD5() {
        return selectedMD5;
    }

    public void setSelectedMD5(String selectedMD5) {
        this.selectedMD5 = selectedMD5;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public int getGague() {
        return gague;
    }

    public void setGague(int gague) {
        this.gague = gague;
    }

    public int getExScore() {
        return this.score.getpGreat() * 2 + this.score.getGreat();
    }

    public int getBP() {
        return this.score.getBad() + this.score.getPoor();
    }

    public int getMaxCombo() {
        return this.score.getMaxCombo();
    }

    public float getRate() {
        return this.getScore().getCurrentNotes() == 0
                ? 0f
                : this.getExScore() * 50f / this.getScore().getCurrentNotes();
    }
}
