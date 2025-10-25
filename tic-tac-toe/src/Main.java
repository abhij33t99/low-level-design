import service.TicTacToeGame;
import strategy.ManualMoveStrategy;
import strategy.PlayerStrategy;

void main(String[] args) {
    PlayerStrategy xStrategy = new ManualMoveStrategy("Alice");
    PlayerStrategy oStrategy = new ManualMoveStrategy("Bob");

    TicTacToeGame game = new TicTacToeGame("Alice", xStrategy, "Bob", oStrategy, 3);
    game.play();
}
