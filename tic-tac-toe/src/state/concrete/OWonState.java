package state.concrete;

import model.Player;
import state.GameContext;
import state.GameState;

public class OWonState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {

    }

    @Override
    public boolean isGameOver() {
        return true;
    }
}
