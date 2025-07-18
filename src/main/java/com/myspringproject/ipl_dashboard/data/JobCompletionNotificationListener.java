package com.myspringproject.ipl_dashboard.data;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.myspringproject.ipl_dashboard.model.Team;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final EntityManager em;

    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("!!! JOB FINISHED! Time to verify the results");
            Map<String, Team> teamData = new HashMap<>();
            try {

                em.createQuery("SELECT m.team1, COUNT(*) FROM Match m GROUP BY m.team1", Object[].class)
                        .getResultList()
                        .stream()
                        .forEach(e -> {
                            Team team = (Team) e[0];
                            team.setTotalMatches((long) e[1]);
                            teamData.put(team.getTeamName(), team);
                        });

                em.createQuery("SELECT m.team2, COUNT(*) FROM Match m GROUP BY m.team2", Object[].class)
                        .getResultList()
                        .stream()
                        .forEach(e -> {
                            Team teamFromQuery = (Team) e[0];
                            Team existingTeam = teamData.get(teamFromQuery.getTeamName());
                            if (existingTeam != null)
                                existingTeam.setTotalMatches(existingTeam.getTotalMatches() + (long) e[1]);
                            else {
                                log.warn("Team '{}' appeared as team2 but not found as team1. Creating new entry.",
                                        (String) (e[0]));
                                teamFromQuery.setTotalMatches((long) e[1]);
                                teamData.put(teamFromQuery.getTeamName(), teamFromQuery);
                            }
                        });

                em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                        .getResultList()
                        .stream()
                        .forEach(e -> {
                            Team teamFromQuery = (Team) e[0];
                            Team existingTeam = teamData.get(teamFromQuery.getTeamName());
                            if (existingTeam != null)
                                existingTeam.setTotalWins((long) e[1]);
                            else {
                                log.warn("Winning team '{}' not found in processed teams. Creating new entry.",
                                        (String) (e[0]));
                                teamFromQuery.setTotalWins((long) e[1]);
                                teamData.put(teamFromQuery.getTeamName(), teamFromQuery);
                            }
                        });
                log.info("Calculated stats for {} unique teams. Attempting to persist them...", teamData.size());
                int persistedCount = 0;
                for (Team team : teamData.values()) {
                    try {
                        em.merge(team);
                        em.flush();
                        log.info("Successfully processed team for persistence: {}", team.getTeamName());
                        persistedCount++;
                    } catch (PersistenceException pe) {
                        log.error("Persistence error for team {}: {}", team.getTeamName(), pe.getMessage(), pe);
                    } catch (Exception e) {
                        log.error("Unexpected error while processing team {}: {}", team.getTeamName(), e.getMessage(),
                                e);
                    }
                }
                log.info("Finished iterating through teams for persistence. Successfully processed {} out of {} teams.",
                        persistedCount, teamData.size());
                log.info("--- Verifying Teams in DB within the same transaction ---");
                List<Team> teamsInDb = em.createQuery("SELECT t FROM Team t", Team.class).getResultList();
                if (teamsInDb.isEmpty()) {
                    log.error(
                            "CRITICAL: No Teams found in DB immediately after persistence attempt (within same transaction)! Transaction might be marked for rollback.");
                } else {
                    log.info(
                            "SUCCESS: {} Teams found in DB immediately after persistence attempt (within same transaction):",
                            teamsInDb.size());
                    teamsInDb.forEach(t -> log.info("   -> DB Confirmed Team (inside tx): {}", t));
                }
                log.info("--- End of in-transaction verification ---");
            } catch (Exception overallException) {
                log.error("FATAL ERROR in JobCompletionNotificationListener.afterJob causing transaction rollback: {}",
                        overallException.getMessage(), overallException);
                throw overallException;
            }

            teamData.values().forEach(team -> log.info("Final calculated Team (from map): {}", team));
        }
    }
}
