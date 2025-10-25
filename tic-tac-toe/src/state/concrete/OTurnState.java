package state.concrete;

import enums.Symbol;
import model.Player;
import state.GameContext;
import state.GameState;

public class OTurnState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        GameState nextState;
        if(hasWon) {
            nextState = player.symbol() == Symbol.X ? new XWonState() : new OWonState();
        }else
            nextState = new XTurnState();
        context.setCurrentState(nextState);
    }

    @Override
    public boolean isGameOver() {
        return false;
    }
}
