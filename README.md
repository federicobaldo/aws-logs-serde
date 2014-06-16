aws-logs-serde
==============

Hive deserializer for various Aws log formats.

### Usage

Inside your script or from the Hive interactive shell you should add first the jar and then create the table specifying
the row format.

```
add jar /home/hadoop/aws-logs-serde.jar;

create table s3_accesslogs(
bucketowner             string,
bucketname              string,
rdatetime               string,
rip                     string,
requester               string,
requestid               string,
operation               string,
rkey                    string,
requesturi              string,
httpstatus              int   ,
errorcode               string,
bytessent               int   ,
objsize                 int   ,
totaltime               int   ,
turnaroundtime          int   ,
referer                 string,
useragent               string,
versionid               string
)
ROW FORMAT SERDE 'com.amazonaws.hive.serdes.s3.S3LogDeserializer'
LOCATION 's3://<yours3bucket>/<yourprefixifany>/';
```


### Status
[![Build Status](https://travis-ci.org/federicob/aws-logs-serde.svg?branch=master)](https://travis-ci.org/federicob/aws-logs-serde)
