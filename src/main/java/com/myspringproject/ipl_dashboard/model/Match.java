package com.myspringproject.ipl_dashboard.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Match {
    @Id
    private long id;
    private String city;
    private LocalDate date;
    private String playerOfMatch;
    private String venue;

    @ManyToOne
    @JoinColumn(name = "team1_id", referencedColumnName = "id")
    private Team team1;
    @ManyToOne
    @JoinColumn(name = "team2_id", referencedColumnName = "id")
    private Team team2;
    private String tossWinner;
    private String tossDecision;
    @ManyToOne
    @JoinColumn(name = "match_winner_id", referencedColumnName = "id")
    private Team matchWinner;
    private String result;
    private String resultMargin;
    private String umpire1;
    private String umpire2;

        @JsonProperty("team1") // Tells Jackson to use this method for the JSON property named "team1"
    public Object getTeam1Json() { // Renamed to avoid direct method name conflict with JPA getter
        if (this.team1 == null) return null;
        return new Object() { // Create an anonymous object on the fly
            public long getId() { return team1.getId(); }
            public String getTeamName() { return team1.getTeamName(); }
        };
    }

    @JsonProperty("team2") // Similarly for team2
    public Object getTeam2Json() {
        if (this.team2 == null) return null;
        return new Object() {
            public long getId() { return team2.getId(); }
            public String getTeamName() { return team2.getTeamName(); }
        };
    }

    @JsonProperty("matchWinner") // And for matchWinner
    public Object getMatchWinnerJson() {
        if (this.matchWinner == null) return null;
        return new Object() {
            public long getId() { return matchWinner.getId(); }
            public String getTeamName() { return matchWinner.getTeamName(); }
        };
    }
    // --- END CUSTOM GETTERS FOR JSON ---


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPlayerOfMatch() {
        return playerOfMatch;
    }

    public void setPlayerOfMatch(String playerOfMatch) {
        this.playerOfMatch = playerOfMatch;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
    @JsonIgnore
    public Team getTeam1() {
        return team1;
    }

    public void setTeam1(Team team1) {
        this.team1 = team1;
    }
    @JsonIgnore
    public Team getTeam2() {
        return team2;
    }

    public void setTeam2(Team team2) {
        this.team2 = team2;
    }

    public String getTossWinner() {
        return tossWinner;
    }

    public void setTossWinner(String tossWinner) {
        this.tossWinner = tossWinner;
    }

    public String getTossDecision() {
        return tossDecision;
    }

    public void setTossDecision(String tossDecision) {
        this.tossDecision = tossDecision;
    }

    @JsonIgnore
    public Team getMatchWinner() {
        return matchWinner;
    }

    public void setMatchWinner(Team matchWinner) {
        this.matchWinner = matchWinner;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultMargin() {
        return resultMargin;
    }

    public void setResultMargin(String resultMargin) {
        this.resultMargin = resultMargin;
    }

    public String getUmpire1() {
        return umpire1;
    }

    public void setUmpire1(String umpire1) {
        this.umpire1 = umpire1;
    }

    public String getUmpire2() {
        return umpire2;
    }

    public void setUmpire2(String umpire2) {
        this.umpire2 = umpire2;
    }

}
