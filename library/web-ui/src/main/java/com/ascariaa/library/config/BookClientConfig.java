package com.ascariaa.library.config;

import com.ascariaa.library.adapter.GrpcBookCatalogAdapter;
import com.ascariaa.library.adapter.HttpBookCatalogAdapter;
import com.ascariaa.library.client.BookHttpClient;
import com.ascariaa.library.port.BookCatalogPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class BookClientConfig {

    @Value("${app.book-client.type}")
    private String clientType;

    @Value("${app.book-client.http.url}")
    private String httpUrl;

    @Value("${app.book-client.http.connect-timeout-ms}")
    private int httpConnectTimeout;

    @Value("${app.book-client.http.read-timeout-ms}")
    private int httpReadTimeout;

    @Value("${app.book-client.grpc.deadline-ms}")
    private long grpcDeadlineMs;

    @Bean
    public BookCatalogPort bookCatalogPort() {
        if ("http".equalsIgnoreCase(clientType)) {
            log.info("Initializing HTTP Book Catalog Adapter with connectTimeout={}ms, readTimeout={}ms",
                    httpConnectTimeout, httpReadTimeout);

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(httpConnectTimeout);
            requestFactory.setReadTimeout(httpReadTimeout);

            RestClient restClient = RestClient.builder()
                    .baseUrl(httpUrl)
                    .requestFactory(requestFactory)
                    .build();

            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
            BookHttpClient httpClient = factory.createClient(BookHttpClient.class);

            return new HttpBookCatalogAdapter(httpClient);

        } else if ("grpc".equalsIgnoreCase(clientType)) {
            log.info("Initializing gRPC Book Catalog Adapter with deadline={}ms", grpcDeadlineMs);
            return new GrpcBookCatalogAdapter(grpcDeadlineMs);

        } else {
            throw new IllegalStateException("Unknown book client type: '" + clientType + "'. Supported values are 'http' or 'grpc'.");
        }
    }
}