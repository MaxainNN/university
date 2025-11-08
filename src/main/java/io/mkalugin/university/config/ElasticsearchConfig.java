package io.mkalugin.university.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Конфигурация для клиента ElasticSearch.
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "io.mkalugin.university.repository.search")
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String elasticUrl;

    /**
     * Создает REST клиент для подключения к Elasticsearch.
     *
     * @return настроенный REST клиент
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder(HttpHost.create(elasticUrl)).build();
    }

    /**
     * Создает клиент Elasticsearch на основе REST клиента.
     *
     * @param restClient REST клиент
     * @return клиент Elasticsearch
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }
}
