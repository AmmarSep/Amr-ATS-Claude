package com.spring.getready.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    private Integer applicationId;
    private Integer jobId;
    private String jobTitle;
    private Integer candidateId;
    private String candidateName;
    private String candidateEmail;
    private Integer resumeId;
    private String resumeFileName;
    private Timestamp appliedOn;
    private String status;
    private Double aiScore;
    private String aiMatchKeywords;
    private Timestamp interviewScheduledOn;
    private Date interviewDate;
    private Time interviewTime;
    private String interviewerName;
    private String interviewLocation;
    private String notes;
}
