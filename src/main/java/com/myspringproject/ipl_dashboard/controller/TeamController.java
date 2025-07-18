package com.myspringproject.ipl_dashboard.controller;

import java.util.List;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;

import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.myspringproject.ipl_dashboard.model.Match;
import com.myspringproject.ipl_dashboard.model.Team;
import com.myspringproject.ipl_dashboard.repository.MatchRepository;
import com.myspringproject.ipl_dashboard.repository.TeamRepository;

@RestController
public class TeamController {

    private TeamRepository teamRepository;
    private MatchRepository matchRepository;
    
    public TeamController(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @GetMapping("/team/{teamName}")
    public Team getTeam(@PathVariable String teamName) {
        Team team = this.teamRepository.findByTeamName(teamName);
        List<Match>matches = this.matchRepository.findLatestMatchesByTeam(teamName,4);
        team.setMatches(matches);
        return team;
    }
}
