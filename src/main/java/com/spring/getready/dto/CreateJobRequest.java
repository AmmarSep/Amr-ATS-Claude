package com.spring.getready.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateJobRequest {

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Job description is required")
    private String jobDescription;

    @NotBlank(message = "Required skills are required")
    private String requiredSkills;

    private String experienceRequired;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Job type is required")
    private String jobType;

    private String deadline;
}
