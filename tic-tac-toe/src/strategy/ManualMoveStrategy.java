package strategy;

import model.Board;
import model.Position;

import java.util.Scanner;

public class ManualMoveStrategy implements PlayerStrategy{
    private final Scanner scanner;
    private final String playerName;

    public ManualMoveStrategy(String playerName) {
        this.scanner = new Scanner(System.in);
        this.playerName = playerName;
    }


    @Override
    public Position makeMove(Board board) {
        while (true) {
            System.out.printf("%s, Enter your move: row [0-2] and col [0-2]\n", playerName);

            try {
                int row = scanner.nextInt();
                int col = scanner.nextInt();
                Position move = new Position(row, col);
                if (board.validMove(move))
                    return move;

                System.out.println("Invalid move. Try again!");
            } catch (Exception e) {
                System.out.println("Invalid input. Try again!");
                scanner.nextLine();
            }
        }
    }
}
