## Spring Cloud: Eureka Server

## RUS

`Eureka Server` - это приложение, которое содержит информацию обо всех клиентских сервисных приложениях. 
Каждый смикросервис регистрируется на сервере Eureka, и Eureka знает все клиентские приложения, работающие на каждом порту и IP-адресе. 
Eureka Server также известен как Discovery Server.

Его аналоги:
- Consul
- Zookeeper
- Cloud Foundry

Если простыми словами, то - `это сервер имен или реестр сервисов. Обязанность - давать имена каждому микросервису.`
Регистрирует микросервисы и отдает их ip другим микросервисам.

Таким образом, каждый сервис регистрируется в Eureka и отправляет эхо-запрос серверу Eureka, чтобы сообщить, что он активен.
Для этого сервис должен быть помечен как `@EnableEurekaClient`, а сервер `@EnableEurekaServer`.
При указании аннотаций `@EnableDiscoveryClient` тоже отработает, т.к. Eureka является Discovery сервисом, но вот в случае если использовать любой другой Dicovery сервис и использовать аннотацию `@EnableEurekaClient`, так уже не прокатит.


###Минимальные настройки:

В файле `application.properties`:

    # По умолчанию использует порт 8761
    server.port=8761
    
    eureka.client.register-with-eureka=false
    eureka.client.fetch-registry=false


или исопльзовать `application.yml` файл:

    eureka:
       client:
          registerWithEureka: false
          fetchRegistry: false
    server:
       port: 8761

Также необходимо указать соответствующую аннотацию `@EnableEurekaServer` в основном файле проекта.

    @EnableEurekaServer
    @SpringBootApplication
    public class EurekaApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(EurekaApplication.class, args);
    	}
    
    }

Это минимальные необходимые настройки для запуска Eureka Server.

Запустите проект и перейдите по адресу

    http://localhost:8761

и вы увидите веб интерфейс, в котором можно получить всю необходимую информацию по микросервисам, которые будут зарегистрированы в Eureka Server.


Аннотация `@EnableEurekaClient` сообщает платформе следующее: 
Что данный сервис является экземпляром какого-то микросервиса и просит зарегистрировать его на сервере Eureka, также хочет узнать другие службы, которые зарегистрированы в Eureka Server.


### Пару слов про понятия, в которых многие путаются.

- Eureka Server

Он содержит реестр служб и REST API, которые можно использовать для регистрации службы, отмены регистрации службы и определения местоположения других служб.

- Eureka Service

Любое приложение, которое можно найти в реестре служб Eureka Server и которое может быть обнаружено другими службами. Служба имеет определенный ID (его еще называют VIP) который может ссылаться на один или несколько экземпляров одного и того же приложения.

- Eureka Instance

Любое приложение, которое регистрируется на Eureka Server для обнаружения другими.

- Eureka Client

Любое приложение, которое может обнаружить службы. Он только запрашивает реестр служб у Eureka Server, чтобы определить запущенные экземпляры микросервисов.

Приложение может быть как `Eureka Instance` и `Eureka Client` одновременно, приложениям часто нужно сделать себя доступными для использования другими (чтобы они были экземпляром), и в то же время им нужно обнаружить другие службы (чтобы они были клиентами).
Но `Eureka Client` не должен являться экземпляром `Eureka Instance`, т.к. иногда приложение не может ничего предложить другим и оно только вызывает другие сервисы.
Можно запретить ему регистрироваться в качестве экземпляра:

    eureka.client.register-with-eureka = false

Другими словами `Eureka Client` регистрируется в `Eureka Server`.
Поскольку `Eureka Instance` регистрируется в `Eureka Server`, он тоже является клиентом.
Т.к. `Eureka Service` предлагает API другим, следовательно его могут обнаружить другие, поэтому он является экземпляром.

### Как это работает ?

По умолчанию клиент Eureka запускается в состоянии `STARTING`, что дает экземпляру возможность выполнить инициализацию для конкретного приложения, прежде чем он сможет обслуживать трафик.
`Eureka Client` сначала пытается связаться с сервером Eureka в той же зоне в облаке AWS `(по умолчанию)`, но если он не может найти сервер, он переключается на другие зоны.
`Eureka Client` сбрасывает все HTTP-соединения, которые простаивали более 30 секунд, которые он создал для связи с сервером.

### Клиент взаимодействует с сервером следующим образом:
1) `Eureka Client` регистрирует информацию о запущенном экземпляре на `сервере Eureka`. 

