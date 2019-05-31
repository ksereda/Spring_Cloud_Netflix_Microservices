# Ribbon Client

### RU

`Ribbon` — это `client-side` балансировщик. По сравнению с традиционным, здесь запросы проходят напрямую по нужному адресу. 
`"Из коробки"` он интегрирован с механизмом Service Discovery, который предоставляет динамический список доступных инстансов для балансировки между ними.

Предоставляет:

- Отказоустойчивость

- Load balancing - Балансировки нагрузки

- Поддержка нескольких протоколов (HTTP, TCP, UDP) в асинхронной и реактивной модели

- Кеширование

и т.д.


### Типы балансировщика

Load Balancer (балансировщик нагрузки) бывает 2 типов:

- `Server side Load Balancer`

Load Balancer настраивается на стороне сервера.
Когда запросы поступают от клиента, они придут к Load Balancer и он уже в свою очередь определит соответствующий сервер для этого запроса.

- `Client-Side Load Balancer`

Когда балансирвока нагрузки находится на стороне Client и она сама решает к какому серверу отправить запрос, основываясь на некоторых критериях.
Балансировка на клиентской стороне обычно отправляет запросы к серверам одной зоны (Zone), или имеет быстрый ответ.

Вы можете выбрать любой алгоритм балансировки нагрузки. 
`Ribbon` предоставляет несколько реализаций:

- Simple Round Robin LB, 

- Weighted Response Time LB, 

- Zone Aware Round Robin LB,

- Random LB.
 

`Ribbon` решает как сервер будет вызван (из списка фильтрованных серверов).
Есть несколько стратегий для решения. По умолчанию используется стратегия `"ZoneAwareLoadBalancer" (Сервера в одной зоне с клиентом)`.


- `Фильтрованный список серверов (Filtered List of Servers):`

	Например одному клиенту нужна информация погоды и есть список серверов, которые могут предоставить эту информацию. Но не все эти сервера работают, или тот сервер, который нам нужен, находится слишком далеко от Client, из-за этого отвечает очень медленно. Client отбросит эти сервера из списка, и в конце будет список более подходящих серверов (Фильтрованнный список).

- `Ping:`

	Ping - это способ, который клиент использует для быстрой проверки: работает ли на тот момент сервер или нет? 
	Eureka автоматически проверяет эту информацию.


Каждый Ribbon Client имеет свой `ApplicationContext`, который поддерживает Spring Cloud. При первом запросе он использует ленивую загрузку. 
Ленивую загрузку можно отключить:

    ribbon:
      eager-load:
        enabled: true
        clients: client1, client2, client3
        
Spring Cloud создает новый `ApplicationContext` для каждого Ribbon Client с помощью `RibbonClientConfiguration`.

С помощью аннотации `@RibbonClient` можно получить полный контроль над клиентом, объявив дополнительную конфигурацию `(поверх RibbonClientConfiguration)`.

    @RibbonClient(name = "ping-server", configuration = RibbonConfiguration.class)
    
Мы также можем указать настройки по умолчанию ​​для всех Ribbon Client:

    @RibbonClients(defaultConfiguration = DefaultRibbonConfig.class)
    
Также, начиная с версии 1.2.0, можно настроить конфигурацию через properties.
Классы, определенные в свойствах имеют приоритет над бинами, определенными с помощью аннотации `@RibbonClient`.


Ribbon Client может использоваться совмесно с Eureka: в таком случае он будет получать список всех сервисов, зарегистрированных в Eureka. 
Он имеет `интерфейс IPing`, который делегирует Eureka, чтобы определить, работает ли сервер.

Вы можете отключить Ribbon Client от Eureka:

    ribbon:
      eureka:
       enabled: false
       
В таком случае укажите аннотацию `@RibbonClient(name = "myService")` и в файле application.properties
    
    myservice.ribbon.eureka.enabled=false
    myservice.ribbon.listOfServers=http://localhost:5000, http://localhost:5001    


### Чем отличаются аннотации @RibbonClient и @LoadBalanced ?

