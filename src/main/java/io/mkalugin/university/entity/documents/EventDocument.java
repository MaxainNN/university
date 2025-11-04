package io.mkalugin.university.entity.documents;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

/**
 * Модель для индекса события в Elasticsearch
 */
@Document(indexName = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDocument {

    @Id
    private String id;
    private String title;
    private String annotation;
    private String notes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;
}
