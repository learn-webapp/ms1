package com.webapp.ms1.sqs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class QueueService {

    private final SqsClient sqsClient;

    private static final String QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/211125360872/shortUrlQueue";

    public void sendMessage(String messageBody) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(messageBody)
                .build();
        SendMessageResponse response = sqsClient.sendMessage(request);
        log.info("Message sent to SQS. Message ID: {}", response.messageId());
    }

    public Message receiveMessages(int messageCount) {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .maxNumberOfMessages(messageCount)
                .waitTimeSeconds(3)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
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
                .queueUrl(QUEUE_URL)
                .receiptHandle(message.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteRequest);
        log.info("Deleted message with ID: {}", message.messageId());
    }
}
