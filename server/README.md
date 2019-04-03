## Spring Cloud: Config Server

We create server and client (I used start.spring.io). 
Add dependencies:

Server:
```
Web
Config Server
```
Client:
```
Web
Config Client
```

Customize `server`:

We need an annotation `@EnableConfigServer` in the main class.
	
```
@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}

```

In application.yml file we need choose port, name and local git url for spring cloud config server.
we also need to specify access to the database (I used MySQL). I also used a connection pool (hikari).
See application.yml for more information:
	
	spring.application.name: config_server
    
    server:
      port: 8888
    
    eureka:
      client:
        serviceUrl:
          defaultZone: http://localhost:8761/eureka/
    
    spring:
      jpa.hibernate.ddl-auto: create
      datasource:
        url: jdbc:mysql://localhost:3306/properties?createDatabaseIfNotExist=true
        username: root
        password: root
        driver-class-name: com.mysql.jdbc.Driver
        hikari:
          connection-timeout: 5000
          maximum-pool-size: 10
    
      cloud:
        config:
          server:
            default-profile: local
            default-label: latest
            jdbc:
              sql: SELECT `key`, `value` FROM `properties` WHERE `application`=? AND `profile`=? AND `label`=?;
              order: 0
    
      profiles:
        active:
          - jdbc
	

I used FlyWay for managing database migrations.
You need create db.migration in resources folder.
The file should be named with the capital V1__Base_version.sql (note, after V1 there are 2 underscores and then specify "your file name"):

```
V1__Base_version.sql
```

_____________________________________________

Customize `client`:

Create simple `REST` controller:

```
@Component
@RestController
@RefreshScope
public class ServiceInstanceRestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        return this.discoveryClient.getInstances(applicationName);
    }

    @RequestMapping("/hello")
    public String getHelloWorld() {
        return "Hello world!";
    }

}
```

In application.yml file you need choose port, name and eureka server address:

```
spring:
  application:
    name: config_client
  cloud:
    config:
      label: latest

server:
  port: 8083


eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

```

_____________________________________________


We also create a local git rep, we do from console:

```
git init
```

Create application.properties file:

```
myname=helloWorld
```

Next step: from console:

```
git commit -m "firstcommit"
```

Configure bootstrap.properties on client:

	# N.B. this is the default:
    spring.cloud.config.uri=http://localhost:8888
    
_____________________________________________

 
Testing: 

Start this command from console (172.17.0.1 - this is ip address of my PC):

```
curl 172.17.0.1:8888/config_server/myname
```

Then create the same second client and try to access the second client from the first client:

```
curl localhost:8888/service-instances/config_client
       or
curl localhost:8888/service-instances/config_client_2
```

