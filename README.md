# ms1

structure and config of application

1. redis - running locally (required to run locally before application start)
   connect to local redis using : ```docker exec -it my-redis redis-cli```
2. dynamodb instance on AWS (always running on AWS)
3. SQS on AWS (always running on AWS)
4. MongoDB - connects with cluster running on cloud.mongodb.com
   to connect, you have add password in connection string in application.properties file, password is stored in 
   local-settings-info.txt file on local system

yet to be added :
elasticsearch
kafka
rds/postgres
cdn
**s3
lambda
zk
flink
timeseries DB


// short url working
uses Redis for caching : while getting long url for shortUrl, first fetched from redis, if not found then fetched from DynamoDB
uses DynamoDB for persistent storage: messages are picked from SQS and processed, which is then saved into DynamoDB
uses SQS for processing messages: while creating short url, message is sent to SQS for processing, saved into Redis first

// working for alias
first take lock in redis to avoid concurrent processing of same alias, then check if alias exists in DynamoDB/Redis, 
if not then create new alias and save it in DynamoDB, then release the lock

Steps to build and push docker image:
1. build new docker image using 
         "docker build -t coderraghvendra/app-ms1:v1.1.3 ."
2. push docker image
         "docker push coderraghvendra/app-ms1:v1.1.3"