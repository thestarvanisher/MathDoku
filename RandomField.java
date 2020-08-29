import java.util.*;

public class RandomField {
    
    private int fieldSize;
    private int difficulty;
    Entry[][] entries;
    int[][] grid;
    Field field;
    int maxCellsPerGroup;


    public RandomField(int fieldSize, int difficulty) {
        this.fieldSize = fieldSize;
        this.difficulty = difficulty;
        this.entries = new Entry[this.fieldSize][this.fieldSize];
        this.grid = new int[fieldSize][fieldSize];
        this.field = new Field(this.fieldSize);

        if(difficulty == 1) {
            this.maxCellsPerGroup = 3;
        }
        else if(difficulty == 2) {
            this.maxCellsPerGroup = 4;
        }
        else {
            this.maxCellsPerGroup = 6;
        }
    }

    private boolean makeGrid(int x, int y) {
        ArrayList<Integer> row = new ArrayList<Integer>();
        ArrayList<Integer> column = new ArrayList<Integer>();
        ArrayList<Integer> possible = new ArrayList<Integer>();
        for(int i = 0; i < x; i++) {
            column.add(this.grid[i][y]);
        }
        for(int i = 0; i < y; i++) {
            row.add(this.grid[x][i]);
        }
        column.addAll(row);
        for(int i = 0; i < this.fieldSize; i++) {
            if(!column.contains(i + 1)) {
                possible.add(i + 1);
            }
        }
        if(x == this.fieldSize - 1 && y == this.fieldSize - 1 && possible.size() == 1) {
            this.grid[x][y] = possible.get(0);
            return true;
        } 
        if(possible.size() == 0) {
            return false;
        }
        Collections.shuffle(possible);
        boolean flag = false;
        for(int j = 0; j < possible.size(); j++) {
            this.grid[x][y] = possible.get(j);
            if(y + 1 > this.fieldSize - 1) {
                flag = this.makeGrid(x + 1, 0);
            }
            else {
                flag = this.makeGrid(x, y + 1);
            }
            if(flag == false) {
                continue;
            }
            else {
                break;
            }
        }
        if(flag == false) {
            return false;
        }
        return true;
    }

    private Cage makeAGroup(int x, int y, int l, Cage group) {
        if(l == 0) {
            return group;
        }
        
        Entry e = new Entry(x, y, this.grid[x][y]);
        group.addEntry(e);
        e.assignGroup(group);
        this.field.setFieldEntry(x, y, e);
        ArrayList<ArrayList<Integer>> directions = new ArrayList<ArrayList<Integer>>();

        if(x + 1 < fieldSize && this.field.getField()[x + 1][y] == null)  {
            directions.add(new ArrayList<Integer>());
            directions.get(directions.size() - 1).add(x + 1);
            directions.get(directions.size() - 1).add(y);
        }
        if(y + 1 < fieldSize && this.field.getField()[x][y + 1] == null) {
            directions.add(new ArrayList<Integer>());
            directions.get(directions.size() - 1).add(x);
            directions.get(directions.size() - 1).add(y + 1);
        }
        if(x - 1 >= 0 && this.field.getField()[x - 1][y] == null) {
            directions.add(new ArrayList<Integer>());
            directions.get(directions.size() - 1).add(x - 1);
            directions.get(directions.size() - 1).add(y);
        }
        if(y - 1 >= 0 && this.field.getField()[x][y - 1] == null) {
            directions.add(new ArrayList<Integer>());
            directions.get(directions.size() - 1).add(x);
            directions.get(directions.size() - 1).add(y - 1);
        }
        if(directions.size() == 0) {
            return group;
        }

        Random random = new Random();

        double k = (1.0 / directions.size());
        float rnd = random.nextFloat();
        int index = (int)(rnd/k);
        return this.makeAGroup(directions.get(index).get(0), directions.get(index).get(1), l - 1, group);
    } 

    private void assignDataToGroups() {
        for(int i = 0; i < this.field.getGroups().size(); i++) {
            this.field.getGroups().get(i).setDisplayable();
            if(this.field.getGroups().get(i).getEntries().size() == 1) {
                this.field.getGroups().get(i).setOperation("");
                
                this.field.getGroups().get(i).setTarget(this.field.getGroups().get(i).getEntries().get(0).getValue());
                continue;
            }
            
            Entry mx = this.field.getGroups().get(i).getMaxElement();
            Entry minx;

            ArrayList<String> operations = new ArrayList<String>();
            ArrayList<Integer> target = new ArrayList<Integer>();
            
            // Sum
            int sums = 0;
            minx = this.field.getGroups().get(i).getEntries().get(0);
            for(int j = 0; j < this.field.getGroups().get(i).getEntries().size(); j++) {
                sums += this.field.getGroups().get(i).getEntries().get(j).getValue();
                if(this.field.getGroups().get(i).getEntries().get(j).getValue() < minx.getValue()) {
                    minx = this.field.getGroups().get(i).getEntries().get(j);
                }
            }
            operations.add("+");
            target.add(sums);

            int prod = 1;
            for(int j = 0; j < this.field.getGroups().get(i).getEntries().size(); j++) {
                prod *= this.field.getGroups().get(i).getEntries().get(j).getValue();
            }
            operations.add("*");
            target.add(prod);

            int div = 1;
            for(int j = 0; j < this.field.getGroups().get(i).getEntries().size(); j++) {
                if(this.field.getGroups().get(i).getEntries().get(j) == mx) {
                    continue;
                }
                div *= this.field.getGroups().get(i).getEntries().get(j).getValue();
            }
            if(mx.getValue() % div == 0) {
                operations.add("/");
                target.add(mx.getValue() / div);
            }

            int diff = 0;
            for(int j = 0; j < this.field.getGroups().get(i).getEntries().size(); j++) {
                if(this.field.getGroups().get(i).getEntries().get(j) == mx) {
                    continue;
                }
                diff += this.field.getGroups().get(i).getEntries().get(j).getValue();
            }
            if(mx.getValue() - diff >= 0) {
                operations.add("-");
                target.add(mx.getValue() - diff);
            }

            Random random = new Random();
            double k = (1.0 / operations.size());
            float rnd = random.nextFloat();
            int index = (int)(rnd/k);
            this.field.getGroups().get(i).setOperation(operations.get(index));
            this.field.getGroups().get(i).setTarget(target.get(index));
        }
    }

    public void makeGroups() {
        Random random = new Random();
        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                if(this.field.getField()[i][j] == null) {
                    Cage g = new Cage();
                    g = this.makeAGroup(i, j, random.nextInt(this.maxCellsPerGroup) + 1, g);
                    this.field.addGroup(g);
                }
            }
        }
    }

    public void createRandomField() {
        this.makeGrid(0, 0);

        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
            }
        }

        this.makeGroups();
        this.assignDataToGroups();

        for(int i = 0; i < this.field.getGroups().size(); i++) {
            for(int j = 0; j < this.field.getGroups().get(i).getEntries().size(); j++) {
            }
        }
    }

    public Field getRandomField() {
        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                this.field.getField()[i][j].updateValue(0);
            }
        }
        this.field.generated_solution = this.grid;
        return this.field;
    }
}