- `@LoadBalanced`
Используется как аннотация маркера, которая указывает на то, что аннотированный ею RestTemplate должен использовать `RibbonLoadBalancerClient` для взаимодействия с вашими другими службами.

- `@RibbonClient`
Используется для кастомной настройки Ribbon Client.

Если вы используете Service Discovery, то аннотация `@RibbonClient` вам не нужна, т.к. она входит по умолчанию.
Но если вы хотите настроить параметры для конкретного клиента, то надо использовать `@RibbonClient`.
    

### Основные компоненты Ribbon:

- `IClientConfig` - хранит конфигурацию клиента для клиента или балансировщика,

- `ILoadBalancer` - представляет собой программный балансировщик нагрузки,

- `ServerList` - определяет как получить список серверов (см его реализации ниже),

Может быть статическим или динамическим.
В случае если он динамический (как используется DynamicServerListLoadBalancer), то фоновый поток обновит и отфильтрует список через определенный заданный интервал.

Их можно установить программно в коде либо через файл properties

    NFLoadBalancerClassName
    NFLoadBalancerRuleClassName
    NFLoadBalancerPingClassName
    NIWSServerListClassName
    NIWSServerListFilterClassName

- `IRule` - описывает стратегию балансировки нагрузки,

- `IPing` - периодически отправляет пинг на сервер, тем самым может динамически определять жизнеспособность серверов.


### Создание свойств и собственного клиента с поддержкой балансирощика нагрузки

Формат записи свойст в файле properties имеет следующий вид:

    <clientName>.<nameSpace>.<propertyName>=<value>

Можно также загрузить настройки конфигурации из файла. Для этого надо вызвать API
    
    ConfigurationManager.loadPropertiesFromResources()
    
Если для клиента не указано никакого свойства, то `ClientFactory` все равно создаст клиент и балансировщик нагрузки со значениями по умолчанию. 
Значения по умолчанию указаны в `DefaultClientConfigImpl`.

Если в формате будет пропущено первое слово (clientName), то это свойство будет определено для всех клиентов!

Это установит свойство ReadTimeout по умолчанию для всех клиентов.

    ribbon.ReadTimeout=1000
    
Чтобы самостоятельно программно установить свойства, необходимо создать экземпляр `DefaultClientConfigImpl`, а именно:

1) надо вызвать `DefaultClientConfigImpl.getClientConfigWithDefaultValues(String clientName)`, чтобы загрузить значения по умолчанию и любые свойства, которые уже определены в Configuration в Archaius.
(`Archaius` - это библиотека управления конфигурацией динамически во время работы).

2) установить необходимые свойства вызвав API `DefaultClientConfigImpl.setProperty()`.

3) передать этот экземпляр вместе с именем клиента соответствующему API `ClientFactory`.

Желательно, чтобы свойства были определены `в другом пространстве имен`, например "hello".

    myservice.hello.ReadTimeout=1000
    
Для этого надо расширить класс `DefaultClientConfigImpl` и переопределить метод `getNameSpace()`

    public class MyClientConfig extends DefaultClientConfigImpl {
        
        public String getNameSpace() {
            return "hello";
        }
        
    }
    
    или
    
    DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl("hello");
    clientConfig.loadProperites("myservice");
    
Используйте `ClientFactory` API для создания клиента:

    MyClient client = (MyClient) ClientFactory.createNamedClient("myservice", MyClientConfig.class);
    
 
Чтобы реализовать свой собственный клиент с поддержкой балансировки нагрузки, вы должны расширить `AbstractLoadBalancerAwareClient` и переопределить некоторые методы.

Затем в файле properties указать:

    <clientName>.<nameSpace>.ClientClassName=<Your implementation class name>



### Некоторые из свойств

Определить максимальное количество попыток на одном сервере (исключая первую попытку)

    myservice.ribbon.MaxAutoRetries=1
    
Можно ли повторить все операции для этого клиента

    myservice.ribbon.OkToRetryOnAllOperations=true
    
Интервал обновления списка серверов

    myservice.ribbon.ServerListRefreshInterval=2000
    
