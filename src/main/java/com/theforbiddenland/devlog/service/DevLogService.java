package com.theforbiddenland.devlog.service;

import com.theforbiddenland.auth.security.UserPrincipal;
import com.theforbiddenland.devlog.dto.request.DevLogCreateRequest;
import com.theforbiddenland.devlog.dto.request.DevLogUpdateRequest;
import com.theforbiddenland.devlog.dto.response.DevLogResponse;
import com.theforbiddenland.devlog.entity.DevLog;
import com.theforbiddenland.devlog.repository.DevLogRepository;
import com.theforbiddenland.global.exception.DevLogException;
import com.theforbiddenland.global.exception.ErrorCode;
import com.theforbiddenland.global.exception.ItemException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevLogService {

    private final DevLogRepository devLogRepository;

    // TODO Pagination 적용
    public List<DevLogResponse> findDevLogs(UserPrincipal userPrincipal) {
        boolean isAdmin = userPrincipal != null && userPrincipal.isAdmin();

        return devLogRepository.findAll()
                .stream()
                .map(i -> DevLogResponse.of(i, isAdmin))
                .toList();
    }

    public DevLogResponse createDevLog(DevLogCreateRequest request, UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.isAdmin()) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        DevLog devLog = DevLog.of(request, userPrincipal.getUserId());
        return DevLogResponse.of(
                devLogRepository.save(devLog),
                true
        );
    }

    public DevLogResponse updateDevLog(String devLogId, DevLogUpdateRequest request, UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.isAdmin()) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        DevLog devLog = devLogRepository.findById(devLogId)
                .orElseThrow(() -> new DevLogException(ErrorCode.DEVLOG_NOT_FOUND));

        boolean isUpdated = devLog.update(request);
        if (isUpdated) {
            devLog = devLogRepository.save(devLog);
        }

        return DevLogResponse.of(devLog, true);
    }

    public void deleteDevLog(String devLogId, UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.isAdmin()) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        DevLog devLog = devLogRepository.findById(devLogId)
                .orElseThrow(() -> new DevLogException(ErrorCode.DEVLOG_NOT_FOUND));

        if (!devLog.getAdminId().equals(userPrincipal.getUserId())) {
            throw new ItemException(ErrorCode.ACCESS_DENIED);
        }

        devLogRepository.delete(devLog);
    }

}
