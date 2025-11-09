package com.example.wagemanager.domain.workrecord.service;

import com.example.wagemanager.domain.contract.entity.WorkerContract;
import com.example.wagemanager.domain.contract.repository.WorkerContractRepository;
import com.example.wagemanager.domain.worker.entity.Worker;
import com.example.wagemanager.domain.worker.repository.WorkerRepository;
import com.example.wagemanager.domain.workrecord.dto.WorkRecordDto;
import com.example.wagemanager.domain.workrecord.entity.WorkRecord;
import com.example.wagemanager.domain.workrecord.enums.WorkRecordStatus;
import com.example.wagemanager.domain.workrecord.repository.WorkRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkRecordService {

    private final WorkRecordRepository workRecordRepository;
    private final WorkerContractRepository workerContractRepository;
    private final WorkerRepository workerRepository;

    public List<WorkRecordDto.Response> getWorkRecordsByContract(Long contractId) {
        return workRecordRepository.findByContractId(contractId).stream()
                .map(WorkRecordDto.Response::from)
                .collect(Collectors.toList());
    }

    public WorkRecordDto.DetailedResponse getWorkRecordById(Long workRecordId) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("근무 기록을 찾을 수 없습니다."));
        return WorkRecordDto.DetailedResponse.from(workRecord);
    }

    // 고용주용: 사업장의 근무 기록 조회 (캘린더)
    public List<WorkRecordDto.CalendarResponse> getWorkRecordsByWorkplaceAndDateRange(
            Long workplaceId, LocalDate startDate, LocalDate endDate) {
        List<WorkRecord> records = workRecordRepository.findByWorkplaceAndDateRange(workplaceId, startDate, endDate);
        return records.stream()
                .map(WorkRecordDto.CalendarResponse::from)
                .collect(Collectors.toList());
    }

    // 근로자용: 내 근무 기록 조회
    public List<WorkRecordDto.DetailedResponse> getWorkRecordsByWorkerAndDateRange(
            Long userId, LocalDate startDate, LocalDate endDate) {
        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("근로자 정보를 찾을 수 없습니다."));

        List<WorkRecord> records = workRecordRepository.findByWorkerAndDateRange(worker.getId(), startDate, endDate);
        return records.stream()
                .map(WorkRecordDto.DetailedResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkRecordDto.Response createWorkRecord(WorkRecordDto.CreateRequest request) {
        WorkerContract contract = workerContractRepository.findById(request.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("계약을 찾을 수 없습니다."));

        int totalMinutes = calculateWorkMinutes(
                LocalDateTime.of(request.getWorkDate(), request.getStartTime()),
                LocalDateTime.of(request.getWorkDate(), request.getEndTime()),
                request.getBreakMinutes() != null ? request.getBreakMinutes() : 0
        );

        WorkRecord workRecord = WorkRecord.builder()
                .contract(contract)
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .breakMinutes(request.getBreakMinutes() != null ? request.getBreakMinutes() : 0)
                .totalWorkMinutes(totalMinutes)
                .status(WorkRecordStatus.SCHEDULED)
                .memo(request.getMemo())
                .build();

        WorkRecord savedRecord = workRecordRepository.save(workRecord);
        return WorkRecordDto.Response.from(savedRecord);
    }

    @Transactional
    public List<WorkRecordDto.Response> batchCreateWorkRecords(WorkRecordDto.BatchCreateRequest request) {
        WorkerContract contract = workerContractRepository.findById(request.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("계약을 찾을 수 없습니다."));

        int totalMinutes = calculateWorkMinutes(
                LocalDateTime.of(LocalDate.now(), request.getStartTime()),
                LocalDateTime.of(LocalDate.now(), request.getEndTime()),
                request.getBreakMinutes() != null ? request.getBreakMinutes() : 0
        );

        List<WorkRecord> workRecords = new ArrayList<>();
        for (LocalDate workDate : request.getWorkDates()) {
            WorkRecord workRecord = WorkRecord.builder()
                    .contract(contract)
                    .workDate(workDate)
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .breakMinutes(request.getBreakMinutes() != null ? request.getBreakMinutes() : 0)
                    .totalWorkMinutes(totalMinutes)
                    .status(WorkRecordStatus.SCHEDULED)
                    .memo(request.getMemo())
                    .build();
            workRecords.add(workRecord);
        }

        List<WorkRecord> savedRecords = workRecordRepository.saveAll(workRecords);
        return savedRecords.stream()
                .map(WorkRecordDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkRecordDto.Response updateWorkRecord(Long workRecordId, WorkRecordDto.UpdateRequest request) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("근무 기록을 찾을 수 없습니다."));

        if (request.getStartTime() != null || request.getEndTime() != null || request.getBreakMinutes() != null) {
            int totalMinutes = calculateWorkMinutes(
                    LocalDateTime.of(workRecord.getWorkDate(),
                            request.getStartTime() != null ? request.getStartTime() : workRecord.getStartTime()),
                    LocalDateTime.of(workRecord.getWorkDate(),
                            request.getEndTime() != null ? request.getEndTime() : workRecord.getEndTime()),
                    request.getBreakMinutes() != null ? request.getBreakMinutes() : workRecord.getBreakMinutes()
            );

            workRecord.updateWorkRecord(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getBreakMinutes(),
                    totalMinutes,
                    request.getMemo()
            );
        }

        return WorkRecordDto.Response.from(workRecord);
    }

    @Transactional
    public void completeWorkRecord(Long workRecordId) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("근무 기록을 찾을 수 없습니다."));
        workRecord.complete();
    }

    @Transactional
    public void deleteWorkRecord(Long workRecordId) {
        WorkRecord workRecord = workRecordRepository.findById(workRecordId)
                .orElseThrow(() -> new IllegalArgumentException("근무 기록을 찾을 수 없습니다."));

        // SCHEDULED 상태만 삭제 가능
        if (workRecord.getStatus() != WorkRecordStatus.SCHEDULED) {
            throw new IllegalStateException("예정된 근무만 삭제할 수 있습니다.");
        }

        workRecordRepository.delete(workRecord);
    }

    private int calculateWorkMinutes(LocalDateTime start, LocalDateTime end, int breakMinutes) {
        long totalMinutes = Duration.between(start, end).toMinutes();
        return (int) (totalMinutes - breakMinutes);
    }
}