Тайм-аут подключения и чтения, используемый Apache HttpClient

    myservice.ribbon.ConnectTimeout=3000
    myservice.ribbon.ReadTimeout=3000
    
Получить список серверов (установить ServerList)

    myservice.ribbon.listOfServers=www.microsoft.com:80,www.yahoo.com:80,www.google.com:80
    
Получить список серверов от клиента Eureka при помощи `DiscoveryEnabledNIWSServerList`.
При этом сервер должен зарегистрироваться на Eureka Server с помощью "Vip".
Любое приложение, которое можно найти в реестре служб Eureka Server и которое может быть обнаружено другими службами, называется Eureka Service.
Служба имеет определенный ID (его еще называют `VIP`) который может ссылаться на один или несколько экземпляров одного и того же приложения. (см. документацию про Eureka Server)

    myservice.ribbon.NIWSServerListClassName=com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList
     
    # the server must register itself with Eureka server with Vip "my-own-service"
    myservice.ribbon.DeploymentContextBasedVipAddresses=my-own-service
    
`ServerList` возвращает список серверов, который можно отфильтровать с помощью `ServerListFilter`.

Он имеет 2 реализации:

- `ZoneAffinityServerListFilter`

Отфильтровывает сервера находятся не в той же зоне, что и клиент, если только в клиентской зоне нет доступных серверов.

    myservice.ribbon.EnableZoneAffinity=true
    
- `ServerListSubsetFilter`

Этот фильтр гарантирует, что клиент видит только фиксированное подмножество каких-то общих серверов, возвращаемых реализацией `ServerList`. 
Он также может периодически заменять сервера плохой доступности новыми серверами.

    myservice.ribbon.NIWSServerListFilterClassName=com.netflix.loadbalancer.ServerListSubsetFilter
    
    # only show client 7 servers (default is 20)
    myservice.ribbon.ServerListSubsetFilter.size=7
    

### Интеграция с Eureka

Чтобы интегрировать Ribbon с Eureka надо:

1) Сконфигурировать `ServerList`

2) Настроить частоту обновления `(по умолчанию 30 секунд)`

3) Сконфигурировать Vip-адрес сервера для клиента и убедиться, что он совпадает с Vip-адресом сервера, который использует клиент для регистрации на Eureka Server.

        
        myclient.ribbon.NIWSServerListClassName=com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList
        
        # refresh every minute 
        myclient.ribbon.ServerListRefreshInterval=60000
        
        # movieservice is the virtual address that the target server(s) uses to register with Eureka server
        myclient.ribbon.DeploymentContextBasedVipAddresses=movieservice

    
### Правила

Существуют некоторые правила:

- `RoundRobinRule`

Это правило просто выбирает серверы с помощью циклического перебора. Он часто используется как правило по умолчанию

- `AvailabilityFilteringRule`

По умолчанию экземпляр отключается, если RestClient не может подключиться к нему в течение последних трех раз. 
После того, как экземпляр отключен, он останется в этом состоянии в течение `30 секунд`, прежде чем снова будет добавлен. Но если он по прежнему неисправен, то он будет находится в этом состоянии и время ожидания будет будет экспоненциально увеличиваться.

Пороговое значение количества сбоев при последовательном соединении для того, чтобы перевести сервер в состояние "отключен" (по умолчанию 3)

    myservice.loadbalancer.<clientName>.connectionFailureCountThreshold
    
Максимальный период, в течение которого экземпляр может оставаться в "отключенном" состоянии независимо от экспоненциального увеличения времени задержки (по умолчанию 30)

    myservice.loadbalancer.<clientName>.circuitTripMaxTimeoutSeconds
    
Порог количества одновременных подключений на сервер (по умолчанию Integer.MAX_INT)

    <clientName>.<clientConfigNameSpace>.ActiveConnectionsLimit


### Основные модули

- `ribbon-core`

- `ribbon` - API, которые интегрируют балансировку нагрузки, отказоустойчивость, кэширование и т.д. поверх других модулей, а также Hystrix

