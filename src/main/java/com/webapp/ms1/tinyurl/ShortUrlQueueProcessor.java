package com.webapp.ms1.tinyurl;

import com.webapp.ms1.sqs.QueueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

@Slf4j
@Component
@AllArgsConstructor
public class ShortUrlQueueProcessor {

    private final QueueService queueService;
    private final UrlService urlService;

    // runs every minute
    @Scheduled(fixedRate = 60000)
    public void processShortUrlFromSQS() {
        try {
            log.info("Url task started, polling to sync task queue ....");
            Message message = queueService.receiveMessages(1);
            if (message == null) {
                log.info("Exiting, no messages in queue.");
                return;
            }
            urlService.processQueueMessage(message);
            log.info("url saved in db, deleting message from queue ...");
            // delete message from sqs after processing
            queueService.deleteMessage(message);
            log.info("Message deleted from queue successfully.");
        } catch (Exception e) {
            log.error("Exception : ", e);
        }
    }
}