package model;

import enums.Symbol;
import strategy.PlayerStrategy;

public record Player (String name, Symbol symbol, PlayerStrategy strategy) {
}