2) `Каждые 30` секунд клиент отсылает запрос на сервер и информирует сервер о том, что экземпляр еще жив.
Если сервер не видел обновления в течение 90 секунд, он удаляет экземпляр из своего реестра.

3) `Eureka Client` получает информацию реестра от сервера и кэширует ее у себя локально. 
Эта информация обновляется периодически (каждые 30 секунд), получая обновления между последним апдейтом и текущим. 
Клиент автоматически обрабатывает дублирующую информацию.

4) Получив обновления, клиент сверяет информацию с сервером, сравнивая количество экземпляров, возвращаемых сервером, и если информация по какой-либо причине не совпадает, вся информация реестра извлекается снова.
Клиент получает информацию в сжатом формате `JSON`, используя клиент `jersey apache`.

5) При завершении работы клиент отправляет запрос отмены на сервер. 
Таким образом экземпляр удаляется из реестра экземпляров сервера.
Eureka использует протокол, который требует, чтобы клиенты выполняли явное действие отмены регистрации.


    DiscoveryManager.getInstance().shutdownComponent()
        
Клиенты, которые использовали 3 неудачные попытки синхронизации с сервером с интервалом в 30 секунд будут удалены автоматически.

По умолчанию `Eureka Client` используют `Jersey` и `Jackson` вместе с `JSON` для связи с `Eureka Server`. 
Можно переопределить механизм по умолчанию, чтобы натсроить свой кастомный. 
    

Есть такое понятие, как `время запаздывания (или Time Lag)`:
Все операции с `Eureka Client` могут занять некоторое время для отражения на `Eureka Server`, а затем и на других `Eureka Client`. 
Это связано с кэшированием полезных данных на сервере, которые периодически обновляются для отображения новой информации. 
Клиенты также периодически получают эти обновления. В итоге может потребоваться `до 2 минут`, чтобы изменения распространялись на всех клиентов.


Режим `самосохранения`:
`Eureka Server` перейдет в режим самосохранения, если обнаружит, что зарегистрированное количество клиентов превысило ожидаемое количество и прервало соединения для связи с сервером.
Это было сделано длля того, чтобы гарантировать, что какие-либо сетевые события (большие нагрузки или проблемы в сети) не уничтожат данные реестра `Eureka Server`.

Чтобы установить порог самосохранения, надо указать свойство в файле properties: 

    eureka.renewalPercentThreshold=[0.0, 1.0]
    
Чтобы отключить режим самосохранения, необходимо:

    eureka.enableSelfPreservation=false
    

Как уже упоминалось выше, клиенты пытаются установить связь с сервером в той же зоне. 
Если возникают проблемы при общении с сервером или если сервер не существует в той же зоне, клиенты переключаются на серверы в других зонах.

### Взаимодействие серверов между собой

`Серверы Eureka` взаимодействуют друг с другом, используя тот же механизм, который используется между клиентом и сервером.
Когда сервер запускается, он пытается получить всю информацию реестра экземпляра от соседнего узла. 
Если при получении информации от узла возникает проблема, сервер проверяет все равноправные узлы.
В случае проблем сервер пытается защитить уже имеющуюся у него информацию.
Например может быть сценарий массового отключения, в результате которого клиенты могут получить экземпляры, которые больше не существуют.
Лучшая защита при таком сценарии - быстрое отключение и проверка других серверов.
Когда сервер запускается и в случае, когда он не может получить информацию о реестре от соседнего узла, он ждет 5 минут, чтобы клиенты могли зарегистрировать свою информацию.

Если появляются проблемы в сети, то между узлами могут возникнуть следующие проблемы:

- эхо-запросы между узлами могут завершиться неудачно, и сервер перейдет в режим самосохранения, защищая свое текущее состояние.

- регистрация клиентов может происходить на потерянном сервере


### Безопасность

Если вы хотите подключиться по HTTPS, вы можете прописать:

    eureka.instance.[nonSecurePortEnabled]=[false]
    eureka.instance.[securePortEnabled]=[true]
    
В application.yml можно использовать placeholders:
  
      eureka:
        instance:
          statusPageUrl: https://${eureka.hostname}/info
          healthCheckUrl: https://${eureka.hostname}/health
          homePageUrl: https://${eureka.hostname}/
          

### Проверка состояния:

После успешной регистрации Eureka всегда объявляет, что приложение находится в состоянии «UP». 
Это поведение можно изменить, включив проверки работоспособности Eureka, в результате чего статус приложения передается в Eureka.

    eureka:
      client:
        healthcheck:
          enabled: true
          

