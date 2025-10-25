package factory;

import enums.RankingType;
import strategy.RatingBasedRanking;
import strategy.SlotRankingStrategy;
import strategy.TimeBasedRanking;

public class RankingStrategyFactory {
    public static SlotRankingStrategy getRankingStrategy(RankingType type) {
        if (type == null) {
            return new TimeBasedRanking();
        }
        return switch (type) {
            case TIME -> new TimeBasedRanking();
            case RATING -> new RatingBasedRanking();
        };
    }
}
