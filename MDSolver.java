/**
 * MathDoku Solver using mixed integer programming.
 * The MathDoku puzzle is presented as mixed integer
 * problem, then the model is build and is then solved
 * using ORTools, courtesy of Google
 */

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.*;

/**
 * MathDoku Solver class that solves the MathDoku puzzle by
 * making a mathematical model of the puzzle and solving it
 * as a mixed integer programming problem
 */

public class MDSolver {
    static {
        System.loadLibrary("jniortools");
    }

    int fieldSize;
    //ArrayList<Cage> groups;
    Field field;
    MPSolver solver;
    /// [row][col][val]
    MPVariable[][][] boolVars;
    /// [row][col]
    MPVariable[][] vars;
    private final double infinity = java.lang.Double.POSITIVE_INFINITY; 
    int[][] prefactorized;
    final int BIG_M = 15042000;
    private boolean solvedStatus;
    int[][] solutionValues;
    ArrayList<ArrayList<MPVariable>> auxi;

    public MDSolver(int fieldSize, Field field) {
        this.fieldSize = fieldSize;
        this.field = field;
        this.auxi = new ArrayList<ArrayList<MPVariable>>();
        this.solver = new MPSolver("Solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        this.boolVars = new MPVariable[this.field.getSize()][this.field.getSize()][this.field.getSize()];
        this.vars = new MPVariable[this.field.getSize()][this.field.getSize()];
        //this.auxilary = new ArrayList<MPVariable>();
        this.prefactorized = new int[this.fieldSize][this.fieldSize];
        this.factorizeTable();
    }

    public boolean checkSolved() {
        for(int i = 0; i < this.field.getGroups().size(); i++) {
            if(this.field.getGroups().get(i).checkSatisfied() == false) {
                return false;
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            ArrayList<Integer> rowValues = new ArrayList<Integer>();
            for(int j = 0; j < this.fieldSize; j++) {
                if(rowValues.contains(this.field.getField()[i][j].getValue())) {
                    return false;
                }
                rowValues.add(this.field.getField()[i][j].getValue());
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            ArrayList<Integer> rowValues = new ArrayList<Integer>();
            for(int j = 0; j < this.fieldSize; j++) {
                if(rowValues.contains(this.field.getField()[j][i].getValue())) {
                    return false;
                }
                rowValues.add(this.field.getField()[j][i].getValue());
            }
        }
        return true;
    }

    /**
     * Initialize the variables for the integer programming model
     */
    private void initializeVariables() {
        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                for(int k = 0; k < this.fieldSize; k++) {
                    this.boolVars[i][j][k] = this.solver.makeBoolVar("");
                }
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                this.vars[i][j] = this.solver.makeIntVar(1.0, (double)(this.fieldSize), "");
            }
        }
    }
    
    private void rowColValConstraints() {
        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, 1, "");
                MPConstraint constraint2 = this.solver.makeConstraint(1, this.infinity, "");
                
                // Sets the constraints, ensuring that each cell contains one value only
                for(int k = 0; k < this.fieldSize; k++) {
                    constraint1.setCoefficient(this.boolVars[i][j][k], 1);
                    constraint2.setCoefficient(this.boolVars[i][j][k], 1);
                }
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, 1, "");
                MPConstraint constraint2 = this.solver.makeConstraint(1, this.infinity, "");
                
