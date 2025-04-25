package com.bajaj.webhookapp.service;

import com.bajaj.webhookapp.model.ResultPayload;
import com.bajaj.webhookapp.model.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookRunner implements ApplicationRunner {

    private final WebhookService webhookService;

    @Autowired
    public WebhookRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Application started, executing webhook workflow...");
        
        try {
            // Step 1: Generate webhook
            WebhookResponse response = webhookService.generateWebhook();
            log.info("Successfully generated webhook: {}", response.getWebhook());
            
            // Step 2: Process the users data to find mutual followers
            int[][] mutualFollowers = webhookService.findMutualFollowers(response.getData().getUsers());
            log.info("Found mutual followers: {}", (Object) mutualFollowers);
            
            // Step 3: Create payload with results
            ResultPayload resultPayload = new ResultPayload("RA2211031010151", mutualFollowers);
            
            // Step 4: Send result to webhook
            webhookService.sendResult(response.getWebhook(), response.getAccessToken(), resultPayload);
            
            log.info("Webhook workflow completed successfully");
        } catch (Exception e) {
            log.error("Error executing webhook workflow: {}", e.getMessage(), e);
        }
    }
} 