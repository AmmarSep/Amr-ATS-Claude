package com.spring.getready.controller.api;

import com.spring.getready.dto.ApiResponse;
import com.spring.getready.dto.ApplicationDTO;
import com.spring.getready.dto.InterviewScheduleRequest;
import com.spring.getready.model.Application;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.UploadFile;
import com.spring.getready.model.UserDetail;
import com.spring.getready.repository.ApplicationRepository;
import com.spring.getready.repository.JobPostingRepository;
import com.spring.getready.repository.UserDetailRepository;
import com.spring.getready.services.RecruitmentService;
import com.spring.getready.services.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApplicationRestController {

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UploadFileService uploadFileService;

    @PostMapping
    public ResponseEntity<?> submitApplication(
            @RequestParam("jobId") Integer jobId,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam(value = "notes", required = false) String notes) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            UserDetail user = userDetailRepository.findByEmailEquals(email);
            if (user == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("User not found"));
            }

            Optional<JobPosting> jobOpt = jobPostingRepository.findById(jobId);
            if (!jobOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Job not found"));
            }

            if (resume == null || resume.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Resume file is required"));
            }

            // Save resume file
            UploadFile uploadedResume = uploadFileService.saveFile(resume, user.getUsername());

            // Extract text from resume
            String resumeText = uploadFileService.extractTextFromFile(uploadedResume);

            // Create application
            Application application = new Application();
            application.setJobPosting(jobOpt.get());
            application.setCandidate(user);
            application.setResume(uploadedResume);
            application.setNotes(notes);

            // Submit application with AI screening
            Application savedApplication = recruitmentService.submitApplication(application, resumeText);

            ApplicationDTO applicationDTO = convertToDTO(savedApplication);

            return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", applicationDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error submitting application: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllApplications(
            @RequestParam(value = "jobId", required = false) Integer jobId,
            @RequestParam(value = "status", required = false) String status) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            UserDetail user = userDetailRepository.findByEmailEquals(email);

            if (user == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("User not found"));
            }
            String role = user.getUserGroup().getShortGroup();

            List<Application> applications;

            if (jobId != null) {
                // Filter by job
                Optional<JobPosting> jobOpt = jobPostingRepository.findById(jobId);
                if (!jobOpt.isPresent()) {
                    return ResponseEntity.status(404).body(ApiResponse.error("Job not found"));
                }
                applications = recruitmentService.getApplicationsByJob(jobOpt.get());
            } else if (status != null) {
                // Filter by status
                applications = applicationRepository.findByStatus(status);
            } else if ("CAN".equals(role)) {
                // Candidates see only their applications
                applications = applicationRepository.findByCandidate(user);
            } else {
                // Admins and recruiters see all applications
                applications = applicationRepository.findAll();
            }

            List<ApplicationDTO> applicationDTOs = applications.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applicationDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving applications: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Integer id) {
        try {
            Optional<Application> appOpt = applicationRepository.findById(id);
            if (!appOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Application not found"));
            }

            ApplicationDTO applicationDTO = convertToDTO(appOpt.get());
            return ResponseEntity.ok(ApiResponse.success("Application retrieved successfully", applicationDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving application: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Integer id,
            @RequestParam("status") String status) {
        try {
            Application updatedApp = recruitmentService.updateApplicationStatus(id, status);
            if (updatedApp == null) {
                return ResponseEntity.status(404).body(ApiResponse.error("Application not found"));
            }

            ApplicationDTO applicationDTO = convertToDTO(updatedApp);
            return ResponseEntity.ok(ApiResponse.success("Application status updated successfully", applicationDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error updating application status: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/interview")
    public ResponseEntity<?> scheduleInterview(
            @PathVariable Integer id,
            @Valid @RequestBody InterviewScheduleRequest request) {
        try {
            Optional<Application> appOpt = applicationRepository.findById(id);
            if (!appOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Application not found"));
            }

            Application application = appOpt.get();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            application.setInterviewDate(new Date(dateFormat.parse(request.getInterviewDate()).getTime()));
            application.setInterviewTime(new Time(timeFormat.parse(request.getInterviewTime()).getTime()));
            application.setInterviewerName(request.getInterviewerName());
            application.setInterviewLocation(request.getInterviewLocation());
            application.setInterviewScheduledOn(new Timestamp(System.currentTimeMillis()));
            application.setStatus("Interview");

            Application savedApp = applicationRepository.save(application);
            ApplicationDTO applicationDTO = convertToDTO(savedApp);

            return ResponseEntity.ok(ApiResponse.success("Interview scheduled successfully", applicationDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error scheduling interview: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/interview")
    public ResponseEntity<?> updateInterview(
            @PathVariable Integer id,
            @Valid @RequestBody InterviewScheduleRequest request) {
        try {
            Optional<Application> appOpt = applicationRepository.findById(id);
            if (!appOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Application not found"));
            }

            Application application = appOpt.get();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            application.setInterviewDate(new Date(dateFormat.parse(request.getInterviewDate()).getTime()));
            application.setInterviewTime(new Time(timeFormat.parse(request.getInterviewTime()).getTime()));
            application.setInterviewerName(request.getInterviewerName());
            application.setInterviewLocation(request.getInterviewLocation());

            Application savedApp = applicationRepository.save(application);
            ApplicationDTO applicationDTO = convertToDTO(savedApp);

            return ResponseEntity.ok(ApiResponse.success("Interview updated successfully", applicationDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error updating interview: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/interview")
    public ResponseEntity<?> cancelInterview(@PathVariable Integer id) {
        try {
            Optional<Application> appOpt = applicationRepository.findById(id);
            if (!appOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Application not found"));
            }

            Application application = appOpt.get();
            application.setInterviewDate(null);
            application.setInterviewTime(null);
            application.setInterviewerName(null);
            application.setInterviewLocation(null);
            application.setInterviewScheduledOn(null);
            application.setStatus("Submitted");

            Application savedApp = applicationRepository.save(application);
            ApplicationDTO applicationDTO = convertToDTO(savedApp);

            return ResponseEntity.ok(ApiResponse.success("Interview cancelled successfully", applicationDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error cancelling interview: " + e.getMessage()));
        }
    }

    private ApplicationDTO convertToDTO(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setApplicationId(app.getApplicationId());
        dto.setAppliedOn(app.getAppliedOn());
        dto.setStatus(app.getStatus());
        dto.setAiScore(app.getAiScore());
        dto.setAiMatchKeywords(app.getAiMatchKeywords());
        dto.setInterviewScheduledOn(app.getInterviewScheduledOn());
        dto.setInterviewDate(app.getInterviewDate());
        dto.setInterviewTime(app.getInterviewTime());
        dto.setInterviewerName(app.getInterviewerName());
        dto.setInterviewLocation(app.getInterviewLocation());
        dto.setNotes(app.getNotes());

        if (app.getJobPosting() != null) {
            dto.setJobId(app.getJobPosting().getJobId());
            dto.setJobTitle(app.getJobPosting().getJobTitle());
        }

        if (app.getCandidate() != null) {
            dto.setCandidateId(app.getCandidate().getUserId());
            dto.setCandidateName(app.getCandidate().getUsername());
            dto.setCandidateEmail(app.getCandidate().getEmail());
        }

        if (app.getResume() != null) {
            dto.setResumeId(app.getResume().getFileId());
            dto.setResumeFileName(app.getResume().getFileOriginalName());
        }

        return dto;
    }
}
