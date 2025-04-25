package com.bajaj.webhookapp.service;

import com.bajaj.webhookapp.model.ResultPayload;
import com.bajaj.webhookapp.model.WebhookRequest;
import com.bajaj.webhookapp.model.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class WebhookService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    @Autowired
    public WebhookService(RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClient = webClientBuilder.build();
    }

    public WebhookResponse generateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
        WebhookRequest request = new WebhookRequest(
                "Rishabh Kumar Tiwari",
                "RA2211031010151",
                "rt4045@srmist.edu.in"
        );

        log.info("Calling generate webhook API with request: {}", request);
        return restTemplate.postForObject(url, request, WebhookResponse.class);
    }

    @Retryable(maxAttempts = 4, backoff = @Backoff(delay = 1000))
    public void sendResult(String webhookUrl, String accessToken, ResultPayload resultPayload) {
        log.info("Sending result to webhook: {}", resultPayload);
        
        webClient.post()
                .uri(webhookUrl)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(resultPayload), ResultPayload.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("Webhook response: {}", response),
                        error -> log.error("Error sending webhook: {}", error.getMessage())
                );
    }

    public int[][] findMutualFollowers(WebhookResponse.User[] users) {
        List<int[]> mutualFollows = new ArrayList<>();
        
        for (int i = 0; i < users.length; i++) {
            WebhookResponse.User user1 = users[i];
            
            for (int followId : user1.getFollows()) {
                // Find the user being followed
                WebhookResponse.User user2 = Arrays.stream(users)
                        .filter(u -> u.getId() == followId)
                        .findFirst()
                        .orElse(null);
                
                if (user2 != null) {
                    // Check if this user also follows user1
                    boolean mutualFollow = Arrays.stream(user2.getFollows())
                            .anyMatch(id -> id == user1.getId());
                    
                    if (mutualFollow) {
                        // Add as [min, max]
                        int minId = Math.min(user1.getId(), user2.getId());
                        int maxId = Math.max(user1.getId(), user2.getId());
                        
                        // Check if this pair is already added
                        boolean alreadyAdded = mutualFollows.stream()
                                .anyMatch(pair -> pair[0] == minId && pair[1] == maxId);
                        
                        if (!alreadyAdded) {
                            mutualFollows.add(new int[]{minId, maxId});
                        }
                    }
                }
            }
        }
        
        return mutualFollows.toArray(new int[0][]);
    }
} 