- `ribbon-loadbalancer` - API балансировки нагрузки, которые можно использовать независимо или с другими модулями

- `ribbon-eureka` - интеграция с Eureka

- `ribbon-transport` - транспортные клиенты, которые поддерживают протоколы HTTP, TCP и UDP, используя RxNetty с возможностью балансировки нагрузки

- `ribbon-example` - примеры


### Пример:

В нашем примере создается 5 реплик приложения "ftp_bucket_service". 
Когда клиент "ribbon" вызывает "ftp_bucket_service", балансировка нагрузки на стороне клиента "ribbon" решает какую реплику "ftp_bucket_service" вызвать.

Для проверки должны быть запущены как минимум Eureka Server и ftp_bucket_service.
Затем перейдите по URL

    http://my-eureka-server-zone1.com:8762/eureka
    
и в дашборде Eureka вы должны увидеть ваш запущенный ftp_bucket_service.
Перейдите по 
    
    http://localhost:5555
    
и кликните по ссылке testCallFTPService.
У вас должен появиться список из доступных репик ftp_bucket_service.

    Instances for Service Id:
    
    Instance: http://tran-pc:8091
    Instance: http://tran-pc:8092
    Instance: http://tran-pc:8093
    Instance: http://tran-pc:8094
    
При обновлении страницы вы можете увидеть как Load Balancer выбирает наиболее подходящий.

_______________

### ENG

`Ribbon` is a` client-side` balancer. Compared to the traditional, here the requests go directly to the right address.
`" Out of the box "` it is integrated with the Service Discovery mechanism, which provides a dynamic list of available instances for balancing between them.

Provides:

- Fault tolerance

- Load balancing - Load balancing

- Support for multiple protocols (HTTP, TCP, UDP) in the asynchronous and reactive model

- Caching

etc.


### Types of balancer

Load Balancer (load balancer) is of 2 types:

- `Server side Load Balancer`

Load Balancer is configured on the server side.
When requests come from the client, they will come to Load Balancer and he in turn will determine the appropriate server for this request.

- `Client-Side Load Balancer`

When the load balancer is on the Client side and it decides to which server to send the request, based on some criteria.
Balancing on the client side usually sends requests to servers of one zone (Zone), or has a quick response.


`Ribbon` decides how the server will be called (from the list of filtered servers).
There are several strategies to solve. The default strategy is `" ZoneAwareLoadBalancer "(Servers in the same zone as the client)`.


- `Filtered list of servers (Filtered List of Servers):`

For example, one client needs weather information and there is a list of servers that can provide this information. But not all of these servers work, or the server that we need is too far from the Client, because of this it responds very slowly. Client will discard these servers from the list, and at the end there will be a list of more suitable servers (Filtered list).

- `Ping:`

Ping is the way the client uses to quickly check whether the server is running at the time or not?
Eureka automatically checks this information.


Each Ribbon Client has its own `ApplicationContext` that supports Spring Cloud. At the first request it uses lazy loading.
Lazy download can be disabled:

        ribbon:
          eager-load:
            enabled: true
            clients: client1, client2, client3
        
Spring Cloud creates a new `ApplicationContext` for each Ribbon Client using` RibbonClientConfiguration`.

Using the annotation `@ RibbonClient` you can take full control of the client by declaring the additional configuration` (on top of RibbonClientConfiguration) `.

    @RibbonClient (name = "ping-server", configuration = RibbonConfiguration.class)
    
We can also specify default settings for all Ribbon Client:

    @RibbonClients (defaultConfiguration = DefaultRibbonConfig.class)
    
Also, starting with version 1.2.0, you can customize the configuration through properties.
Classes defined in properties take precedence over bins defined using the annotation `@ RibbonClient`.


Ribbon Client can be used jointly with Eureka: in this case, it will receive a list of all services registered with Eureka.
It has an IPing interface, which delegates to Eureka to determine if the server is running.

You can disable the Ribbon Client from Eureka:

        ribbon:
          eureka:
           enabled: false
       
