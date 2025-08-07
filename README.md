# ms1

structure and config of application

1. redis - running locally
2. dynamodb instance on AWS
3. SQS on AWS

yet to be added :
elasticsearch
kafka
rds/postgres
cdn
s3
lambda


// short url working
uses Redis for caching : while getting long url for shortUrl, first fetched from redis, if not found then fetched from DynamoDB
uses DynamoDB for persistent storage: messages are picked from SQS and processed, which is then saved into DynamoDB
uses SQS for processing messages: while creating short url, message is sent to SQS for processing, saved into Redis first

// working for alias
first take lock in redis to avoid concurrent processing of same alias, then check if alias exists in DynamoDB/Redis, 
if not then create new alias and save it in DynamoDB, then release the lock