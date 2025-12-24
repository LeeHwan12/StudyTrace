package com.example.StudyTrace.service;

import com.example.StudyTrace.domain.timeLine.ResponseTimeLineDTO;
import com.example.StudyTrace.domain.timeLine.TimeLineSliceResponse;
import com.example.StudyTrace.domain.timeLine.TimeLineUpsertRequest;
import com.example.StudyTrace.entity.TimeLine;
import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.enums.TimeLineCategory;
import com.example.StudyTrace.repository.TimeLineRepository;
import com.example.StudyTrace.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeLineService {

    private final TimeLineRepository timeLineRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public void createTimeLine(Long userId, TimeLineUpsertRequest dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));



        TimeLine tl = TimeLine.builder()
                .user(user)
                .topic(dto.getTopic())
                .memo(dto.getMemo())
                .studyDate(LocalDate.now())
                .score(0)
                .category(dto.getCategory())
                .build();

        timeLineRepository.save(tl);
    }

    @Transactional
    public void updateTimeLine(Long userId, Long timeLineId, TimeLineUpsertRequest dto) {
        TimeLine tl = timeLineRepository.findByIdAndUser_Id(timeLineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found timeline"));

        tl.updateContent(dto.getTopic(), dto.getMemo(), dto.getCategory());
    }

    @Transactional
    public void deleteTimeLine(Long userId, Long timeLineId) {
        int deleted = timeLineRepository.deleteByIdAndUser_Id(timeLineId, userId);

        if (deleted == 0) {
            throw new IllegalArgumentException("not found timeline");
        }
    }

    @Transactional(readOnly = true)
    public List<ResponseTimeLineDTO> getTimeLines(
            Long userId,
            LocalDate studyDate,
            String keyword,
            TimeLineCategory category
    ) {
        boolean hasDate = (studyDate != null);
        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasCategory = (category != null);

        List<TimeLine> result;

        if (!hasDate && !hasKeyword && !hasCategory) {                 // 전체
            result = timeLineRepository.findAllByUser_IdOrderByStudyDateDesc(userId);

        } else if (!hasDate && !hasKeyword) {                          // 카테고리만
            result = timeLineRepository.findByUser_IdAndCategoryOrderByStudyDateDesc(userId, category);

        } else if (hasDate && !hasKeyword && !hasCategory) {           // 날짜만
            result = timeLineRepository.findByUser_IdAndStudyDateOrderByStudyDateDesc(userId, studyDate);

        } else if (hasDate && !hasKeyword) {                           // 날짜 + 카테고리
            result = timeLineRepository.findByUser_IdAndStudyDateAndCategoryOrderByStudyDateDesc(userId, studyDate, category);

        } else if (!hasDate && hasKeyword && !hasCategory) {           // 키워드만
            result = timeLineRepository.findByUser_IdAndTopicContainingIgnoreCaseOrderByStudyDateDesc(userId, keyword);

        } else if (!hasDate && hasKeyword) {                           // 키워드 + 카테고리
            result = timeLineRepository.findByUser_IdAndCategoryAndTopicContainingIgnoreCaseOrderByStudyDateDesc(userId, category, keyword);

        } else if (hasDate && hasKeyword && !hasCategory) {            // 날짜 + 키워드
            result = timeLineRepository.findByUser_IdAndStudyDateAndTopicContainingIgnoreCaseOrderByStudyDateDesc(userId, studyDate, keyword);

        } else {                                                       // 날짜 + 키워드 + 카테고리
            result = timeLineRepository
                    .findByUser_IdAndStudyDateAndCategoryAndTopicContainingIgnoreCaseOrderByStudyDateDesc(
                            userId, studyDate, category, keyword
                    );
        }

        return result.stream().map(ResponseTimeLineDTO::of).toList();
    }


    @Transactional(readOnly = true)
    public ResponseTimeLineDTO getTimeLine(Long userId, Long timeLineId) {
        return ResponseTimeLineDTO.of(
                timeLineRepository
                        .findByUser_IdAndId(userId, timeLineId)
                        .orElseThrow(() -> new IllegalArgumentException("not found timeLine"))
        );
    }

    @Transactional(readOnly = true)
    public TimeLineSliceResponse getTimeLinesSlice(
            Long userId,
            LocalDate studyDate,
            String keyword,
            TimeLineCategory category,
            int page,
            int size
    ) {
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 30));

        // ✅ keyword 공백/빈문자 정리
        String k = (keyword == null) ? null : keyword.trim();
        if (k != null && k.isEmpty()) k = null;

        PageRequest pr = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "studyDate")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        Slice<TimeLine> slice = timeLineRepository.searchSlice(userId, studyDate, k, category, pr);

        List<ResponseTimeLineDTO> items = slice.getContent()
                .stream()
                .map(ResponseTimeLineDTO::of)
                .toList();

        return new TimeLineSliceResponse(items, slice.hasNext(), safePage + 1);
    }

}