### Работы клиентов в других зонах:

Если клиенты Eureka находятся в нескольких зонах, можно сделать так, чтобы эти клиенты использовали службы в той же зоне, где они сами находятся, прежде чем пытаться использовать службы в другой зоне. 
Чтобы это настроить, необходимо правильно настроить клиенты.

    # Служба А в Зоне А
    
    eureka.instance.metadataMap.zone = zone_A
    eureka.client.preferSameZoneEureka = true
    
    # Служба А в зоне B
    
    eureka.instance.metadataMap.zone = zone_B
    eureka.client.preferSameZoneEureka = true


Компания `Netflix` предоставляет нам использование текущей конфигурации по умолчанию "из коробки", но вы также можете написать свою кастомную реализацию:

Клиент: 
    
    https://github.com/Netflix/eureka/blob/master/eureka-client/src/main/java/com/netflix/discovery/DefaultEurekaClientConfig.java
    
Сервер:

    https://github.com/Netflix/eureka/blob/master/eureka-core/src/main/java/com/netflix/eureka/DefaultEurekaServerConfig.java
    

_____________________________________

### ENG

`Eureka Server` is an application that contains information about all client service applications.
Each microservice is registered on the Eureka server, and Eureka knows all client applications running on each port and IP address.
Eureka Server is also known as Discovery Server.

Its analogues are:
- Consul
- Zookeeper
- Cloud Foundry

If in simple words, then - `this is a name server or registry services. Responsibility - to give names to every microservice.`
Registers microservices and gives them ip to other microservices.

Thus, each service is registered with Eureka and sends a ping request to the Eureka server to report that it is active.
To do this, the service must be marked as `@ EnableEurekaClient`, and the server is @ @ EnableEurekaServer`.
When specifying annotations, `@ EnableDiscoveryClient` will also work because Eureka is a Discovery service, but if you use any other Dicovery service and use the `@ EnableEurekaClient` annotation, it will not work.


### Minimum Settings:

In the file `application.properties`:

        # By default, it uses port 8761
        server.port = 8761
        
        eureka.client.register-with-eureka = false
        eureka.client.fetch-registry = false


or use the `application.yml` file:

        eureka:
           client:
              registerWithEureka: false
              fetchRegistry: false
        server:
           port: 8761

You must also specify the corresponding annotation `@ EnableEurekaServer` in the main project file.

        @EnableEurekaServer
        @SpringBootApplication
        public class EurekaApplication {
        
        public static void main (String [] args) {
        SpringApplication.run (EurekaApplication.class, args);
        }
        
        }

These are the minimum required settings for running Eureka Server.

Run the project and go to
    
    http: // localhost: 8761

and you will see a web interface where you can get all the necessary information on microservices, which will be registered in Eureka Server.


Annotation `@ EnableEurekaClient` tells the platform the following:
That this service is an instance of a microservice and asks to register it on the Eureka server, also wants to find out other services that are registered with the Eureka Server.


### A few words about concepts in which many are confused.

- Eureka Server

It contains a registry of services and REST APIs that you can use to register a service, unregister a service, and locate other services.

- Eureka Service

Any application that can be found in the registry of Eureka Server services and which can be detected by other services. The service has a specific ID (it is also called VIP) that can refer to one or more instances of the same application.

- Eureka Instance

Any application that registers with Eureka Server for detection by others.

- Eureka Client

Any application that can discover services. It only queries the services registry from Eureka Server to determine the running instances of microservices.

An application can be both `Eureka Instance` and` Eureka Client` at the same time, applications often need to be made available for use by others (so that they are an instance), and at the same time they need to discover other services (so that they are clients).
But the `Eureka Client` should not be an instance of` Eureka Instance`, since sometimes the application can not offer anything to others and it only calls other services.
You can prevent him from registering as an instance:

    eureka.client.register-with-eureka = false

In other words, `Eureka Client` registers with` Eureka Server`.
Since `Eureka Instance` registers with` Eureka Server`, it is also a client.
Because `Eureka Service` offers API to others, therefore others can detect it, so it is an instance.

### How it works ?

By default, the Eureka client runs in the `STARTING` state, which allows the instance to initialize for a specific application before it can handle the traffic.
ʻEureka Client` first tries to contact the Eureka server in the same zone in the AWS cloud `(default)`, but if it cannot find the server, it switches to other zones.
`Eureka Client` resets all HTTP connections that have been idle for more than 30 seconds that it created to communicate with the server.


