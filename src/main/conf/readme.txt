Import this projet into your favourite IDE

Set the following values in the following files

1. main/.../resources/application.properties
2. test/.../resources/application.properties

cloudsearch.domain.search.endpoint=
cloudsearch.domain.document.endpoint=
cloudsearch.connection.accesskey=
cloudsearch.connection.secretkey=

To run this as an application, set the following VM parameters
-DCLOUDSEARCH.DOMAIN.SEARCH.ENDPOINT=<aws-search-endpoint> -DCLOUDSEARCH.DOMAIN.DOCUMENT.ENDPOINT=<aws-document-endpoint> -DCLOUDSEARCH.CONNECTION.ACCESSKEY=<aws-access-key> -DCLOUDSEARCH.CONNECTION.SECRETKEY=<aws-secret-key>

To test search from browser:
http://localhost:8025/mcs/v1/matchmaker?query={"queryString":"(and (or weapons:'GUN' weapons:'CANNON' weapons:'DRONE')(and last_login:['2013-05-25T00:00:00Z','2014-10-25T00:00:00Z'])(and points:[100, 200])(and elo_rating:[1000, 2000]))","facets":"{\"weapons\":{\"sort\":\"count\",\"size\":5},\"points\":{\"buckets\":[\"[0,100]\",\"[101,200]\",\"[201,300]\",\"[301,400]\",\"[401,}\"]}}","cursor":"initial","numRecordsToReturn":10}