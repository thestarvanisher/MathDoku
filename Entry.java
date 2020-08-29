public class Entry {

    private int x, y;
    private int value;
    private Cage group;
    private boolean display;

    public Entry(int x, int y) {
        this.x = x;
        this.y = y;
        this.display = false;
    }

    public Entry(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.display = false;
    }

    public void updateValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;        
    }

    public void assignGroup(Cage group) {
        this.group = group;
    }
    
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Cage getGroup() {
        return this.group;
    }

    public void setOnDisplay() {
        this.display = true;
    }

    public boolean isDisplayable() {
        return this.display;
    }
}