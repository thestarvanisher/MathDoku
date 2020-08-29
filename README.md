# MathDoku
MathDoku, known also under the name 'KenKen', is an interesting grid game. The rules are in a way similar to the Sudoku, however, you are also required to put a bit more thought into it as it requres you do simple maths. Like in the Sudoku,  you have to fill each of the columns and rows with the numbers 1 to n, where n is the length of the row/culumn (the field is a square grid). You also have cages which are separeated with a thicker grid. You are given a math operation and a target for the grid. The idea is to fill the grid with numbers so that when you apply the math operation on them, you will get the targeted number. The targets are integer numbers and the operations are the basic math operations - addition, multiplication, subtraction and division.<br>
You can read more about the game [here](https://en.wikipedia.org/wiki/KenKen).

## About the project
The game was originally planned for an university assignment. However, I came up with an idea for a fast solver and made a more user friendly GUI so I decided to publish it here. 

## The solver
The puzzle is solved as an Integer programming problem. The original idea could be found [here](https://www.scirp.org/journal/paperinformation.aspx?paperid=66317). However, I have expanded on that - in the paper they have limited the number of cells in a cage with division and subtraction operations to two. The model can now solve puzzles where the number of cells in such cages exceeds two. The way that is done is by constaining the model and using the reverse operations. In the future I might write a paper on it and explain it in more details. 

## About the other features of the game
The game has a main menu where you can access the Random Game menu or Load Game from File menu. There are also some basic instructions.
In the Random Game menu you can select the puzzle size - from 2x2 up to 8x8 and the difficulty.
In the Load Game from File menu you can load a puzzle from a text file or type a puzzle by hand in the designated area. The following format is used for that:
- Each line defines one cage of the puzzle
- The line starts with the target followed immediately by the arithmetic operator (or none if it's a single cell) for that cage
- This is followed by a space and a sequence of cell IDs that belong to the cage (consecutive cell IDs are separated by a comma). Here cells are numbered from 1 to (NxN), where 1 to N are the cells in the top row (left to right), N+1 to 2N are the cells in the second row from the top, and so on

Here is a puzzle example:
```
11+ 1,7
2÷ 2,3
20x 4,10
6x 5,6,12,18
3- 8,9
3÷ 11,17
240x 13,14,19,20
6x 15,16
6x 21,27
7+ 22,28,29
30x 23,24
6x 25,26
9+ 30,36
8+ 31,32,33
2÷ 34,35
```

### The random game generator
There are three difficult settings, each corresponding to different maximim number of cells in a cage. The random generator creates a latin square and then recursively creates the cages by putting cells into a cage. Once that is done, the random generator checks which operations could be assigned to a cage. The multiplication and addition are always possible because these are commutative operations. Since division and subtraction are not, you cannot always assign them to cages. Once the program determines the possible operations, it picks one at random with equal probability and assigns it to the cage.

### Mistake detection
There is a mistake detection that works when you click on the "Toggle Mistakes" button. It is automatic and paints the row, column or cage if it detects a mistake, for example having repeated a number in a row/column or the target is unreachable by applying the operation on the numbers.

### Solve
The "Solve" button calls the solver to solve the puzzle. If the puzzle was randomly generated, the solver is not invoked as the latin square is saved and is displayed. If the puzzle is entered by hand or from a file, the actual solver is called. It solves the puzzle. After the puzzle is solved, the "Solve" button turns to "Show Solution" button which can display the solution to the puzzle.

### Hint
Once the puzzle is solved, the "Hint" button is enabled. The user can then select a cell and clik on the button to reveal the solution value for 2 seconds.

### Undo, Redo, Clear
These are self-explanatory. The "Undo" button undoes the user action, the "Redo" button does the oppposite. The "Clear" button clears the board. This is a final operation, meaning that it cannot be undone.

### Other features
There are buttons for incereasing and decreasing the font size in the grid. Currently there are 3 font sizes. There are also buttons to enter cell values by clicking on them as opposed to using the keyboard.


## How to run?
The game has been written to run on Windows. It has not been tested on other platforms!

### Dependencies
The game requires:
- java-jdk - the game was developed in Java 13
- JavaFX - that was used for the GUI. It was developed in javafx-sdk version 11.0.2
- OR-Tools - they have been used to implement the MathDoku as an Integer programming problem as they have an Integer programming problem solver. You can download it from [here](https://developers.google.com/optimization/install).
- Microsoft Visual C++ Redistributable - it is required for the OR-Tools to run. Usually it is installed on most Windows machines. You can download it from [here](https://support.microsoft.com/en-us/help/2977003/the-latest-supported-visual-c-downloads).

### Running it
Once you have the all the required dependencies, you can open the CMD and navigate to the game folder. Assuming that the javafx folder is located on {J} fodler and the OR-Tools are in {O} folder, to build and run the game, type the following two lines in the CMD (Windows):
```
javac -encoding UTF-8 --module-path={O}/lib;{J}/lib --add-modules=ALL-MODULE-PATH MathDoku.java
java -Djava.library.path={O}/lib --module-path={O}/lib;{J}/lib --add-modules=ALL-MODULE-PATH MathDoku
```

That should run the game.<br><br>
To clear the folder from all `.class` files after you close the game, you can use the following command in CMD (Windows):
```
del *.class
```

I hope you like and enjoy the game!


**Note:** The OR-Tools software suite is licensed under the terms of the Apache License 2.0. Link to the GitHub repository and license [here](https://github.com/google/or-tools).

