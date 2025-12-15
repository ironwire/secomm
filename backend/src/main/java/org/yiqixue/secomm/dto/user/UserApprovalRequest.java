package org.yiqixue.secomm.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.yiqixue.secomm.entity.User;

@Data
public class UserApprovalRequest {
    
    @NotNull(message = "审批状态不能为空")
    private User.ApprovalStatus approvalStatus;
    
    private String approvalNotes;
}