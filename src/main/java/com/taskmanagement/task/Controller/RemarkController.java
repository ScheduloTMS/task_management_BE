package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.ApiResponse;
import com.taskmanagement.task.DTO.RemarkDTO;
import com.taskmanagement.task.Service.RemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/remarks")
public class RemarkController {

    @Autowired
    private RemarkService remarkService;

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse> getRemarksForAssignment(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<RemarkDTO> remarks = remarkService.getRemarksForAssignment(taskId, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse(200, "Remarks retrieved successfully", remarks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(403, e.getMessage(), null));
        }
    }

    @PostMapping("/tasks/{taskId}")
    public ResponseEntity<ApiResponse> addRemark(
            @PathVariable UUID taskId,
            @RequestParam String comment,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            RemarkDTO remark = remarkService.addRemark(taskId, userDetails.getUsername(), comment);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(201, "Comment added successfully", remark));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(403, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{remarkId}")
    public ResponseEntity<ApiResponse> deleteRemark(
            @PathVariable UUID remarkId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            remarkService.deleteRemark(remarkId, userDetails.getUsername());
            return ResponseEntity.ok(new ApiResponse(200, "Remark deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(403, e.getMessage(), null));
        }
    }
}