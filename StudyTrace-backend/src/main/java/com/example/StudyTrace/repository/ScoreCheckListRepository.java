package com.example.StudyTrace.repository;

import com.example.StudyTrace.entity.ScoreCheckList;
import com.example.StudyTrace.enums.CheckItemType;
import com.example.StudyTrace.enums.CheckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ScoreCheckListRepository extends JpaRepository<ScoreCheckList, Long> {


    Optional<ScoreCheckList> findByIdAndUser_Id(Long id, Long userId);


    List<ScoreCheckList> findAllByTimeLine_IdAndUser_IdOrderByIdAsc(Long timeLineId, Long userId);

    List<ScoreCheckList> findAllByTimeLine_IdAndUser_IdAndStatusOrderByIdAsc(
            Long timeLineId, Long userId, CheckStatus status
    );

    List<ScoreCheckList> findAllByTimeLine_IdAndUser_IdAndTypeOrderByIdAsc(
            Long timeLineId, Long userId, CheckItemType type
    );

    List<ScoreCheckList> findAllByTimeLine_IdAndUser_IdAndStatusAndTypeOrderByIdAsc(
            Long timeLineId, Long userId, CheckStatus status, CheckItemType type
    );


    int deleteByIdAndUser_Id(Long timeLineId, Long userId);


    @Query("""
        select coalesce(sum(s.points), 0)
        from ScoreCheckList s
        where s.timeLine.id = :timeLineId
          and s.user.id = :userId
    """)
    int sumPointsByTimeLine(@Param("timeLineId") Long timeLineId, @Param("userId") Long userId);

    @Query("""
        select coalesce(sum(s.points), 0)
        from ScoreCheckList s
        where s.user.id = :userId
          and s.timeLine.id = :timelineId
          and s.status = :status
    """)
    Integer sumPointsByUserAndTimelineAndStatus(
            @Param("userId") Long userId,
            @Param("timelineId") Long timelineId,
            @Param("status") CheckStatus status
    );

    boolean existsByTimeLine_IdAndUser_IdAndTypeAndContent(
            Long timeLineId, Long userId, CheckItemType type, String content
    );

    List<ScoreCheckList> findByUser_IdAndTimeLine_IdOrderByIdAsc(Long userId, Long timeLineId);


}