In this case, specify the annotation `@RibbonClient (name =" myService ")` and in the file application.properties

        myservice.ribbon.eureka.enabled = false
        myservice.ribbon.listOfServers = http: // localhost: 5000, http: // localhost: 50015001
    

### What is the difference between @RibbonClient and @LoadBalanced annotations?

- `@ LoadBalanced`
Used as a marker annotation, which indicates that the RestTemplate annotated by it should use `RibbonLoadBalancerClient` to interact with your other services.

- `@ RibbonClient`
Used for customizing Ribbon Client.

If you use Service Discovery, then the annotation `@ RibbonClient` you do not need, because it is included by default.
But if you want to customize the settings for a specific client, you must use `@ RibbonClient`.
    

### Main Ribbon components:

- `IClientConfig` - stores the client’s configuration for a client or balancer,

- `ILoadBalancer` - is a software load balancer,

- `ServerList` - defines how to get a list of servers (see its implementation below),

May be static or dynamic.
In case it is dynamic (as used by DynamicServerListLoadBalancer), the background thread will update and filter the list at a certain specified interval.

They can be installed programmatically in the code or through the file properties

        NFLoadBalancerClassName
        NFLoadBalancerRuleClassName
        NFLoadBalancerPingClassName
        NIWSServerListClassName
        NIWSServerListFilterClassName

- `IRule` - describes a load balancing strategy

- `IPing` - periodically sends ping to the server, thereby can dynamically determine the viability of servers.


### Creating properties and own client with load balancer support

The format of the property record in the properties file is as follows:

    <clientName>. <nameSpace>. <propertyName> = <value>

You can also load configuration settings from a file. To do this, call the API `ConfigurationManager.loadPropertiesFromResources()`
    
If no property is specified for the client, the `ClientFactory` will still create the client and the load balancer with default values.
Default values ​​are given in `DefaultClientConfigImpl`.

If the first word (clientName) is missing in the format, this property will be defined for all clients!

This will set the default ReadTimeout property for all clients.

    ribbon.ReadTimeout = 1000
    
To set properties on your own, you need to create an instance of `DefaultClientConfigImpl`, as follows:

1) you must call `DefaultClientConfigImpl.getClientConfigWithDefaultValues ​​(String clientName)` to load the default values ​​and any properties that are already defined in the Configuration in Archaius.
(`Archaius` is a configuration management library dynamically while running).

2) set the necessary properties by calling the API `DefaultClientConfigImpl.setProperty ()`.

3) pass this instance along with the client name to the appropriate API `ClientFactory`.

It is desirable that the properties be defined `in a different namespace`, for example" hello ".

    myservice.hello.ReadTimeout = 1000
    
To do this, extend the `DefaultClientConfigImpl` class and override the` getNameSpace () `method

        public class MyClientConfig extends DefaultClientConfigImpl {
            
            public String getNameSpace () {
                return "hello";
            }
            
        }
        
        or
        
        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl ("hello");
        clientConfig.loadProperites ("myservice");
    
Use the `ClientFactory` API to create a client:

    MyClient client = (MyClient) ClientFactory.createNamedClient ("myservice", MyClientConfig.class);
    
 
To implement your own client with load balancing support, you must extend `AbstractLoadBalancerAwareClient` and override some methods.

Then specify in the properties file:

    <clientName>. <nameSpace> .ClientClassName = <Your implementation class name>
    
    
### Some of the properties

Determine the maximum number of attempts on a single server (excluding the first attempt)

    myservice.ribbon.MaxAutoRetries = 1
    
Is it possible to repeat all the operations for this client

    myservice.ribbon.OkToRetryOnAllOperations = true
    
Server list update interval

    myservice.ribbon.ServerListRefreshInterval = 2000
    
Connection and read timeout used by Apache HttpClient

        myservice.ribbon.ConnectTimeout = 3000
        myservice.ribbon.ReadTimeout = 3000
    
Get server list (install ServerList)

    myservice.ribbon.listOfServers = www.microsoft.com: 80, www.yahoo.com: 80, www.google.com: 80
    
