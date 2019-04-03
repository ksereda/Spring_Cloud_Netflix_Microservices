## Spring Cloud: Eureka Server
`Eureka Server` – This microservice application will provide service discovery and registration of above microservices.

`Eureka`: This is a name server or registry services. Duty - to give names to each microservice. Register microservices and give them ip to other microservices.

Thus, each service is registered in Eureka and sends an echo request to the Eureka server to report that it is active.

Client-side service discovery allows services to find and communicate with each other without hard coding hostname and port. 
The only ‘fixed point’ in such an architecture consists of a service registry with which each service has to register.

With `Netflix Eureka` each client can simultaneously act as a server, to replicate its status to a connected peer. 
In other words, a client retrieves a list of all connected peers of a service registry and makes all further requests to any other services through a load-balancing algorithm.


To implement a `Eureka Server` for using as service registry is as easy as: adding spring-cloud-starter-netflix-eureka-server to the dependencies, 
enable the Eureka Server in a `@SpringBootApplication` per annotate it with `@EnableEurekaServer` and configure some properties.

```
@EnableEurekaServer
@SpringBootApplication
public class EurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}

}
```

You should also specify additional settings (port) in the file perperties:

```
server.port=8761
```