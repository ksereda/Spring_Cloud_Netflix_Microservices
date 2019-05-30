# Zuul Gateway


____

`Zuul` - это основанный на JVM маршрутизатор и серверный балансировщик нагрузки от `Netflix`.
Используется с аннотацией `@EnableZuulProxy`.
Zuul автоматически выберет список серверов в Eureka.
Он хорошо работает в связке с Hystrix, Ribbon И Turbine.

Он запускает предварительные фильтры (pre-filters), затем передает запрос с помощью клиента Netty, а затем возвращает ответ после запуска постфильтров (post-filters).
Фильтры являются основой функциональности Zuul. Они могут выполняться в разных частях жизненного цикла "запрос-ответ", т.к. они отвечают за бизнес-логику приложения и могут выполнять самые разные задачи.


###Фильтры:

1) `Предварительные фильтры (pre-filters):`
Выполняются перед маршрутизацией и могут использоваться для таких вещей, как аутентификация, маршрутизация и оформление запроса, ограничение скорости, защита от DDoS и т.д.

2) `Endpoint фильтры` - отвечают за обработку запроса на основе выполнения предварительных фильтров (ответы проверки работоспособности, ответы на статические ошибки, 404).

3) `Постфильтры (post-filters):`
Выполняются после получения ответа от источника и могут использоваться для метрик, для формирования ответа для пользователя или добавления каких-либо пользовательских заголовков.

4) `Error фильтры` - когда возникает ошибка во время одного из других этапов.

`Параметры фильтров:`

- `Type `- определяет когда будет применяться фильтр.
- `Async` - определяет является ли фильтр синхронным или нет.
- `Execution Order` - определяет порядок выполнения для нескольких фильтров.
- `Criteria` - условия, необходимые для выполнения фильтра.
- `Action` - действие, которое будет выполнено, если все критерии удовлетворены.

Фильтры не связываются друг с другом напрямую - вместо этого они совместно используют состояние через RequestContext (по структуре подобен Map), который уникален для каждого запроса.


### Компоненты

`Zuul` состоит из нескольких компонентов (модулей):
- `zuul-core` - библиотека, содержащая набор основных функций.
- `zuul-netflix` - библиотека расширений, использующая множество компонентов Netflix OSS:
    - `servo` - для метрик и мониторинга,
    - `hystrix` - библиотека отказоустойчивости,
    - `eureka`,
    - `ribbon`,
    - `archaius` - это библиотека управления конфигурацией в режиме реального времени,
    - ...
- `zuul-filters` - фильтры для работы с библиотеками zuul-core и zuul-netflix,
- `zuul-webapp-simple`
- `zuul-netflix-webapp` - веб-приложение, объединяющее zuul-core, zuul-netflix и zuul-фильтры,


###Async

Фильтры могут выполняться либо синхронно, либо асинхронно. Если ваш фильтр не выполняет много работы, не блокирует или работает отдельно, вы можете спокойно использовать синхронный фильтр (extends HttpInboundSyncFilter или HttpOutboundSyncFilter.).
    
    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/inbound/Routes.groovy#L55
    
Но если вам необходимо получить некоторые данные из какой-либо другой службы или из кэша, или, например, выполнить какие-либо сложные вычисления, то в этом случае вам надо использовать асинхронный фильтр, который возвращает Observable для ответа. (extends HttpInboundFilter или HttpOutboundFilter).

    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/inbound/SampleServiceFilter.groovy
    
    
Также есть полезные фильтры дя работы:

- `ZuulResponseFilter` - предоставляет дополнительную информацию о маршрутизации, выполнении запроса, статусе и причине ошибки.

    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/outbound/ZuulResponseFilter.groovy
    
- `Healthcheck` - возвращает 200, если все загружено корректно.

    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/endpoint/Healthcheck.groovy

- `SurgicalDebugFilter` - позволяет направлять конкретные запросы на разные узлы для отладки.

    https://github.com/Netflix/zuul/blob/2.1/zuul-core/src/main/java/com/netflix/zuul/filters/common/SurgicalDebugFilter.java


###Как работают фильтры ?

