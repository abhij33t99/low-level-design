package state.concrete;

import model.Player;
import state.GameContext;
import state.GameState;

public class DrawState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        // The game is over, no next state
    }

    @Override
    public boolean isGameOver() {
        return true;
    }
}
