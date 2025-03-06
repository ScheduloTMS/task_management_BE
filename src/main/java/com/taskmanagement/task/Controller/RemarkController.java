package com.taskmanagement.task.Controller;

import com.taskmanagement.task.DTO.RemarkDTO;
import com.taskmanagement.task.Service.RemarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class RemarkController {

    private final RemarkService remarkService;

    public RemarkController(RemarkService remarkService) {
        this.remarkService = remarkService;
    }


    @GetMapping("tasks/{task_id}/remarks")
    public ResponseEntity<Map<String, Object>> getAllRemarks(@PathVariable("task_id") UUID taskId) {
        List<RemarkDTO> remarks = remarkService.getAllRemarksForTask(taskId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Remarks retrieved successfully");
        response.put("body", remarks);

        return ResponseEntity.ok(response);
    }


    @PostMapping("tasks/{task_id}/remarks")
    public ResponseEntity<Map<String, Object>> addRemark(@PathVariable("task_id") UUID taskId,
                                                         @RequestBody Map<String, String> requestBody) {
        String userId = requestBody.get("user_id");
        String comment = requestBody.get("comment");

        RemarkDTO remark = remarkService.addRemark(taskId, userId, comment);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Comment added successfully");
        response.put("body", remark);

        return ResponseEntity.status(201).body(response);
    }


    @DeleteMapping("/remarks/{remark_id}")
    public ResponseEntity<Map<String, Object>> deleteRemark(@PathVariable("remark_id") UUID remarkId,
                                                            @RequestParam("user_id") String userId) {
        try {
            remarkService.deleteRemark(remarkId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", 200);
            response.put("message", "Remark soft-deleted successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 403);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(403).body(errorResponse);
        }
    }
}
