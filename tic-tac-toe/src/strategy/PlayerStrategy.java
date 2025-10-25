package strategy;

import model.Board;
import model.Position;

public interface PlayerStrategy {
    Position makeMove(Board board);
}