Фильтры написаны на Groovy, но `Zuul` поддерживает любой язык на основе JVM. Исходный код каждого фильтра записывается в указанный набор каталогов на сервере `Zuul`, которые автоматически обновляются в случае каких-либо нововведений.
Обновленные фильтры считываются, динамически компилируются в работающий сервер и вызываются `Zuul` для каждого последующего запроса.

###Балансировка нагрузки

В `Ribbon` по умолчанию используется `ZoneAwareLoadBalancer` для `Zuul`.
Балансировщик нагрузки будет хранить статистику для каждой зоны и удалит зону, если частота отказов превысит настраиваемый порог значений.

###Пул подключений
`Zuul` использует свой собственный пул подключений с помощью клиента `Netty`. Это сделано для того, чтобы уменьшить переключение контекста между потоками и обеспечить работоспособность.
В результате весь запрос выполняется в одном и том же потоке.

###Повтор отправки запроса
Одной из ключевых функций, используемых `Netflix` для обеспечения отказоустойчивости, является повторная попытка отправка запроса.
- ошибка таймаута
- ошибка в случае кода статуса (например статус 503)

Повторный запрос отправлен не будет, в случае:
- если утеряна часть body запроса
- если уже был начат ответ клиенту

###Push Notifications
Начиная с версии 2.0 `Zuul` поддерживает отправку push-сообщения - отправку сообщений с сервера на клиент (Push-соединения отличаются от обычных HTTP-запросов тем, что они постоянны и долговечны). Он поддерживает два протокола, `WebSockets` и `Server Sent Events (SSE)`.

После успешной аутентификации `Zuul` регистрирует каждое аутентифицированное соединение на основе идентификатора клиента, чтобы его можно было найти позже, чтобы отправить push-сообщение этому конкретному клиенту.
Можно реализовать свою Авторизацию `(extends abstract class PushAuthHandler и реализовать его метод doAuth())`.

Push сервер при помощи `PushConnectionRegistry` поддерживает локальный реестр в памяти всех клиентов, подключенных к нему. Чтобы найти конкретного клиента, вначале просматривается push-сервер, к которому подключен указанный клиент, в этом глобальном push-реестре.

Как только соединение установлено, оно остается открытым как клиентом, так и сервером, даже если нет никаких запросов.

Например, количество секунд, в течение которых сервер будет ждать, пока клиент закроет соединение, прежде чем он принудительно закроет его со своей стороны по умолчанию = 4 секунды.

    zuul.push.client.close.grace.period
    
    
###Реализация своего фильтра
Вы можете написать свой фильтр, но для этого надо расширить `класс ZuulFilter` и реализовать его методы:

    String filterType();
    
    int filterOrder();
    
    boolean shouldFilter();
    
    Object run();
    
где, 
- shouldFilter() - возвращает boolean, должен ли работать фильтр или нет.
- filterOrder() - возвращает int, описывающий порядок, в котором фильтр должен работать относительно других.
- filterType(); - описывает основной жизненный цикл фильтра, либо "pre", "routing", и "post".

Например:

`Spring Cloud Netflix` в качестве фильтра принимает любой `@Bean`, который расширяет `ZuulFilter` и доступен в контексте приложения.

    public class MyTestFilter extends ZuulFilter {
    
      @Override
      public String filterType() {
        return "pre";
      }
    
      @Override
      public int filterOrder() {
        return 1;
      }
    
      @Override
      public boolean shouldFilter() {
        return true;
      }
    
      @Override
      public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
    
        return null;
      }
    
    }
    
И также в основном классе объявить его как бин

    @EnableZuulProxy
    @SpringBootApplication
    public class GatewayApplication {
    
      public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
      }
    
      @Bean
      public MyTestFilter myTestFilter() {
        return new MyTestFilter();
      }
    
    }

### Параметры файла properties

