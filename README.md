# mcs
A simple matchmaking service based on AWS Cloudsearch

I created this project to create a simple matchmaker using AWS Cloudsearch. The matchmaker is used to find other users using a variety of search criteria listed under "fields" below.

{
    "fields": {
        "elo_rating": 3121.44,
        "points": 404,
        "randomizer": 35,
        "last_login": "2014-10-09T22:57:57Z",
        "weapons": [
            "CANNON",
            "GUN"
        ]
    },
    "id": "8960ef26-5dd4-4882-9463-efd61e90ae0a",
    "type": "add"
}

This application demonstates how to run simple and faceted queries using the "structured search"

###H3 Requirements for using this project

1. Create a CloudSearch domain (https://docs.aws.amazon.com/cloudsearch/latest/developerguide/what-is-cloudsearch.html)
2. Upload the src/main/conf/matchmaker.json file into Cloudsearch to be indexed. 
a. Select elo_rating, points, randomizer, last_login and weapons as fields that need to be indexed.
b. This file will upload 5000 documents
3. Run a test query to validate upload
a. Match weapon, date range, points range and elo_rating : (and (or weapons:'GUN' weapons:'CANNON' weapons:'DRONE') (and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z']) (and points:[100, 200]) (and elo_rating:[1000, 2000]))
4. Record the Cloudsearch document and search endpoints.
5. Create AWS access key and secret key to access this service


###H3 Configuring the application

1. Running tests and the application. Configure the properties
```
cloudsearch.domain.search.endpoint=
cloudsearch.domain.document.endpoint=
cloudsearch.connection.accesskey=
cloudsearch.connection.secretkey=
```
in the the files
```
a. main/.../resources/application.properties
b. test/.../resources/application.properties
```
2. Setup VM parameters to run this as an application
```
-DCLOUDSEARCH.DOMAIN.SEARCH.ENDPOINT=<aws-search-endpoint>
-DCLOUDSEARCH.DOMAIN.DOCUMENT.ENDPOINT=<aws-document-endpoint> 
-DCLOUDSEARCH.CONNECTION.ACCESSKEY=<aws-access-key> 
-DCLOUDSEARCH.CONNECTION.SECRETKEY=<aws-secret-key>
```
c. Launch and run through browser or REST client
```
http://localhost:8025/mcs/v1/matchmaker?query={"queryString":"(and (or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')(and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z'])(and points:[100, 200])(and elo_rating:[1000, 2000]))","facets":"{\"weapons\":{\"sort\":\"count\",\"size\":5},\"points\":{\"buckets\":[\"[0,100]\",\"[101,200]\",\"[201,300]\",\"[301,400]\",\"[401,}\"]}}","cursor":"initial","numRecordsToReturn":10}
```