### The client interacts with the server as follows:
1) `Eureka Client` logs information about the running instance on the` Eureka` server.

2) `Every 30` seconds, the client sends a request to the server and informs the server that the instance is still alive.
If the server has not seen the update within 90 seconds, it deletes the instance from its registry.

3) `Eureka Client` receives registry information from the server and caches it locally.
This information is updated periodically (every 30 seconds), receiving updates between the latest update and the current one.
The client automatically processes duplicate information.

4) After receiving updates, the client checks the information with the server, comparing the number of instances returned by the server, and if the information for any reason does not match, all the registry information is retrieved again.
The client receives information in the compressed `JSON` format using the` jersey apache` client.

5) When shutting down, the client sends a cancel request to the server.
In this way, the instance is removed from the registry of server instances.
Eureka uses a protocol that requires customers to perform an explicit deregistration action.
    
    
    DiscoveryManager.getInstance (). ShutdownComponent ()
        
Clients who have used 3 unsuccessful attempts to synchronize with the server at an interval of 30 seconds will be deleted automatically.

By default, `Eureka Client` uses` Jersey` and `Jackson` along with` JSON` to communicate with `Eureka Server`.
You can override the default mechanism to customize your custom one.
    

There is such a thing as `lag time (or Time Lag)`:
All operations with `Eureka Client` may take some time to reflect on` Eureka Server`, and then on the other `Eureka Client`.
This is due to the caching of useful data on the server, which is periodically updated to display new information.
Customers also receive these updates periodically. As a result, it may take up to 2 minutes for the changes to apply to all clients.


`Self-preservation` mode:
`Eureka Server` will go into self-save mode, if it detects that the number of registered clients has exceeded the expected number and has broken connections to communicate with the server.
This was done to ensure that any network events (heavy loads or network problems) do not destroy the `Eureka Server` registry data.

To set the threshold for self-preservation, you must specify a property in the properties file:
    
    eureka.renewalPercentThreshold = [0.0, 1.0]
    
To disable the self-saving mode, you must:
    
    eureka.enableSelfPreservation = false
    
As mentioned above, clients are trying to connect to the server in the same zone.
If problems arise when communicating with the server or if the server does not exist in the same zone, the clients switch to servers in other zones.

### Interaction between servers

`Eureka servers` interact with each other using the same mechanism that is used between the client and the server.
When the server starts up, it tries to get all the registry information of the instance from the neighbor.
If there is a problem getting information from the node, the server checks all peers.
In case of problems, the server tries to protect the information it already has.
For example, there may be a mass disconnect scenario, as a result of which clients may receive instances that no longer exist.
The best protection in this scenario is to quickly shut down and check other servers.
When the server is started and in the case when it cannot get the registry information from the neighboring node, it waits 5 minutes for the clients to register their information.

If there are problems in the network, the following problems may arise between the nodes:

- Ping between nodes may fail, and the server will go into self-save mode, protecting its current state.

- client registration can occur on a lost server


### Security

If you want to connect via HTTPS, you can register:

        eureka.instance. [nonSecurePortEnabled] = [false]
        eureka.instance. [securePortEnabled] = [true]
    
You can use placeholders in application.yml:
  
          eureka:
            instance:
              statusPageUrl: https: // $ {eureka.hostname} / info
              healthCheckUrl: https: // $ {eureka.hostname} / health
              homePageUrl: https: // $ {eureka.hostname} /
          

### Status check:

After successful registration, Eureka always announces that the application is in the “UP” state.
This behavior can be changed by enabling the Eureka health checks, which results in the status of the application being transferred to Eureka.

        eureka:
          client:
            healthcheck:
              enabled: true
          

### Clients work in other areas:

If Eureka customers are in multiple zones, you can make these clients use services in the same zone where they are located before trying to use services in another zone.
To set this up, clients need to be properly configured.

        # Service A in Zone A
        
        eureka.instance.metadataMap.zone = zone_A
        eureka.client.preferSameZoneEureka = true
        
        # Service A in Zone B
        
        eureka.instance.metadataMap.zone = zone_B
        eureka.client.preferSameZoneEureka = true



The company `Netflix` provides us with the use of the current default configuration" out of the box ", but you can also write your custom implementation:

Customer:
    
    https://github.com/Netflix/eureka/blob/master/eureka-client/src/main/java/com/netflix/discovery/DefaultEurekaClientConfig.java
    
Server:

    https://github.com/Netflix/eureka/blob/master/eureka-core/src/main/java/com/netflix/eureka/DefaultEurekaServerConfig.java