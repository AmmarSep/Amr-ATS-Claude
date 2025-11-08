package com.spring.getready.controller.api;

import com.spring.getready.dto.ApiResponse;
import com.spring.getready.dto.CreateJobRequest;
import com.spring.getready.dto.JobPostingDTO;
import com.spring.getready.model.JobPosting;
import com.spring.getready.model.UserDetail;
import com.spring.getready.repository.JobPostingRepository;
import com.spring.getready.repository.UserDetailRepository;
import com.spring.getready.services.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobRestController {

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @GetMapping
    public ResponseEntity<?> getAllActiveJobs() {
        try {
            List<JobPosting> jobs = recruitmentService.getAllActiveJobs();
            List<JobPostingDTO> jobDTOs = jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving jobs: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Integer id) {
        try {
            Optional<JobPosting> jobOpt = jobPostingRepository.findById(id);
            if (jobOpt.isPresent()) {
                JobPostingDTO jobDTO = convertToDTO(jobOpt.get());
                return ResponseEntity.ok(ApiResponse.success("Job retrieved successfully", jobDTO));
            }
            return ResponseEntity.status(404).body(ApiResponse.error("Job not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving job: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllJobs() {
        try {
            List<JobPosting> jobs = jobPostingRepository.findAll();
            List<JobPostingDTO> jobDTOs = jobs.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error retrieving jobs: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createJob(@Valid @RequestBody CreateJobRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Optional<UserDetail> userOpt = userDetailRepository.findByEmailEquals(email);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(401).body(ApiResponse.error("User not found"));
            }

            JobPosting jobPosting = new JobPosting();
            jobPosting.setJobTitle(request.getJobTitle());
            jobPosting.setJobDescription(request.getJobDescription());
            jobPosting.setRequiredSkills(request.getRequiredSkills());
            jobPosting.setExperienceRequired(request.getExperienceRequired());
            jobPosting.setLocation(request.getLocation());
            jobPosting.setJobType(request.getJobType());
            jobPosting.setPostedOn(new Timestamp(System.currentTimeMillis()));
            jobPosting.setIsActive(true);
            jobPosting.setPostedBy(userOpt.get());

            if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    jobPosting.setDeadline(new Timestamp(sdf.parse(request.getDeadline()).getTime()));
                } catch (Exception e) {
                    // Ignore deadline parsing errors
                }
            }

            JobPosting savedJob = recruitmentService.saveJobPosting(jobPosting);
            JobPostingDTO jobDTO = convertToDTO(savedJob);

            return ResponseEntity.ok(ApiResponse.success("Job created successfully", jobDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error creating job: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Integer id, @Valid @RequestBody CreateJobRequest request) {
        try {
            Optional<JobPosting> jobOpt = jobPostingRepository.findById(id);
            if (!jobOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Job not found"));
            }

            JobPosting jobPosting = jobOpt.get();
            jobPosting.setJobTitle(request.getJobTitle());
            jobPosting.setJobDescription(request.getJobDescription());
            jobPosting.setRequiredSkills(request.getRequiredSkills());
            jobPosting.setExperienceRequired(request.getExperienceRequired());
            jobPosting.setLocation(request.getLocation());
            jobPosting.setJobType(request.getJobType());

            if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    jobPosting.setDeadline(new Timestamp(sdf.parse(request.getDeadline()).getTime()));
                } catch (Exception e) {
                    // Ignore deadline parsing errors
                }
            }

            JobPosting updatedJob = recruitmentService.saveJobPosting(jobPosting);
            JobPostingDTO jobDTO = convertToDTO(updatedJob);

            return ResponseEntity.ok(ApiResponse.success("Job updated successfully", jobDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error updating job: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleJobStatus(@PathVariable Integer id) {
        try {
            Optional<JobPosting> jobOpt = jobPostingRepository.findById(id);
            if (!jobOpt.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.error("Job not found"));
            }

            JobPosting jobPosting = jobOpt.get();
            jobPosting.setIsActive(!jobPosting.getIsActive());
            JobPosting updatedJob = recruitmentService.saveJobPosting(jobPosting);
            JobPostingDTO jobDTO = convertToDTO(updatedJob);

            return ResponseEntity.ok(ApiResponse.success("Job status updated successfully", jobDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Error toggling job status: " + e.getMessage()));
        }
    }

    private JobPostingDTO convertToDTO(JobPosting job) {
        JobPostingDTO dto = new JobPostingDTO();
        dto.setJobId(job.getJobId());
        dto.setJobTitle(job.getJobTitle());
        dto.setJobDescription(job.getJobDescription());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setExperienceRequired(job.getExperienceRequired());
        dto.setLocation(job.getLocation());
        dto.setJobType(job.getJobType());
        dto.setPostedOn(job.getPostedOn());
        dto.setDeadline(job.getDeadline());
        dto.setIsActive(job.getIsActive());

        if (job.getPostedBy() != null) {
            dto.setPostedByUsername(job.getPostedBy().getUsername());
            dto.setPostedByEmail(job.getPostedBy().getEmail());
        }

        if (job.getApplications() != null) {
            dto.setApplicationCount(job.getApplications().size());
        } else {
            dto.setApplicationCount(0);
        }

        return dto;
    }
}
