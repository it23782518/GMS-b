package com.example.Backend.service;

import com.example.Backend.dto.SessionRequestDTO;
import com.example.Backend.model.Exercise;
import com.example.Backend.model.ExerciseLog;
import com.example.Backend.model.Member;
import com.example.Backend.model.Routine;
import com.example.Backend.repository.ExerciseLogRepository;
import com.example.Backend.repository.ExerciseRepository;
import com.example.Backend.repository.MemberRepository;
import com.example.Backend.repository.RoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private final ExerciseLogRepository exerciseLogRepository;
    private final MemberRepository memberRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;

    public SessionService(ExerciseLogRepository exerciseLogRepository, MemberRepository memberRepository,
            RoutineRepository routineRepository, ExerciseRepository exerciseRepository) {
        this.exerciseLogRepository = exerciseLogRepository;
        this.memberRepository = memberRepository;
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional
    public boolean logSession(SessionRequestDTO request) {
        Long memberId = request.getMemberId();
        Long routineId = request.getRoutineId();

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        Optional<Routine> routineOpt = routineRepository.findById(routineId);
        if (memberOpt.isEmpty() || routineOpt.isEmpty()) {
            return false;
        }

        Long maxSessionCounter = exerciseLogRepository
                .findMaxSessionCounterByMemberIdAndRoutineId(memberId, routineId)
                .orElse(0L);
        Long newSessionCounter = maxSessionCounter + 1;

        for (SessionRequestDTO.ExerciseLogDTO logDTO : request.getExerciseLogs()) {
            Optional<Exercise> exerciseOpt = exerciseRepository.findById(logDTO.getExerciseId());
            if (exerciseOpt.isEmpty()) {
                return false;
            }

            ExerciseLog log = new ExerciseLog();
            log.setMember(memberOpt.get());
            log.setRoutine(routineOpt.get());
            log.setExercise(exerciseOpt.get());
            log.setWeight(logDTO.getWeight());
            log.setCompleted(logDTO.isCompleted());
            log.setSessionCounter(newSessionCounter);

            exerciseLogRepository.save(log);
        }
        return true;
    }

    public List<Map<String, Object>> getExerciseStats(Long exerciseId, Long memberId) {
        logger.info("Fetching exercise stats for exerciseId: {} and memberId: {}", exerciseId, memberId);

        // Verify that the exercise and member exist
        if (!exerciseRepository.existsById(exerciseId)) {
            logger.error("Exercise with id {} not found", exerciseId);
            throw new RuntimeException("Exercise not found");
        }
        if (!memberRepository.existsById(memberId)) {
            logger.error("Member with id {} not found", memberId);
            throw new RuntimeException("Member not found");
        }

        List<ExerciseLog> logs = exerciseLogRepository.findByExerciseIdAndMemberIdOrderBySessionCounterAsc(exerciseId,
                memberId);
        logger.info("Found {} exercise logs", logs.size());

        return logs.stream()
                .map(log -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("sessionCounter", log.getSessionCounter());
                    stat.put("weight", log.getWeight());
                    stat.put("completed", log.isCompleted());
                    return stat;
                })
                .collect(Collectors.toList());
    }
}