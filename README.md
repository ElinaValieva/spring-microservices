# Kotlin Spring Microservices [![Kotlin](https://img.shields.io/badge/Kotlin-1.3.72-orange.svg) ](https://kotlinlang.org/) ![Java CI with Gradle](https://github.com/ElinaValieva/spring-microservices/workflows/Java%20CI%20with%20Gradle/badge.svg)
> Based on methodology and patterns of [Chris Richardson](https://github.com/cer) and [Eventuate Framework](https://github.com/eventuate-tram/eventuate-tram-core)

![](https://github.com/ElinaValieva/spring-microservices/blob/master/schema.png)

&nbsp;

### Technologies 🚩
- Spring Boot, Spring Admin
- Spring Cloud Discovery, Spring Cloud Config
- Eventuate Tram - [CQRS, Sagas](https://eventuate.io/abouteventuatetram.html) with Kafka, Postgres

&nbsp;

### How to start 🐳
```shell script
docker-compose up
```
&nbsp;

### Deploy to OpenShift 🚩
```shell script
./gradlew helm
```
