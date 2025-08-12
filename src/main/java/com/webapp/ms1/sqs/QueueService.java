package com.webapp.ms1.sqs;

import com.webapp.ms1.config.AppConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class QueueService {

    public void sendMessage(String messageBody) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(AppConfig.getShorturlQueueUrl())
                .messageBody(messageBody)
                .build();
        SendMessageResponse response = AppConfig.getSqsClient().sendMessage(request);
        log.info("Message sent to SQS. Message ID: {}", response.messageId());
    }

    public Message receiveMessages(int messageCount) {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(AppConfig.getShorturlQueueUrl())
                .maxNumberOfMessages(messageCount)
                .waitTimeSeconds(3)
                .build();
        List<Message> messages = AppConfig.getSqsClient().receiveMessage(receiveRequest).messages();
        if (messages.isEmpty()) {
            log.info("No messages received from SQS.");
            return null;
        }
        Message message = messages.getFirst();
        log.info("Received message: {}", message.body());
        return message;
    }

    public void deleteMessage(Message message) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(AppConfig.getShorturlQueueUrl())
                .receiptHandle(message.receiptHandle())
                .build();
        AppConfig.getSqsClient().deleteMessage(deleteRequest);
        log.info("Deleted message with ID: {}", message.messageId());
    }
}
