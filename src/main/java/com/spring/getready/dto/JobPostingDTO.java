package com.spring.getready.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDTO {

    private Integer jobId;
    private String jobTitle;
    private String jobDescription;
    private String requiredSkills;
    private String experienceRequired;
    private String location;
    private String jobType;
    private Timestamp postedOn;
    private Timestamp deadline;
    private Boolean isActive;
    private String postedByUsername;
    private String postedByEmail;
    private Integer applicationCount;
}
