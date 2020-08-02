package com.oyekanmiayo.findtreasureapp.processes;

import com.oyekanmiayo.findtreasureapp.entities.FTResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@RequiredArgsConstructor
public class Hunter {

    private static final Logger log = LoggerFactory.getLogger(Hunter.class);
    private final RestTemplate restTemplate;
    private HttpEntity<FTResponse> httpRequest;

    @Value("${entry.url}")
    private String entryUrl;
    @Value("${ft.token}")
    private String ftToken;
    @Value("${phone.no}")
    private String phoneNo;


    @PostConstruct
    public void init() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        httpHeaders.set("Authorization", "Bearer " + ftToken);
        httpHeaders.set("gomoney", phoneNo);
        httpHeaders.setAccept(Collections.singletonList(MediaType.ALL));
        httpRequest = new HttpEntity<>(httpHeaders);
    }

    /**
     * @technique: Breadth-First Search Traversal
     * @futureImplementation: Use Concurrency. BFS & Concurrency is tricky though because the queue could be empty, but
     * the traversal is not actually complete. What will be the source of truth in that case?
     */

    public void findTreasure() {
        int counter = 0;
        Set<String> visited = new HashSet<>();

        Deque<String> endpointQueue = new ArrayDeque<>();
        endpointQueue.add(entryUrl);

        while (!endpointQueue.isEmpty()) {
            String currUrl = endpointQueue.poll();
            log.info("Visited {}", currUrl);

            try {
                ResponseEntity<FTResponse> response = restTemplate
                        .exchange(currUrl, HttpMethod.GET, httpRequest, FTResponse.class);

                log.info("Number of nodes visited {}", counter++);
                log.info("X-RateLimit-Limit {}", response.getHeaders().get("X-RateLimit-Limit"));
                log.info("X-RateLimit-Remaining {}", response.getHeaders().get("X-RateLimit-Remaining"));
                log.info("Retry after {}", response.getHeaders().get("Retry-After"));

                if (response.getBody() == null || response.getBody().getPaths() == null) {
                    continue;
                }

                if (response.getStatusCode() == HttpStatus.FOUND) {
                    log.info("Got treasure at {}", currUrl);
                }

                for (String url : response.getBody().getPaths()) {
                    if (visited.add(url)) { //if true, we are adding url for the first time
                        endpointQueue.addFirst(url);
                    }
                }

            } catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
                endpointQueue.addLast(currUrl);
            }
        }
    }

}