Чтобы ограничить сервисы, которые должны выполняться, можно использовать

    zuul:
      ignoredServices: '*'
      routes:
        users: /myusers/**

Здесь будут проигнорированы все сервисы, кроме users.

Местоположение сервиса может быть указано либо как serviceId, либо как URL

    zuul:
      routes:
        text-service:
          path: /text-service/**
          serviceId: service_indexer
          
либо

    zuul:
      routes:
        users:
          path: /myusers/**
          url: https://example.com/users_service
          

Возможна более детальная конфигурация для разных сценариев обработки запросов, например:

    zuul:
      routes:
        first:
          path: /first/**
          url: https://lalala.test.com
        second:
          path: /second/**
          url: forward:/second
        third:
          path: /third/**
          url: forward:/3rd
        legacy:
          path: /**
          url: https://legacy.example.com
          
Если вы используете аннотацию `@EnableZuulProxy`, вы можете использовать пути прокси для загрузки файлов. Пока файлы небольшие, будет работать без проблем.

Если маршрут прокси-сервера проходит через `Ribbon`, то для очень больших файлов требуются повышенные параметры тайм-аута:

    ribbon:
      ConnectTimeout: 3000
      ReadTimeout: 60000
      

Чтобы принудительно закодировать внешний запрос:

     zuul:
      forceOriginalQueryStringEncoding: true
      
При обработке входящего запроса URI запроса декодируется перед тем, как будет сопоставлен с маршрутами.
Если ваш URI содержит закодированный символ `"/"`, то могут быть проблемы.
Чтобы использовать исходный URI запроса:

    zuul:
      decodeUrl: false
      
При использовании аннотации `@EnableZuulServer` `(вместо @EnableZuulProxy)`, можно запустить сервер `Zuul` без прокси. 
Любые bean-компоненты, которые будут добавлены в приложение (типа ZuulFilter), будут установлены автоматически (как и в случае с `@EnableZuulProxy`), но без добавления каких-либо прокси-фильтров.
Следовательно, настройки `«serviceId» и «url»` будут игнорироваться.

Вы также можете настроить время ожидания для запросов, передаваемых через `Zuul`:

    zuul:
      host:
        connect-timeout-millis: 60000
        socket-timeout-millis: 60000
    ribbon:
      ReadTimeout: 60000
      ConnectTimeout: 60000
      


### В чем разнциа между `@EnableZuulProxy` и `@EnableZuulServer` ?

`@EnableZuulProxy` - это расширенный `@EnableZuulServer`. Другими словами, `@EnableZuulProxy` содержит все фильтры, установленные `@EnableZuulServer`.


###EnableZuulServer

Аннотация `@EnableZuulServer` создает `SimpleRouteLocator`, который загружает определения маршрутов из файлов конфигурации Spring Boot.

Фильтры:
- `ServletDetectionFilter`: определяет, выполняется ли запрос через Spring Dispatcher. 



###EnableZuulProxy

Он создает `DiscoveryClientRouteLocator`, который загружает определения маршрутов из DiscoveryClient (Eureka) или из свойств.


### Настройки

Рассмотрим пример настроек из этого приложения

    zuul:
      routes:
        searchIndexFile:
          serviceId: service_searcher
        text-service:
          path: /text-service/**
          serviceId: service_indexer
        search-service:
          path: /search-service/**
          serviceId: service_searcher
      host:
        socket-timeout-millis: 30000
        
Здесь видно, что только "text-service" и "search-service" сервисы будут использоваться. Остальные будут игнорироваться.

Обратите внимание на параметр

    zuul:
          routes:
            employeeUI:
              serviceId: service_searcher
              
Здесь мы говорим, что если какой-либо запрос приходит к шлюзу в форме `/searchIndexFile`, то он перенаправляется в микросервис `service_searcher`.
т.е. если пойти на 

     http://localhost:8880/searchIndexFile/statictics/1
     
вы будете перенавправлены на

     http://localhost:8085/statictics/1
     
     # это сервис "service_searcher"
     
Здесь `Zuul` несет ответственность за маршрутизацию службы к соответствующему микросервису.

Также `Zuul` может быть реализован без сервера Eureka. В этом случае вы должны указать точный URL-адрес службы, на которую он будет перенаправлен.

Параметр

    zuul:
      host:
        socket-timeout-millis: 30000
        
дает команду Spring Boot ждать ответа в течение 30000 мс.

Или мы можем указать чтобы все запросы от пользователей, начинающиеся на `/tests` были направлены на наш сервер ресурсов Tests по адресу http://localhost:8081/tests

    zuul:
      routes:
        tests:
          path: /tests/**
          url: http://localhost:8081/tests


Поскольку `Zuul` является клиентом Discovery, добавьте аннотацию `@EnableDiscoveryClient` (и не забудьте про `@EnableZuulProxy`) в основной класс:

    @EnableZuulProxy
    @EnableDiscoveryClient
    @SpringBootApplication
    public class ZuulGatewayApplication {
    
    	public static void main(String[] args) {
    		SpringApplication.run(ZuulGatewayApplication.class, args);
    	}
    
    }
    

На данный момент существует версия `Zuul 2.0`.
`Zuul 1.0` был построен на основе Servlet. Такие системы являются блокирующими и многопоточными, т.е. они обрабатывают запросы, используя один поток на соединение. 
Он берет поток из пула потоков для выполнения операции ввода-вывода, и поток запроса блокируется до завершения операции.
Но если что-то идет не так (ошибки сети и т.д.) то количество активных соединений и потоков увеличивается, также увеличивается нагрузка на сервер и перегружается кластер.
`В версии 2.0` была добавлена такая библиотека как `Hystrix`. На время событий срабатывают блокировки чтобы помочь поддерживать стабильность системы.

________

### ENG


`Zuul` is a JVM-based router and server load balancer from` Netflix`.
Used with the annotation `@ EnableZuulProxy`.
Zuul will automatically select the server list in Eureka.
It works well in conjunction with Hystrix, Ribbon and Turbine.

It runs the pre-filters, then sends the request using the Netty client, and then returns the response after running the post-filters.
Filters are the core of Zuul functionality. They can be executed in different parts of the request-response life cycle, since they are responsible for the business logic of the application and can perform a variety of tasks.


### Filters:

1) `Pre-filters:`
They are performed before routing and can be used for such things as authentication, routing and query processing, speed limit, DDoS protection, etc.

2) ʻEndpoint filters` - are responsible for processing the request based on the execution of preliminary filters (responses of health checks, responses to static errors, 404).

3) `Post-filters (post-filters):`
Runs after receiving a response from the source and can be used for metrics, to form a response for the user, or to add any custom headers.

4) `Error filters` - when an error occurs during one of the other stages.

`Filter options:`

- `Type` - determines when the filter will be applied.
- `Async` - determines whether the filter is synchronous or not.
- `Execution Order` - determines the execution order for several filters.
- `Criteria` - conditions required for the filter.
- `Action` - the action that will be executed if all criteria are met.

The filters do not communicate directly with each other - instead, they share the state via RequestContext (similar in structure to the Map), which is unique for each request.


### Components

`Zuul` consists of several components (modules):
- `zuul-core` is a library containing a set of basic functions.
- `zuul-netflix` is an extension library that uses many of the components of Netflix OSS:
    - `servo` - for metrics and monitoring,
    - `hystrix` - failover library,
    - `eureka`,
    - `ribbon`,
    - `archaius` is a real-time configuration management library,
    - ...
- `zuul-filters` - filters for working with zuul-core and zuul-netflix libraries,
- `zuul-webapp-simple`
- `zuul-netflix-webapp` - a web application that combines zuul-core, zuul-netflix and zuul-filters,


### Async

Filters can run either synchronously or asynchronously. If your filter does not do a lot of work, does not block or works separately, you can safely use a synchronous filter (extends HttpInboundSyncFilter or HttpOutboundSyncFilter.).
    
    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/inbound/Routes.groovy#L55
    
But if you need to get some data from some other service or from the cache, or, for example, perform some complex calculations, in this case you need to use an asynchronous filter that Observable returns for the response. (extends HttpInboundFilter or HttpOutboundFilter).

    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/inbound/SampleServiceFilter.groovy
    
    
There are also useful filters for work:

- `ZuulResponseFilter` - provides additional information about the routing, query execution, status and cause of the error.

    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/outbound/ZuulResponseFilter.groovy
    
- `Healthcheck` - returns 200 if everything is loaded correctly.

    https://github.com/Netflix/zuul/blob/2.1/zuul-sample/src/main/groovy/com/netflix/zuul/sample/filters/endpoint/Healthcheck.groovy

- `SurgicalDebugFilter` - allows you to send specific requests to different nodes for debugging.

    https://github.com/Netflix/zuul/blob/2.1/zuul-core/src/main/java/com/netflix/zuul/filters/common/SurgicalDebugFilter.java


### How do filters work?

Filters are written in Groovy, but `Zuul` supports any language based on JVM. The source code of each filter is written to the specified set of directories on the `Zuul` server, which are automatically updated in case of any innovations.
Updated filters are read, dynamically compiled into a working server, and `Zuul` is invoked for each subsequent request.

### load balancing

In `Ribbon`, the default uses` ZoneAwareLoadBalancer` for `Zuul`.
The load balancer will keep statistics for each zone and will remove the zone if the failure rate exceeds the adjustable threshold.

### Connection Pool
`Zuul` uses its own connection pool using the` Netty` client. This is done to reduce context switching between threads and ensure operability.
As a result, the entire request is executed in the same thread.

### Repeat request
One of the key features used by `Netflix` to ensure fault tolerance is to retry sending a request.
- timeout error
- error in the case of a status code (for example, status 503)

Repeat request will not be sent, in the case of:
- if the body part of the request is lost
- if the response has already been started to the client

### Push Notifications
Starting from version 2.0, `Zuul` supports sending push messages - sending messages from the server to the client (Push connections differ from regular HTTP requests in that they are permanent and long-lasting). It supports two protocols, `WebSockets` and` Server Sent Events (SSE) `.

After successful authentication, `Zuul` registers each authenticated connection based on the client's identifier so that it can be found later to send a push message to this particular client.
You can implement your Authorization `(extends abstract class PushAuthHandler and implement its doAuth ())` method.

Push server using `PushConnectionRegistry` maintains a local registry in the memory of all clients connected to it. To find a specific client, first look at the push server to which the specified client is connected, in this global push registry.

Once the connection is established, it remains open both by the client and by the server, even if there are no requests.

For example, the number of seconds during which the server will wait for the client to close the connection, before it forcibly closes it for its part, by default = 4 seconds.
    
    zuul.push.client.close.grace.period
    
    
### Implementing Your Filter
You can write your own filter, but for this you need to extend the `ZuulFilter` class and implement its methods:

        String filterType ();
        
        int filterOrder ();
        
        boolean shouldFilter ();
        
        Object run ();
    
Where,
- shouldFilter () - returns boolean if the filter should work or not.
- filterOrder () - returns an int that describes the order in which the filter should work relative to others.
- filterType (); - describes the basic life cycle of the filter, either "pre", "routing", and "post".

For example:

`Spring Cloud Netflix` as a filter takes any` @ Bean`, which extends `ZuulFilter` and is available in the context of the application.

        public class MyTestFilter extends ZuulFilter {
        
          @Override
          public String filterType () {
            return "pre";
          }
        
          @Override
          public int filterOrder () {
            return 1;
          }
        
          @Override
          public boolean shouldFilter () {
            return true;
          }
        
          @Override
          public Object run () {
            RequestContext ctx = RequestContext.getCurrentContext ();
            HttpServletRequest request = ctx.getRequest ();
        
            return null;
          }
        
        }
    
And also basically class declare it as bin

        @EnableZuulProxy
        @SpringBootApplication
        public class GatewayApplication {
        
          public static void main (String [] args) {
            SpringApplication.run (GatewayApplication.class, args);
          }
        
          @Bean
          public MyTestFilter myTestFilter () {
            return new MyTestFilter ();
          }
        
        }
    
### File properties

To limit the services that should be run, you can use

        zuul:
          ignoredServices: '*'
          routes:
            users: / myusers / **

All services except users will be ignored here.

The location of the service can be specified either as a serviceId or as a URL

        zuul:
          routes:
            text-service:
              path: / text-service / **
              serviceId: service_indexer
          
or

        zuul:
          routes:
            users:
              path: / myusers / **
              url: https://example.com/users_service
          

A more detailed configuration is possible for different query processing scenarios, for example:

        zuul:
          routes:
            first:
              path: / first / **
              url: https://lalala.test.com
            second:
              path: / second / **
              url: forward: / second
            third:
              path: / third / **
              url: forward: / 3rd
            legacy:
              path: / **
              url: https://legacy.example.com
          
If you use the `@ EnableZuulProxy` annotation, you can use proxy paths to download files. While the files are small, it will work without problems.

If the proxy route goes through `Ribbon`, then very large files require increased timeout parameters:

        ribbon:
          ConnectTimeout: 3000
          ReadTimeout: 60000
      

To force an external request code:

         zuul:
          forceOriginalQueryStringEncoding: true
      
When processing an incoming request, the request URI is decoded before it is mapped to routes.
If your URI contains an encoded character `" / "`, then there may be problems.
To use the original request URI:

        zuul:
          decodeUrl: false
      
When using the annotation `@ EnableZuulServer`` (instead of @EnableZuulProxy) `, you can start the server` Zuul` without a proxy.
Any beans that will be added to the application (such as ZuulFilter) will be installed automatically (as is the case with `@ EnableZuulProxy`), but without adding any proxy filters.
Therefore, the “serviceId” and “url” `settings will be ignored.

You can also set the wait time for requests sent via `Zuul`:

        zuul:
          host:
            connect-timeout-millis: 60000
            socket-timeout-millis: 60000
        ribbon:
          ReadTimeout: 60000
          ConnectTimeout: 60000
    
### What is the difference between `@ EnableZuulProxy` and` @ EnableZuulServer`?

`@ EnableZuulProxy` is an advanced` @ EnableZuulServer`. In other words, `@ EnableZuulProxy` contains all filters set by` @ EnableZuulServer`.


### EnableZuulServer

The annotation `@ EnableZuulServer` creates` SimpleRouteLocator`, which loads route definitions from the Spring Boot configuration files.

Filters:
- `ServletDetectionFilter`: Determines whether a request is being executed through the Spring Dispatcher.



### EnableZuulProxy

It creates `DiscoveryClientRouteLocator`, which loads route definitions from DiscoveryClient (Eureka) or from properties.


### Settings

Consider an example of settings from this application.

        zuul:
          routes:
            searchIndexFile:
              serviceId: service_searcher
            text-service:
              path: / text-service / **
              serviceId: service_indexer
            search-service:
              path: / search-service / **
              serviceId: service_searcher
          host:
            socket-timeout-millis: 30000
        
Here you can see that only the "text-service" and "search-service" services will be used. The rest will be ignored.

Pay attention to the parameter

            zuul:
              routes:
                employeeUI:
                  serviceId: service_searcher
                  
Here we say that if any request comes to the gateway in the form `/ searchIndexFile`, then it is redirected to the` service_searcher` microservice.
those. if you go on

    http: // localhost: 8880 / searchIndexFile / statictics / 1
     
you will be redirected to

         http: // localhost: 8085 / statictics / 1
         
         # is a service "service_searcher"
     
Here `Zuul` is responsible for routing the service to the appropriate microservice.

Also `Zuul` can be implemented without Eureka server. In this case, you must specify the exact URL of the service to which it will be redirected.

Parameter

        zuul:
          host:
            socket-timeout-millis: 30000
        
instructs Spring Boot to wait for a response for 30,000 ms.

Or we can specify that all requests from users beginning with `/ tests` are sent to our Tests resource server at http: // localhost: 8081 / tests

        zuul:
          routes:
            tests:
              path: / tests / **
              url: http: // localhost: 8081 / tests


Since `Zuul` is a Discovery client, add the` @ EnableDiscoveryClient` annotation (and don’t forget about `@ EnableZuulProxy`) to the main class:

        @EnableZuulProxy
        @EnableDiscoveryClient
        @SpringBootApplication
        public class ZuulGatewayApplication {
        
        public static void main (String [] args) {
        SpringApplication.run (ZuulGatewayApplication.class, args);
        }
        
        }
    

At the moment there is a version of `Zuul 2.0`.
`Zuul 1.0` was built on the basis of Servlet. Such systems are blocking and multithreaded, i.e. they process requests using one stream per connection.
It takes a thread from the thread pool to perform an I / O operation, and the request thread blocks until the operation is completed.
But if something goes wrong (network errors, etc.) then the number of active connections and flows increases, the server load also increases and the cluster is overloaded.
`In version 2.0` a library was added as` Hystrix`. During events, locks are triggered to help maintain system stability.