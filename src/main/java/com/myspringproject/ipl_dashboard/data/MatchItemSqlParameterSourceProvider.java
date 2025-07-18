package com.myspringproject.ipl_dashboard.data;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource; // Use MapSqlParameterSource for more control

import com.myspringproject.ipl_dashboard.model.Match;

public class MatchItemSqlParameterSourceProvider extends BeanPropertyItemSqlParameterSourceProvider<Match> {

    @Override
    public SqlParameterSource createSqlParameterSource(Match item) {
        // Start with a MapSqlParameterSource, which is more flexible for custom values
        MapSqlParameterSource source = new MapSqlParameterSource();

        // Map basic properties directly
        source.addValue("id", item.getId());
        source.addValue("city", item.getCity());
        source.addValue("date", item.getDate());
        source.addValue("playerOfMatch", item.getPlayerOfMatch());
        source.addValue("venue", item.getVenue());
        source.addValue("tossWinner", item.getTossWinner()); // Assuming this is still String
        source.addValue("tossDecision", item.getTossDecision());
        source.addValue("result", item.getResult());
        source.addValue("resultMargin", item.getResultMargin());
        source.addValue("umpire1", item.getUmpire1());
        source.addValue("umpire2", item.getUmpire2());

        // Explicitly handle Team foreign keys
        // If the Team object is null, provide null for the ID parameter
        source.addValue("team1.id", (item.getTeam1() != null ? item.getTeam1().getId() : null));
        source.addValue("team2.id", (item.getTeam2() != null ? item.getTeam2().getId() : null));
        source.addValue("matchWinner.id", (item.getMatchWinner() != null ? item.getMatchWinner().getId() : null));

        return source;
    }
}