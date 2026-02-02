package com.project.movieapi.emailservice.repository;

import com.project.movieapi.emailservice.model.EmailLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface EmailLogRepository extends ElasticsearchRepository<EmailLog, String> {
    List<EmailLog> findByStatus(String status);
}