                // Sets the constraints, ensuring that each row contains one instance of each value
                for(int k = 0; k < this.fieldSize; k++) {
                    constraint1.setCoefficient(this.boolVars[i][k][j], 1);
                    constraint2.setCoefficient(this.boolVars[i][k][j], 1);
                }
            }
        }

        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, 1, "");
                MPConstraint constraint2 = this.solver.makeConstraint(1, this.infinity, "");
                
                // Sets the constraints, ensuring that each column contains one instance of each value
                for(int k = 0; k < this.fieldSize; k++) {
                    constraint1.setCoefficient(this.boolVars[k][i][j], 1);
                    constraint2.setCoefficient(this.boolVars[k][i][j], 1);
                }
            }
        }

        /// Connecting the variables to the boolean values
        for(int i = 0; i < this.fieldSize; i++) {
            for(int j = 0; j < this.fieldSize; j++) {
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, 0, "");
                MPConstraint constraint2 = this.solver.makeConstraint(0, this.infinity, "");

                for(int k = 0; k < this.fieldSize; k++) {
                    constraint1.setCoefficient(this.boolVars[i][j][k], k + 1);
                    constraint2.setCoefficient(this.boolVars[i][j][k], k + 1);
                }
                constraint1.setCoefficient(this.vars[i][j], -1);
                constraint2.setCoefficient(this.vars[i][j], -1);
            }
        }
    }

    /**
     * Sets the group constraints for every group, based on the operation
     */
    private void setGroupContraints() {
        for(int i = 0; i < this.field.getGroups().size(); i++) {
            Cage group = this.field.getGroups().get(i);
            if(group.getOperation().equals("+")) {
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, group.getTarget(), "");
                MPConstraint constraint2 = this.solver.makeConstraint(group.getTarget(), this.infinity,  "");

                for(int j = 0; j < group.getEntries().size(); j++) {
                    constraint1.setCoefficient(this.vars[group.getEntries().get(j).getX()][group.getEntries().get(j).getY()], 1);
                    constraint2.setCoefficient(this.vars[group.getEntries().get(j).getX()][group.getEntries().get(j).getY()], 1);
                } 
            }

            else if(group.getOperation().equals("*")) {
                int[] factors = this.primeFactorization(group.getTarget());

                for(int j = 0; j < factors.length; j++) {
                    if(factors[j] != 0) {
                        MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, factors[j], "");
                        MPConstraint constraint2 = this.solver.makeConstraint(factors[j], this.infinity,  "");

                        for(int k = 0; k < this.fieldSize; k++) {
                            if((k + 1) % (j + 1) == 0 && k + 1 <= group.getTarget() && group.getTarget() % (k + 1) == 0) {
                                for(int p = 0; p < group.getEntries().size(); p++) {
                                    constraint1.setCoefficient(this.boolVars[group.getEntries().get(p).getX()][group.getEntries().get(p).getY()][k], this.prefactorized[k][j]);
                                    constraint2.setCoefficient(this.boolVars[group.getEntries().get(p).getX()][group.getEntries().get(p).getY()][k], this.prefactorized[k][j]);
                                }
                            }
                        }
                    }
                }
            }

            else if(group.getOperation().equals("-")) {
                ArrayList<MPVariable> auxilary = new ArrayList<MPVariable>();
                for(int j = 0; j < group.getEntries().size(); j++) {
                    MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, -group.getTarget(), "");
                    MPConstraint constraint2 = this.solver.makeConstraint(-group.getTarget(), this.infinity,  "");
                    for(int k = 0; k < group.getEntries().size(); k++) {
                        if(group.getEntries().get(j) != group.getEntries().get(k)) {
                            constraint1.setCoefficient(this.vars[group.getEntries().get(k).getX()][group.getEntries().get(k).getY()], 1);
                            constraint2.setCoefficient(this.vars[group.getEntries().get(k).getX()][group.getEntries().get(k).getY()], 1);
                        }
                        else {
                            constraint1.setCoefficient(this.vars[group.getEntries().get(k).getX()][group.getEntries().get(k).getY()], -1);
                            constraint2.setCoefficient(this.vars[group.getEntries().get(k).getX()][group.getEntries().get(k).getY()], -1);
                        }
                        
                    }
                    MPVariable aux = this.solver.makeBoolVar("");
                    constraint1.setCoefficient(aux, -this.BIG_M);
                    constraint2.setCoefficient(aux, this.BIG_M);
                    auxilary.add(aux);
                }
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, auxilary.size() - 1, "");
                MPConstraint constraint2 = this.solver.makeConstraint(auxilary.size() - 1, this.infinity,  "");
                for(int j = 0; j < auxilary.size(); j++) {
                    constraint1.setCoefficient(auxilary.get(j), 1);
                    constraint2.setCoefficient(auxilary.get(j), 1);
                }
                this.auxi.add(auxilary);
            }

            else if(group.getOperation().equals("/")) {
                
                ArrayList<MPVariable> auxilary = new ArrayList<MPVariable>();

                for(int j = 0; j < this.fieldSize; j++) {
                    MPVariable aux = this.solver.makeBoolVar("");
                    auxilary.add(aux);
                    for(int k = 0; k < this.fieldSize; k++) {
                        if(this.prefactorized[j][k] != 0) {

                            MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, this.prefactorized[j][k], "");
                            MPConstraint constraint2 = this.solver.makeConstraint(this.prefactorized[j][k], this.infinity,  "");

                            for(int t = 0; t < this.fieldSize; t++) {
                                if((t + 1) % (k + 1) == 0 && t <= j) {

                                    for(int p = 0; p < group.getEntries().size(); p++) {

                                        constraint1.setCoefficient(this.boolVars[group.getEntries().get(p).getX()][group.getEntries().get(p).getY()][t], this.prefactorized[t][k]);
                                        constraint2.setCoefficient(this.boolVars[group.getEntries().get(p).getX()][group.getEntries().get(p).getY()][t], this.prefactorized[t][k]);
                                    }
                                }
                            }
                            constraint1.setCoefficient(aux, -this.BIG_M);
                            constraint2.setCoefficient(aux, this.BIG_M);
                        }
                    }
                }

                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, auxilary.size() - 1, "");
                MPConstraint constraint2 = this.solver.makeConstraint(auxilary.size() - 1, this.infinity, "");

                for(int j = 0; j < auxilary.size(); j++) {
                    constraint1.setCoefficient(auxilary.get(j), 1);
                    constraint2.setCoefficient(auxilary.get(j), 1);
                }
            }

            else {
                MPConstraint constraint1 = this.solver.makeConstraint(-this.infinity, 1, "");
                MPConstraint constraint2 = this.solver.makeConstraint(1, this.infinity, "");
                constraint1.setCoefficient(this.boolVars[group.getEntries().get(0).getX()][group.getEntries().get(0).getY()][group.getTarget() - 1], 1);
                constraint2.setCoefficient(this.boolVars[group.getEntries().get(0).getX()][group.getEntries().get(0).getY()][group.getTarget() - 1], 1); 
            }
        }
    }

    /**
     * Returns the prime factorization of the number provided
     * @param target the number to be factorized into prime numbers
     * @return an array of integers, containing the number of times a number appears in the prime factorization
     */
    private int[] primeFactorization(int target) {
        int[] primes = new int[this.fieldSize];
        for(int i = 0; i < this.fieldSize; i++) {
            primes[i] = 0;
        }
        int rem = target;
        int q = 2;
        while(rem != 1) {
            if(rem % q == 0) {
                primes[q - 1]++;
                rem /= q;
            }
            else {
                q++;
            }
        }
        return primes;
    }

    /**
     * Creates the factorization table for all the numbers in the 
     * grid. This is used for the group constraints
     */
    private void factorizeTable() {
        for(int i = 0; i < this.fieldSize; i++) {
            if(i == 0) {
                this.prefactorized[0][i] = 1;
            }
            else {
                this.prefactorized[0][i] = 0;
            }
            
        }
        for(int i = 1; i < this.fieldSize; i++) {
            int[] factors = this.primeFactorization(i + 1);
            for(int j = 0; j < this.fieldSize; j++) {
                this.prefactorized[i][j] = factors[j];
            }
        }
    }

    /**
     * Solves the puzzle
     */
    public void solve() {
        this.initializeVariables();
        this.rowColValConstraints(); 
        this.setGroupContraints();

        final MPSolver.ResultStatus status = this.solver.solve();

        this.solutionValues = new int[this.fieldSize][this.fieldSize];
        if(status == MPSolver.ResultStatus.OPTIMAL) {
            this.solvedStatus = true;
            for(int i = 0; i < this.fieldSize; i++) {
                for(int j = 0; j < this.fieldSize; j++) {
                    solutionValues[i][j] = (int) this.vars[i][j].solutionValue();
                }
            }
            for(int i = 0; i < this.auxi.size(); i++) {
                for(int j = 0; j < this.auxi.get(i).size(); j++) {
                }
            }
        }
        else {
            this.solvedStatus = false;
        }
    }

    public boolean isSolved() {
        return this.solvedStatus;
    }

    public int[][] getSolutionValues() {
        return this.solutionValues;
    }
}