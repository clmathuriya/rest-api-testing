###REST-API-Testing###

This repository contains framework to test REST APIs.

###Setup###
* clone this repository and import as maven project
* run ```mvn clean install``` to add dependencies


###Development###
* add endpoints in testData.xlsx
* provide url parameters, request data  and expected response in json format as below 
- url params in folder : src/main/resources/params/<any unique file name>.json
- request data in folder : src/main/resources/requests/<any unique file name>.json
- request data in folder : src/main/resources/responses/<any unique file name>.json

###running tests ###
* run ```mvn test```

###viewing reports###
* view report test-output/extent-reports/extent-report.html
