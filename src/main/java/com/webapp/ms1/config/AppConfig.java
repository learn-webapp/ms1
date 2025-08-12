package com.webapp.ms1.config;

import com.webapp.ms1.json.JsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.Map;
import java.util.Properties;

@Slf4j
public class AppConfig {

    private static final Region AWS_REGION = Region.AP_SOUTH_1;
    private static final String AWS_SECRET_NAME = "app-secret-json";
    private static Map<String, String> AWS_SECRET_MAP;

    @Getter
    private static String shorturlQueueUrl;

    @Getter
    private static S3Client s3Client;

    @Getter
    private static SqsClient sqsClient;

    @Getter
    private static DynamoDbClient dynamoDbClient;

    public static void initialize(ApplicationPreparedEvent event) throws Exception {
        // fetch secrets json from AWS Secrets Manager
        AWS_SECRET_MAP = fetchSecretsFromSecretsManager();
//        log.info("aws secret map fetched: {}", AWS_SECRET_MAP);
        shorturlQueueUrl = AWS_SECRET_MAP.get("shorturl.queue.url");

        initializeAwsClients();
//        initializeDatabaseConfig(event);
    }

    public static Map<String, String> fetchSecretsFromSecretsManager() throws Exception {
        try {
            SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
                    .region(AWS_REGION)
                    .build();
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(AWS_SECRET_NAME)
                    .build();
            GetSecretValueResponse getSecretValueResponse = secretsManagerClient.getSecretValue(getSecretValueRequest);
            String secret = getSecretValueResponse.secretString();
            return JsonUtil.parseJsonToMap(secret);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            log.error("Exception while fetching secrets: ", e);
            throw e;
        }
    }

    public static void initializeAwsClients() {
        String awsAccessKey = AWS_SECRET_MAP.get("aws.access.key");
        String awsSecretKey = AWS_SECRET_MAP.get("aws.secret.key");
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials
                .create(awsAccessKey, awsSecretKey));

        s3Client = S3Client.builder()
                .credentialsProvider(staticCredentialsProvider)
                .region(AWS_REGION)
                .build();
        sqsClient = SqsClient.builder()
                .region(AWS_REGION)
                .credentialsProvider(staticCredentialsProvider)
                .build();
        dynamoDbClient = DynamoDbClient.builder()
                .region(AWS_REGION)
                .credentialsProvider(staticCredentialsProvider)
                .build();
    }

    public static void initializeDatabaseConfig(ApplicationPreparedEvent event) {
        log.info("database config initialization started ...");
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        Properties props = new Properties();
        props.put("spring.datasource.url", AWS_SECRET_MAP.get("database.url"));
        props.put("spring.datasource.username", AWS_SECRET_MAP.get("database.username"));
        props.put("spring.datasource.password", AWS_SECRET_MAP.get("database.password"));
        environment.getPropertySources().addFirst(new PropertiesPropertySource("db.props", props));
        log.info("database config initialization completed");
    }
}
