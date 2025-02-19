# Alea Pokémon API v1.0

This project is a Spring Boot application that exposes RESTful APIs to retrieve Pokémon rankings based on three different criteria:

1. **Heaviest Pokémon**: Returns the 5 heaviest Pokémon.
2. **Tallest Pokémon**: Returns the 5 tallest Pokémon.
3. **Most Experienced** Pokémon: Returns the 5 Pokémon with the highest base experience.

The data is fetched from the public [PokéAPI](https://pokeapi.co/api/v2/) and is cached to reduce external API calls and improve response times. The application also demonstrates the use of concurrent fetching to efficiently retrieve detailed Pokémon data.

### Project Architecture

This project follows a hexagonal (ports and adapters) architecture. The application is structured into several layers:

- **Core Domain**: Contains the domain models and business logic.
- **Ports**: Defines interfaces for external systems (e.g., API calls, caching).
- **Adapters**: Implements the interfaces defined by the ports for communication with external systems such as the PokéAPI.
- **Application Services**: Orchestrates the business logic by leveraging the domain and external adapters.
This separation of concerns makes the system flexible, testable, and easy to maintain.

### Languages & Frameworks:

- **Java 21**: Modern Java features are leveraged for concise, robust code.
- **Spring Boot**: Rapid development of a production-ready microservice with embedded server and autoconfiguration.
- **JUnit 5 & Mockito**: Comprehensive unit and integration testing ensuring at least 90% test coverage.
- **RestAssured**: For integration testing of REST endpoints.
- **Swagger/OpenAPI**: Automatically generated API documentation for easier testing and integration.

### Concurrency & Caching:

- **ExecutorService**: Used to concurrently fetch Pokémon details.
- **Caffeine Cache**: Caches top-ranked Pokémon results to reduce redundant API calls.

### Features Implemented

#### Pokémon Rankings:
- **Heaviest**: Retrieves and caches the top 5 heaviest Pokémon by default.
- **Tallest**: Retrieves and caches the top 5 tallest Pokémon by default.
- **Most Experienced**: Retrieves and caches the top 5 Pokémon by default with the highest base experience.

#### Example Usage:
- Retrieve Top 5 Heaviest Pokémon:
```bash
curl -X GET 'http://localhost:8080/api/v1/pokemon/top-heaviest?topN=10' -H "Accept: application/json"
```
- Retrieve Top 5 Tallest Pokémon:
```bash
curl -X GET 'http://localhost:8080/api/v1/pokemon/top-tallest' -H "Accept: application/json"
```
 - Retrieve Top 5 Experienced Pokémon:
```bash
  curl -X GET 'http://localhost:8080/api/v1/pokemon/top-experienced' -H "Accept: application/json"
```

#### Response example:
```json
{"pokemonList":[{"name":"blissey","rankingValue":635},{"name":"eternatus-eternamax","rankingValue":563},{"name":"audino-mega","rankingValue":425},{"name":"chansey","rankingValue":395},{"name":"audino","rankingValue":390}],"count":5}
```

#### Custom Ranking Size:
Each endpoint supports a custom ranking size through the topN query parameter. 
If a client needs a different number of results, they can override the default by specifying the desired value. 
For example:

```
GET /api/v1/pokemon/top-heaviest?topN=10
```

#### Caching:
- Uses a caching strategy to store ranking results (via keys like pokemon:ranking:heaviest:5) to reduce redundant external API calls.

#### Concurrent Data Fetching:
- Uses an ExecutorService wrapped in a service that implements AutoCloseable to concurrently fetch Pokémon details.

#### Logging
- Detailed logging is implemented to track API call durations, pagination, and concurrent fetch operations.

#### Integration Tests:
- Tests using RestAssured to verify the correctness of API endpoints.
- Comprehensive unit tests with Mockito ensuring the business logic and error handling are robust.

#### API Documentation:
- Swagger/OpenAPI annotations to generate interactive API documentation for ease of testing and integration.


## Future Improvements

While the current implementation meets the test requirements, there are several enhancements planned:

- Enhanced Error Handling:
Improve error messages and incorporate fallback mechanisms in case the external PokéAPI is down or slow.

- Dynamic Caching Strategies:
Explore more dynamic caching policies such as cache invalidation, refreshing stale data, or distributed caching for scalability.

- Security Enhancements:
Add security measures (e.g., OAuth2 or JWT) to secure the API endpoints for production scenarios.

- Cloud Deployment:
Containerize the application using Docker and orchestrate with Kubernetes or ECS for scalable cloud deployments.

- Extended Test Coverage:
Increase integration tests, including performance benchmarks and stress tests, to ensure robustness in a production environment.

## Conclusion

This project demonstrates a production-ready implementation of a RESTful service using modern Java, Spring Boot, and robust testing practices. 
It fulfills the requirements of providing ranked Pokémon data (heaviest, tallest, and experienced) with high test coverage and a scalable design. 
Future enhancements will focus on improving resilience, security, and scalability as we move towards a fully cloud-native microservices architecture.
