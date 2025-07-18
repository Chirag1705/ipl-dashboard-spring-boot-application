package com.myspringproject.ipl_dashboard.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.myspringproject.ipl_dashboard.model.Match;

public interface MatchRepository extends CrudRepository<Match, Long> {
    @Query("SELECT m FROM Match m WHERE m.team1.teamName = :teamName1 OR m.team2.teamName = :teamName2 ORDER BY m.date DESC")
   List<Match>findByTeamNameSorted(@Param("teamName1") String teamName1, @Param("teamName2") String teamName2, PageRequest pageable);

   default List<Match> findLatestMatchesByTeam(String teamName, int count) {
       return findByTeamNameSorted(teamName, teamName, PageRequest.of(0, count));
   }

}
