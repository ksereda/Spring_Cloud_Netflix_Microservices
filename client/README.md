## Spring Cloud: Config Client

## RUS

Для начала создадим сервер и клиент (Я использовал start.spring.io)
Добавляем зависимости:

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

### Настраиваем сервер:

Мы должны использовать аннотацию `@EnableConfigServer` в основном классе

	
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

В файле `application.yml` нам нужно выбрать порт, имя и локальный URL-адрес git для сервера конфигурации Spring Cloud.
Нам также нужно указать доступ к базе данных (я использовал MySQL). Я также использовал пул соединений (hikari).

Смотрите `application.yml` для получения дополнительной информации:

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


Я использовал `FlyWay` для управления миграцией базы данных.
Вам нужно создать `db.migration` в папке ресурсов.
Файл должен быть назван с большой буквы `V1__Base_version.sql` (обратите внимание, после V1 есть 2 подчеркивания, а затем укажите «имя вашего файла»):

```
V1__Base_version.sql
```


### Настройка клиента:

Создадим простейший `REST` контроллер

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

или

```
@Component
@RefreshScope
@RestController
public class ScheduleTaskController {

    @Value("${myname}")
    private String name;

    @RequestMapping("/name")
    String getValue() {
        return this.name;
    }

    @Scheduled(fixedRate = 1000)
    public void showValue(){
        System.out.println(name);
    }

}
```

В файле `application.yml` вам нужно указать порт, имя и адрес сервера eureka:

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

Нам также понадобиться для примера создать локальный гит репозиторий.

В консоли:

```
git init
```

Создайте в этом репозитории файл `application.properties`:

```
myname=helloWorld
```

Следующий шаг (в консоли):

```
git commit -m "firstcommit"
```

Также настройте файл `bootstrap.properties` на клиенте (если его нет, создайте):

    # N.B. this is the default:
    spring.cloud.config.uri=http://localhost:8888
    
    # For GIT repo:
    spring.cloud.config.server.git.uri=/home/tmp/serverrepo
    
    # For File System:
    # spring.profiles.active=native
    # spring.cloud.config.server.native.searchLocations=/tmp/config-server



### Тестируем:

Запустите сервер и клиент.
Запустите эту команду из консоли (172.17.0.1 - это IP-адрес моего ПК):

```
curl 172.17.0.1:8888/config_server/myname
```

Затем создайте того же второго клиента и попробуйте получить доступ ко второму клиенту с первого клиента:

```
curl localhost:8888/service-instances/config_client
       or
curl localhost:8888/service-instances/config_client_2
```
или

```
curl localhost:8888/name
```

Вы получите информацию из файла в локальном репозитории:

    /home/tmp/serverrepo


______________________________________________________


## ENG

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

### Customize server:

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

In `application.yml` file we need choose port, name and local git url for spring cloud config server.
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
	

I used `FlyWay` for managing database migrations.
You need create `db.migration` in resources folder.
The file should be named with the capital `V1__Base_version.sql` (note, after V1 there are 2 underscores and then specify "your file name"):

```
V1__Base_version.sql
```


### Customize client:

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

or

```
@Component
@RefreshScope
@RestController
public class ScheduleTaskController {

    @Value("${myname}")
    private String name;

    @RequestMapping("/name")
    String getValue() {
        return this.name;
    }

    @Scheduled(fixedRate = 1000)
    public void showValue(){
        System.out.println(name);
    }

}
```

In `application.yml` file you need choose port, name and eureka server address:

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



We also create a local git rep, we do from console:

```
git init
```

Create `application.properties` file:

```
myname=helloWorld
```

Next step: from console:

```
git commit -m "firstcommit"
```

Configure `bootstrap.properties` on client:

	# N.B. this is the default:
    spring.cloud.config.uri=http://localhost:8888
    
    # For GIT repo:
    spring.cloud.config.server.git.uri=/home/tmp/serverrepo
    
    # For File System:
    # spring.profiles.active=native
    # spring.cloud.config.server.native.searchLocations=/tmp/config-server
    

 
### Testing: 

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
or

```
curl localhost:8888/name
```
You will receive info from file in local repository
    
    /home/tmp/serverrepo
