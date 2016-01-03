# loggerbot

## Setup

Install the telegram libs locally:

```
$ git clone https://github.com/aldrinleal/JTeleBot-Core jtelebot-core
$ cd jtelebot-core
$ mvn install
```

Create config.properties:

```
beanstalk.s3Bucket=ingenieux-images
telegram.apiKey=<telegram api key>
telegram.defaultEndpoint=https://y7mewjnqfb.execute-api.us-east-1.amazonaws.com/dev/bot
telegram.bot.event_table=loggerbot
service.s3.uri=s3://loggerbot.ingenieux.io/feed/atom/%s.xml
service.base.uri=https://s3.amazonaws.com/loggerbot.ingenieux.io/feed/atom/%s.xml
```

Then:
  * Create a empty AWS API Gateway project, and deploy. Set the URL under defaultEndpoint
  * Create a dynamodb hash+range table called loggerbot, with chat_id (number) and update_id (number) as hash+range keys
  * Obtain a telegram api key. Set telegram.apiKey under config.properties
  * Create two buckets. One (for binary image) is set on beanstalk.s3Bucket while the other allows publicly access, refers to loggerbot.ingenieux.io. Remember to the set ACL accordingly

Update the IAM Role, like:

```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "logs:CreateLogGroup",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": "arn:aws:logs:*:*:*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "dynamodb:*"
            ],
            "Resource": "arn:aws:dynamodb:us-east-1:235368163414:table/loggerbot"
        },
        {
            "Effect": "Allow",
            "Action": "s3:PutObject",
            "Resource": "arn:aws:s3:::loggerbot.ingenieux.io/*"
        }
    ]
}
```

Save the file, then:

  * mvn -Pdeploy deploy

Back to API Gateway, create the resource /bot under the /dev environment, mapping to the loggerbot_message function. Deploy.

Then, from the AWS Lambda Console, call the loggerbot_register function with the following argument:

```
null
```

It should set accordingly the URL. In order to test, look under smoketail for the register function.

