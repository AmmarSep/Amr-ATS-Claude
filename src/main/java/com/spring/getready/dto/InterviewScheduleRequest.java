package com.spring.getready.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InterviewScheduleRequest {

    @NotNull(message = "Application ID is required")
    private Integer applicationId;

    @NotBlank(message = "Interview date is required")
    private String interviewDate;

    @NotBlank(message = "Interview time is required")
    private String interviewTime;

    @NotBlank(message = "Interviewer name is required")
    private String interviewerName;

    @NotBlank(message = "Interview location is required")
    private String interviewLocation;
}
