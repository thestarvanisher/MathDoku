import java.util.*;

public class Field {

    private int fieldSize;
    private Entry[][] field;
    private ArrayList<Cage> groups;
    Entry selected;
    int[][] generated_solution;

    public Field(int fieldSize) {
        this.fieldSize = fieldSize;
        this.field = new Entry[fieldSize][fieldSize];
        this.groups = new ArrayList<Cage>();
    }

    public void addGroup(Cage group) {
        this.groups.add(group);
        for(int i = 0; i < group.getEntries().size(); i++) {
            this.field[group.getEntries().get(i).getX()][group.getEntries().get(i).getY()] = group.getEntries().get(i);
        }
    }

    public ArrayList<Cage> getGroups() {
        return this.groups;
    }

    public Entry[][] getField() {
        return this.field;
    }

    public int getSize() {
        return this.fieldSize;
    }

    public void setFieldEntry(int x, int y, Entry e) {
        this.field[x][y] = e;
    }

    public void setFieldValue(int x, int y, int value) {
        this.field[x][y].updateValue(value);
    }

    public boolean checkSolved() {
        for(int i = 0; i < this.getGroups().size(); i++) {
            if(this.getGroups().get(i).checkSatisfied() == false) {
                return false;
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            ArrayList<Integer> rowValues = new ArrayList<Integer>();
            for(int j = 0; j < this.fieldSize; j++) {
                if(rowValues.contains(this.getField()[i][j].getValue())) {
                    return false;
                }
                rowValues.add(this.getField()[i][j].getValue());
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            ArrayList<Integer> rowValues = new ArrayList<Integer>();
            for(int j = 0; j < this.fieldSize; j++) {
                if(rowValues.contains(this.getField()[j][i].getValue())) {
                    return false;
                }
                rowValues.add(this.getField()[j][i].getValue());
            }
        }

        return true;
    }

    public ArrayList<Object> returnMistakes() {
        ArrayList<Cage> problemCage = new ArrayList<Cage>();
        ArrayList<Integer> rowProblem = new ArrayList<Integer>();
        ArrayList<Integer> columnProblem = new ArrayList<Integer>();


        for(int i = 0; i < this.getGroups().size(); i++) {
            if(this.getGroups().get(i).checkSatisfied() == false) {
                problemCage.add(this.getGroups().get(i));
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            ArrayList<Integer> rowValues = new ArrayList<Integer>();
            for(int j = 0; j < this.fieldSize; j++) {
                if(rowValues.contains(this.getField()[i][j].getValue()) && this.getField()[i][j].getValue() != 0) {
                    rowProblem.add(i);
                    break;
                }
                rowValues.add(this.getField()[i][j].getValue());
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            ArrayList<Integer> rowValues = new ArrayList<Integer>();
            for(int j = 0; j < this.fieldSize; j++) {
                if(rowValues.contains(this.getField()[j][i].getValue()) && this.getField()[j][i].getValue() != 0) {
                    columnProblem.add(i);
                    break;
                }
                rowValues.add(this.getField()[j][i].getValue());
            }
        }
        ArrayList<Object> keeper = new ArrayList<Object>();
        keeper.add(problemCage);
        keeper.add(rowProblem);
        keeper.add(columnProblem);

        return keeper;
    }
}