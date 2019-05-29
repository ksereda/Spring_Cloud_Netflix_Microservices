# Ribbon Client

* [Official Gradle documentation](https://docs.gradle.org)

### Guides

* [Client Side Load Balancing with Ribbon and Spring Cloud](https://spring.io/guides/gs/client-side-load-balancing/)

### Additional Links

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

_____

### Ribbon

The `Load Balancer` can be 2 types:

- `Server side Load Balancer`

Load Balancer is configured on the server side.
When requests come from Client, they will come to Load Balancer and he will in turn determine the appropriate server for this request.

- `Client-Side Load Balancer`

When the load balancer is on the Client side and it decides to which server to send the request, based on some criteria.
Balancing on the client side usually sends requests to servers of one zone (Zone), or has a quick response.

___________

`Ribbon` is the `Client-Side Load Balancer` (balancer on the client side). It decides how the server will be called (from the list of filtered servers).
There are several strategies to solve. The default strategy is `"ZoneAwareLoadBalancer"` (Servers in the same zone with the client).


* `Filtered list of servers (Filtered List of Servers):`

For example, one client needs weather information and there is a list of servers that can provide this information. But not all of these servers work, or the server that we need is too far from the Client, because of this it responds very slowly. Client will discard these servers from the list, and at the end there will be a list of more suitable servers (Filtered list).

* `Ping:`

Ping is the way Client uses to quickly check whether the server is currently running or not?
Eureka automatically checks this information.

_____

Каждый Ribbon Client имеет свой ApplicationContext, который поддерживает Spring Cloud. При первом запросе он использует ленивую загрузку. Ленивую загрузку можно отключить:

    ribbon:
      eager-load:
        enabled: true
        clients: client1, client2, client3
        
Spring Cloud создает новый ApplicationContext для каждого Ribbon Client с помощью RibbonClientConfiguration.

С помощью аннотации @RibbonClient можно получить полный контроль над клиентом, объявив дополнительную конфигурацию (поверх RibbonClientConfiguration).

    @RibbonClient(name = "ping-server", configuration = RibbonConfiguration.class)
    
Мы также можем указать настройки по умолчанию ​​для всех Ribbon Client:

    @RibbonClients(defaultConfiguration = DefaultRibbonConfig.class)
    
Также, начиная с версии 1.2.0, можно настроить конфигурацию через properties.
Классы, определенные в свойствах имеют приоритет над бинами определенными с помощью аннотации @RibbonClient.

_______

Ribbon Client может использоваться совмесно с Eureka: в таком случае он будет получать список всех сервисов, зарегистрированных в Eureka. 
Он имеет интерфейс IPing, который делегирует Eureka, чтобы определить, работает ли сервер.

Вы можете отключить Ribbon Client от Eureka:

    ribbon:
      eureka:
       enabled: false
       
В таком случае укажите аннотацию @RibbonClient(name = "myService") и в файле application.properties
    
    myservice.ribbon.eureka.enabled=false
    myservice.ribbon.listOfServers=http://localhost:5000, http://localhost:5001    
________

Чем отличаются аннотации @RibbonClient и @LoadBalanced ?

@LoadBalanced
Используется как аннотация маркера, которая указывает на то, что аннотированный ею RestTemplate должен использовать RibbonLoadBalancerClient для взаимодействия с вашими другими службами.

@RibbonClient
Используется для кастомной настройки Ribbon Client.

Если вы используете Service Discovery, то аннотация @RibbonClient вам не нужна, т.к. она входит по умолчанию.
Но если вы хотите настроить параметры для конкретного клиента, то надо использовать @RibbonClient.
    
_______

Основные компоненты Ribbon:

an IClientConfig, which stores client configuration for a client or load balancer,
an ILoadBalancer, which represents a software load balancer,
a ServerList, which defines how to get a list of servers to choose from,
an IRule, which describes a load balancing strategy, and
an IPing, which says how periodic pings of a server are performed.

