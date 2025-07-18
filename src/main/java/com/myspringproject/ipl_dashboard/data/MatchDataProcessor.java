package com.myspringproject.ipl_dashboard.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myspringproject.ipl_dashboard.model.Match;
import com.myspringproject.ipl_dashboard.model.Team;
import com.myspringproject.ipl_dashboard.repository.TeamRepository;

@Component
public class MatchDataProcessor implements ItemProcessor<MatchInput, Match> {
  private static final Logger log = LoggerFactory.getLogger(MatchDataProcessor.class);
  private final TeamRepository teamRepository;

  @Autowired
  public MatchDataProcessor(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  @Override
  public Match process(final MatchInput matchInput) throws Exception {
    Match match = new Match();
    match.setId(Long.parseLong(matchInput.getId()));
    match.setCity(matchInput.getCity());
    final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    match.setDate(LocalDate.parse(matchInput.getDate(), DATE_FORMATTER));
    match.setPlayerOfMatch(matchInput.getPlayer_of_match());
    match.setVenue(matchInput.getVenue());
    String firstInningsTeam, secondInningsTeam;
    if("bat".equals(matchInput.getToss_winner())){
        firstInningsTeam = matchInput.getToss_winner();
        secondInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) ? matchInput.getTeam2() : matchInput.getTeam1();
    }
    else{
        secondInningsTeam = matchInput.getToss_winner();
        firstInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) ? matchInput.getTeam2() : matchInput.getTeam1();
    }
    match.setTeam1(getOrCreateTeam(firstInningsTeam));
    match.setTeam2(getOrCreateTeam(secondInningsTeam));
    match.setTossWinner(matchInput.getToss_winner());
    match.setTossDecision(matchInput.getToss_decision());
    match.setResult(matchInput.getResult());
    match.setResultMargin(matchInput.getResult_margin());
    Team matchWinner = null;
    if (matchInput.getWinner() != null && !matchInput.getWinner().isEmpty() && !"NA".equalsIgnoreCase(matchInput.getWinner())) {
      matchWinner = getOrCreateTeam(matchInput.getWinner());
    }
    match.setMatchWinner(matchWinner);
    match.setUmpire1(matchInput.getUmpire1());
    match.setUmpire2(matchInput.getUmpire2());
    return match;
  }

  private Team getOrCreateTeam(String teamName) {
    if (teamName == null || teamName.trim().isEmpty() || "NA".equalsIgnoreCase(teamName.trim())) {
        return null;
    }

    Team team = null;
    try {
      team = this.teamRepository.findByTeamName(teamName);
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (team == null) {
      team = new Team();
      team.setTeamName(teamName);
      team.setTotalMatches(0L);
      team = this.teamRepository.save(team);
    }
    return team;
  }
}
