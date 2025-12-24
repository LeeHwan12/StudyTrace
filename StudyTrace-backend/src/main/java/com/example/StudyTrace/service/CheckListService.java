package com.example.StudyTrace.service;

import com.example.StudyTrace.domain.checklist.CheckItemCreateRequest;
import com.example.StudyTrace.domain.checklist.CheckItemResponse;
import com.example.StudyTrace.entity.ScoreCheckList;
import com.example.StudyTrace.entity.TimeLine;
import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.enums.CheckItemType;
import com.example.StudyTrace.enums.CheckStatus;
import com.example.StudyTrace.repository.ScoreCheckListRepository;
import com.example.StudyTrace.repository.TimeLineRepository;
import com.example.StudyTrace.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckListService {

    private final ScoreCheckListRepository checkListRepository;
    private final TimeLineRepository timeLineRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<CheckItemResponse> getItems(Long userId, Long timeLineId) {
        return checkListRepository.findByUser_IdAndTimeLine_IdOrderByIdAsc(userId, timeLineId)
                .stream()
                .map(CheckListService::toResponse)
                .toList();
    }

    @Transactional
    public CheckItemResponse addItem(Long userId, Long timeLineId, CheckItemCreateRequest dto) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));

        TimeLine tl = timeLineRepository.findByIdAndUser_Id(timeLineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found timeline"));

        ScoreCheckList item = ScoreCheckList.builder()
                .user(user)
                .timeLine(tl)
                .type(dto.getType() != null ? dto.getType() : CheckItemType.ETC)
                .points(dto.getPoints())
                .content(dto.getContent())
                .status(CheckStatus.NOT_DONE)
                .build();

        ScoreCheckList saved = checkListRepository.save(item);

        recalcAndUpdateTimelineScore(userId, timeLineId);
        return toResponse(saved);
    }

    @Transactional
    public void updateStatus(Long userId, Long itemId, CheckStatus status) {
        ScoreCheckList item = checkListRepository.findByIdAndUser_Id(itemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found checklist item"));

        item.setStatus(status);

        recalcAndUpdateTimelineScore(userId, item.getTimeLine().getId());
    }

    @Transactional
    public void updateItem(Long userId, Long itemId, CheckItemCreateRequest dto) {
        ScoreCheckList item = checkListRepository.findByIdAndUser_Id(itemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found checklist item"));

        item.setType(dto.getType() != null ? dto.getType() : CheckItemType.ETC);
        item.setPoints(dto.getPoints());
        item.setContent(dto.getContent());

        recalcAndUpdateTimelineScore(userId, item.getTimeLine().getId());
    }

    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        ScoreCheckList item = checkListRepository.findByIdAndUser_Id(itemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found checklist item"));

        Long timeLineId = item.getTimeLine().getId();

        int deleted = checkListRepository.deleteByIdAndUser_Id(itemId, userId);
        if (deleted == 0) throw new IllegalArgumentException("not found checklist item");

        recalcAndUpdateTimelineScore(userId, timeLineId);
    }

    @Transactional
    public int recalcAndUpdateTimelineScore(Long userId, Long timeLineId) {
        TimeLine tl = timeLineRepository.findByIdAndUser_Id(timeLineId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found timeline"));

        int doneSum = checkListRepository
                .sumPointsByUserAndTimelineAndStatus(userId, timeLineId, CheckStatus.DONE);

        int finalScore = Math.max(0, Math.min(100, doneSum));

        tl.changeScore(finalScore); // dirty checking으로 업데이트됨
        return finalScore;
    }

    private static CheckItemResponse toResponse(ScoreCheckList e) {
        return CheckItemResponse.builder()
                .id(e.getId())
                .status(e.getStatus())
                .type(e.getType())
                .points(e.getPoints())
                .content(e.getContent())
                .build();
    }
}

