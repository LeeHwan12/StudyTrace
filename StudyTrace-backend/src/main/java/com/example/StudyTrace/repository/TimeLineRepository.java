package com.example.StudyTrace.repository;

import com.example.StudyTrace.entity.TimeLine;
import com.example.StudyTrace.enums.TimeLineCategory;
import com.example.StudyTrace.repository.projection.TopicStatProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeLineRepository extends JpaRepository<TimeLine, Long> {

    // ✅ 상세/수정용: 소유자 검증 포함
    Optional<TimeLine> findByIdAndUser_Id(Long timeLineId, Long userId);

    // ✅ 상세 조회(너 서비스에서 findByUser_IdAndId 쓰던 것 대체 가능)
    Optional<TimeLine> findByUser_IdAndId(Long userId, Long timeLineId);

    // ✅ 삭제(삭제된 row 수 반환)
    int deleteByIdAndUser_Id(Long timeLineId, Long userId);

    // =========================
    // 목록 조회 (정렬: 최신 날짜 우선)
    // =========================

    // 1) 전체
    List<TimeLine> findAllByUser_IdOrderByStudyDateDesc(Long userId);

    // 2) 날짜만
    List<TimeLine> findByUser_IdAndStudyDateOrderByStudyDateDesc(Long userId, LocalDate studyDate);

    // 2-1) 날짜 + 카테고리
    List<TimeLine> findByUser_IdAndStudyDateAndCategoryOrderByStudyDateDesc(
            Long userId, LocalDate studyDate, TimeLineCategory category
    );

    // 3) 키워드만
    List<TimeLine> findByUser_IdAndTopicContainingIgnoreCaseOrderByStudyDateDesc(
            Long userId, String keyword
    );

    // 3-1) 키워드 + 카테고리
    List<TimeLine> findByUser_IdAndCategoryAndTopicContainingIgnoreCaseOrderByStudyDateDesc(
            Long userId, TimeLineCategory category, String keyword
    );

    // 4) 날짜 + 키워드
    List<TimeLine> findByUser_IdAndStudyDateAndTopicContainingIgnoreCaseOrderByStudyDateDesc(
            Long userId, LocalDate studyDate, String keyword
    );

    List<TimeLine> findByUser_IdAndCategoryOrderByStudyDateDesc(Long userId, TimeLineCategory category);

    List<TimeLine> findByUser_IdAndStudyDateAndCategoryAndTopicContainingIgnoreCaseOrderByStudyDateDesc(Long userId, LocalDate studyDate, TimeLineCategory category, String keyword);

    @Query("""
        select coalesce(avg(t.score), 0)
        from TimeLine t
        where t.user.id = :userId
          and t.studyDate between :start and :end
    """)
    double avgScoreByUserAndDateBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    long countByUser_IdAndStudyDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("""
        select 
            t.topic as topic,
            avg(t.score) as avgScore,
            count(t) as cnt
        from TimeLine t
        where t.user.id = :userId
          and t.studyDate between :from and :to
        group by t.topic
        order by avg(t.score) asc, count(t) desc
    """)
    List<TopicStatProjection> findTopicStats(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    List<TimeLine> findByUser_IdAndStudyDateBetweenOrderByStudyDateAsc(
            Long userId,
            LocalDate from,
            LocalDate to
    );

    @Query("""
      select t
      from TimeLine t
      where t.user.id = :userId
        and (:studyDate is null or t.studyDate = :studyDate)
        and (:category is null or t.category = :category)
        and (
          :keyword is null
          or trim(:keyword) = ''
          or lower(t.topic) like lower(concat(concat('%', :keyword), '%'))
        )
    """)
    Slice<TimeLine> searchSlice(
            @Param("userId") Long userId,
            @Param("studyDate") LocalDate studyDate,
            @Param("keyword") String keyword,
            @Param("category") TimeLineCategory category,
            Pageable pageable
    );

}
