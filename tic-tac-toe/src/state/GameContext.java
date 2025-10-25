package state;

import model.Player;
import state.concrete.XTurnState;

public class GameContext {
    private GameState currentState;

    public GameContext(){
        this.currentState = new XTurnState();
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public void next(Player player, boolean hasWon) {
        currentState.next(this, player, hasWon);
    }

    public boolean isGameOver() {
        return currentState.isGameOver();
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
