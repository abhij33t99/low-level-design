package service;

import enums.Symbol;
import model.Board;
import model.Player;
import model.Position;
import state.GameContext;
import state.GameState;
import state.concrete.OWonState;
import state.concrete.XWonState;
import strategy.PlayerStrategy;

public class TicTacToeGame {
    private final Board board;
    private final Player playerX;
    private final Player playerO;
    private Player currentPlayer;
    private final GameContext gameContext;

    public TicTacToeGame(String xName, PlayerStrategy xStrategy, String oName, PlayerStrategy oStrategy, int boardSize) {
        this.playerX = new Player(xName, Symbol.X, xStrategy);
        this.playerO = new Player(oName, Symbol.O, oStrategy);
        currentPlayer = playerX;
        this.board = new Board(boardSize);
        gameContext = new GameContext();
    }

    public void play() {
        do {
            board.printBoard();
            Position position = currentPlayer.strategy().makeMove(board);
            board.move(position, currentPlayer.symbol());
            board.checkGameState(gameContext, currentPlayer);
            switchPlayer();
        } while (!gameContext.isGameOver());
        announceResult();
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer == playerX ? playerO : playerX;
    }

    private void announceResult() {
        GameState state = gameContext.getCurrentState();
        board.printBoard();
        if (state instanceof XWonState) {
            System.out.printf("Player %s wins!\n", playerX.name());
        } else if (state instanceof OWonState) {
            System.out.printf("Player %s wins!\n", playerO.name());
        } else {
            System.out.println("It's a draw!");
        }
    }
}