Retrieve the server list from the Eureka client using `DiscoveryEnabledNIWSServerList`.
In this case, the server must register with Eureka Server using "Vip".
Any application that can be found in the registry of Eureka Server services that can be detected by other services is called the Eureka Service.
A service has a specific ID (also called `VIP`) that can refer to one or more instances of the same application. (see the documentation for Eureka Server)

        myservice.ribbon.NIWSServerListClassName = com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList
         
        # your server must register my my own service
        myservice.ribbon.DeploymentContextBasedVipAddresses = my-own-service
    
`ServerList` returns a list of servers that can be filtered using` ServerListFilter`.

It has 2 implementations:

- `ZoneAffinityServerListFilter`

Filters servers are not in the same zone as the client, unless there are no servers available in the client zone.

    myservice.ribbon.EnableZoneAffinity = true
    
- `ServerListSubsetFilter`

This filter ensures that the client sees only a fixed subset of some shared servers returned by the implementation of `ServerList`.
It may also periodically replace poor availability servers with new ones.

        myservice.ribbon.NIWSServerListFilterClassName = com.netflix.loadbalancer.ServerListSubsetFilter
        
        # only show client 7 servers (default is 20)
        myservice.ribbon.ServerListSubsetFilter.size = 7
    

### Integration with Eureka

To integrate the Ribbon with Eureka:

1) Configure `ServerList`

2) Set the update rate `(default 30 seconds)`

3) Configure the server's VIP address for the client and make sure that it matches the server's VIP address that the client uses to register with Eureka Server.


        myclient.ribbon.NIWSServerListClassName = com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList
               
        # refresh every minute
        myclient.ribbon.ServerListRefreshInterval = 60000
         
        # movieservice is the address of the virtual server.
        myclient.ribbon.DeploymentContextBasedVipAddresses = movieservice

    
### Rules

There are some rules:

- `RoundRobinRule`

This rule simply selects servers using looping. It is often used as a default by default.

- `AvailabilityFilteringRule`

By default, an instance is disabled if RestClient cannot connect to it within the last three times.
After the instance is disabled, it will remain in this state for `30 seconds` before being added again. But if it is still defective, then it will be in this state and the waiting time will exponentially increase.

Serial connection failure threshold in order to set the server to "off" (default 3)

    myservice.loadbalancer. <clientName> .connectionFailureCountThreshold
    
The maximum period during which an instance can remain in the "disabled" state regardless of an exponential increase in the delay time (default is 30)

    myservice.loadbalancer. <clientName> .circuitTripMaxTimeoutSeconds
    
The threshold number of simultaneous connections to the server (default Integer.MAX_INT)

    <clientName>. <clientConfigNameSpace> .ActiveConnectionsLimit
    
    
### Main modules

- `ribbon-core`

- `ribbon` - APIs that integrate load balancing, fault tolerance, caching, etc. on top of other modules as well as Hystrix

- `ribbon-loadbalancer` - a load balancing API that can be used independently or with other modules

- `ribbon-eureka` - integration with Eureka

- `ribbon-transport` - transport clients that support HTTP, TCP and UDP protocols using load-balanced RxNetty

- `ribbon-example` - examples


### Example:

In our example, 5 replicas of the application "ftp_bucket_service" are created.
When the ribbon client calls ftp_bucket_service, the load balancing on the client side of the ribbon decides which ftp_bucket_service replica to trigger.

At least Eureka Server and ftp_bucket_service must be running for verification.
Then go to URL

    http://my-eureka-server-zone1.com:8762/eureka
    
and in the Eureka dashboard you should see your running ftp_bucket_service.
Go to

    http: // localhost: 5555
    
and click on the testCallFTPService link.
You should see a list of available ftp_bucket_service reps.

    Instances for Service Id:

    Instance: http: // tran-pc: 8091
    Instance: http: // tran-pc: 8092
    Instance: http: // tran-pc: 8093
    Instance: http: // tran-pc: 8094
    
When you refresh the page, you can see how the Load Balancer chooses the most appropriate one.
