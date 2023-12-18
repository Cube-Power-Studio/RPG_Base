package rpg.rpg_base.data;

public class PlayerData {
    private int enduranceLevel;
    private int level;
    private int sp;

    public int getEnduranceLevel(){
        return enduranceLevel;
    }
    public int getLevel(){
        return level;
    }
    public int getSp(){
        return sp;
    }
    public void setEnduranceLevel(int enduranceLevel){
        this.enduranceLevel = enduranceLevel;
    }
    public void setSP(int sp){
        this.sp = sp;
    }
    public void setLevel(int level){
        this.level = level;
    }
}
