package com.example.wagemanager.api.employer;

import com.example.wagemanager.common.dto.ApiResponse;
import com.example.wagemanager.domain.workplace.dto.WorkplaceDto;
import com.example.wagemanager.domain.workplace.service.WorkplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employer/workplaces")
@RequiredArgsConstructor
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    // TODO: 인증 구현 후 @AuthenticationPrincipal로 userId 받아오기
    // 임시로 @RequestHeader 사용
    @PostMapping
    public ApiResponse<WorkplaceDto.Response> createWorkplace(
            @RequestHeader("User-Id") Long userId,
            @RequestBody WorkplaceDto.CreateRequest request) {
        return ApiResponse.success(workplaceService.createWorkplace(userId, request));
    }

    @GetMapping
    public ApiResponse<List<WorkplaceDto.ListResponse>> getWorkplaces(
            @RequestHeader("User-Id") Long userId) {
        return ApiResponse.success(workplaceService.getWorkplacesByUserId(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<WorkplaceDto.Response> getWorkplace(@PathVariable Long id) {
        return ApiResponse.success(workplaceService.getWorkplaceById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<WorkplaceDto.Response> updateWorkplace(
            @PathVariable Long id,
            @RequestBody WorkplaceDto.UpdateRequest request) {
        return ApiResponse.success(workplaceService.updateWorkplace(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivateWorkplace(@PathVariable Long id) {
        workplaceService.deactivateWorkplace(id);
        return ApiResponse.success(null);
    }
}
