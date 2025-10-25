package model;

import enums.Symbol;
import state.GameContext;
import state.concrete.DrawState;

import java.util.Arrays;

public class Board {
    private final int size;
    private final Symbol[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Symbol[size][size];
        for (Symbol[] row : grid)
            Arrays.fill(row, Symbol.EMPTY);
    }

    public boolean validMove(Position move) {
        return move.x() >= 0 && move.x() < size
                && move.y() >= 0 && move.y() < size
                && grid[move.x()][move.y()] == Symbol.EMPTY;
    }

    public void move(Position move, Symbol symbol) {
        grid[move.x()][move.y()] = symbol;
    }

    public void checkGameState(GameContext context, Player currentPlayer) {
        // Check for a win
        if (isWinner(currentPlayer.symbol())) {
            context.next(currentPlayer, true);
            return;
        }

        // Check for a draw
        if (isFull()) {
            context.setCurrentState(new DrawState());
        }
    }

    private boolean isWinner(Symbol symbol) {
        // Row and column checks
        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            for (int j = 0; j < size; j++) {
                if (grid[i][j] != symbol) {
                    rowWin = false;
                }
                if (grid[j][i] != symbol) {
                    colWin = false;
                }
            }
            if (rowWin || colWin) {
                return true;
            }
        }

        // Diagonal checks
        boolean diag1Win = true;
        boolean diag2Win = true;
        for (int i = 0; i < size; i++) {
            if (grid[i][i] != symbol) {
                diag1Win = false;
            }
            if (grid[i][size - 1 - i] != symbol) {
                diag2Win = false;
            }
        }
        return diag1Win || diag2Win;
    }

    private boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == Symbol.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Symbol symbol = grid[i][j];
                switch (symbol) {
                    case X:
                        System.out.print(" X ");
                        break;
                    case O:
                        System.out.print(" O ");
                        break;
                    case EMPTY:
                    default:
                        System.out.print(" . ");
                }

                if (j < size - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i < size - 1) {
                System.out.println("---+---+---");
            }
        }
        System.out.println();
    }
}
