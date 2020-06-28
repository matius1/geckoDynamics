# Purpose
Assess candidate code style and check basic Spring skill.

#Short description
ots from one client, validate and persist data in storage, distribute persisted data to other clients via REST interface.

# Requirements details
Languages, tools and frameworks to be used: Java 8+, Maven/Gradle, Spring Boot, any DB (eg H2, postgres).
Use of other framework/libraries at the discretion of the developer.

# Scenarios to be implemented
	•	As client I want to upload plain text file with comma-separated data via HTTP request
	•	First line of file will contain header: PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP
	•	Last line of file always to be empty
	•	All other lines will contain four values what represents single record to be persisted
	•	As client I want access data persisted via HTTP request
	•	Values of single record to be provided for PRIMARY_KEY supplied via request URL
	•	Paginated list of records at specific time period
	•	As service owner I want to remove record from storage via HTTP request by single PRIMARY_KEY for reconciliation purpose
	•	As service owner I want prevent persistence of all records from client-file what contains invalid rows

# How to share yur work
Please, create a git repository using free online service (GitHub, Bitbucket, etc.). This repository should contain all source files and maven/gradle build script.

# Planned efforts
2h-3h


#TODO:
* install Lombok plugin