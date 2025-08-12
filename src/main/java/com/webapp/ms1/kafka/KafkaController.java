package com.webapp.ms1.kafka;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kafka")
@AllArgsConstructor
public class KafkaController {

    private final KafkaService kafkaService;

    @PostMapping("/create-topic")
    public void sendMessage() {
//        kafkaService.createTopic();
    }

    @PostMapping("/produce/{topicName}")
    public List<String> getMessages() {
//        return kafkaService.produce();
        return null;
    }

    @PostMapping("/consume/{topicName}")
    public void deleteMessage() {
//        kafkaService.consume();
    }

}
