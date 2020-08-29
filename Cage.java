import java.util.*;

public class Cage {

    private int target;
    private String operation;
    private ArrayList<Entry> entries;



    public Cage(int target, String operation) {
        this.target = target;
        this.operation = operation;
        this.entries = new ArrayList<Entry>();
    }
    public Cage() {
        this.entries = new ArrayList<Entry>();
    }

    public void addEntry(Entry entry) {
        this.entries.add(entry);
        entry.assignGroup(this);
    }

    public ArrayList<Entry> getEntries() {
        return this.entries;
    }

    public String getOperation() {
        return this.operation;
    }

    public int getTarget() {
        return this.target;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    } 

    public void setTarget(int target) {
        this.target = target;
    }
    public void setDisplayable() {
        Entry t = this.entries.get(0);
        for(int i = 0; i < this.entries.size(); i++) {
            if(this.entries.get(i).getX() < t.getX()) {
                t = this.entries.get(i);
            }
            else if(this.entries.get(i).getX() == t.getX() && this.entries.get(i).getY() < t.getY()) {
                t = this.entries.get(i);
            } 
        }
        t.setOnDisplay();
    }

    public Entry getMaxElement() {
        //int maxValue;
        Entry maxEntry;
        maxEntry = this.entries.get(0);
        for(int i = 1; i < this.entries.size(); i++) {
            if(this.entries.get(i).getValue() > maxEntry.getValue()) {
                maxEntry = this.entries.get(i);
            }
        }
        return maxEntry;
    }

    public boolean checkSatisfied() {
        if(this.operation.equals("+")) {
            int sumElements = 0;
            for(int i = 0; i < this.entries.size(); i++) {
                sumElements += this.entries.get(i).getValue();
            }
            if(sumElements == this.target) {
                return true;
            }
            else {
                return false;
            }

        }

        else if(this.operation.equals("*")) {
            int productElements = 1;
            for(int i = 0; i < this.entries.size(); i++) {
                productElements *= this.entries.get(i).getValue();
            }
            if(productElements == this.target) {
                return true;
            }
            else {
                return false;
            }
        }

        else if(this.operation.equals("-")) {
            Entry maxEntry = this.getMaxElement();
            int product = this.target;
            for(int i = 0; i < this.entries.size(); i++) {
                if(this.entries.get(i) == maxEntry) {
                    continue;
                }

                if(this.entries.get(i).getValue() == 0) {
                    return false;
                }
                product += this.entries.get(i).getValue();
            }
            if(product == maxEntry.getValue()) {
                return true;
            }
            else {
                return false;
            }
        }

        else if(this.operation.equals("/")) {
            Entry maxEntry = this.getMaxElement();
            int product = this.target;
            for(int i = 0; i < this.entries.size(); i++) {
                if(this.entries.get(i) == maxEntry) {
                    continue;
                }

                product *= this.entries.get(i).getValue();
            }
            if(product == maxEntry.getValue()) {
                return true;
            }
            else {
                return false;
            }
        }

        else {
            if(this.getEntries().get(0).getValue() != this.target) {
                return false;
            }
            else {
                return true;
            }
        }
    }

} 