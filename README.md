Using OpenAPI v3.0.0 for RESTful API documentation
==============

- Build and run

      mvn clean package 
      java -jar target/jax-rs-2.1-openapi-0.0.1-SNAPSHOT.jar


- Play with API using `Swagger UI`

      http://localhost:8080/api/api-docs?url=/api/openapi.json

- Play with API using `curl`

      curl -iv -X POST "http://localhost:8080/api/people" -H  "Accept: application/json" -H  "Content-Type: application/x-www-form-urlencoded" -d "email=a@b.com&firstName=John&lastName=Smith"

      curl -iv -X GET "http://localhost:8080/api/people" -H  "Accept: application/json"

      curl -iv -X GET "http://localhost:8080/api/people/a@b.com" -H  "accept: application/json"

      curl -iv -X DELETE "http://localhost:8080/api/people/a@b.com"
