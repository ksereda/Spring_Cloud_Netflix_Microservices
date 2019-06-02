## Spring Cloud: Feign Client

### RUS

`Feign` — простой и гибкий http-клиент, который нативно интегрирован с `Ribbon`.
`Feign` использует интерфейсы аннотированные `@FeignClient` чтобы генерировать API запросы и мапить ответ на Java классы.
Он шлет http запросы другим сервисам.

Его особенность в том, что нам не нужно знать где и на каком порту находится какой-то сервис.
Мы просто говорим `Feign` клиенту, иди к "Джон Уик" и получи у него всех пользователей. Далее `Feign` обращается к `Eureka Server` и спрашивает где находится "Джон Уик".
 
Если "Джон Уик" регистрировался в `Eureka Server`, то `Eureka` будет всё знать о "Джон Уик" (где он находится, на каком порту, его URL и т.д.)

Вам нужно только описать, как получить доступ к удаленной службе API, указав такие детали, как URL, тело запроса и ответа, принятые заголовки и т. д. Клиент Feign позаботится о деталях реализации.

`Netflix` предоставляет `Feign` в качестве абстракции для вызовов на основе REST, благодаря которым микросервисы могут связываться друг с другом, но разработчикам не нужно беспокоиться о внутренних деталях REST.

Нужно указать аннотацию `@EnableFeignClients` над основным классом

    @SpringBootApplication
    @EnableFeignClients
    @EnableDiscoveryClient
    public class FeignClientApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(FeignClientApplication.class, args);
        }
    
    }

На интерфейс ставим аннотацию `@FeignClient(name = "Джон Уик")` и указываем имя того сервиса, который нам нужен (в пояснении я описывал сервис под названием `"Джон Уик"`). 
В том сервисе будет выполняться некая логика по работе с базой и настройки коннекта к базе, или получение всех пользователей и т.д.

`Feign` - это первый шаг в реализации архитектуры микросервиса при помощи инструментов `Netflix`. 
В реальности слабо связанных сервисов очень важно чтобы общение между ними было легковесным и простым для отладки. 
Поэтому для этой цели зачастую используют `REST`, хотя для некоторых случаев это может быть не лучшим выбором. 
Для упрощения связи по REST мы и используем Feign: при помощи него мы будем поглощать сообщения от других сервисов и автоматически превращать их в Java объекты.

### Пример
Рассмотрим небольшой и в то же время очень простой пример.
У нас есть `сервис А` и `сервис B`, которые оба зарегистрированы в `Eureka`. Мы хотим из `сервиса А` вызвать `сервис B` и получить какие-то данные. 

`Сервис B` имеет следующий контроллер:


	@RestController
	@RequestMapping("/users/{id}/statistic")
	public class StatisticsController {
	    
	    @Autowired
	    StatisticsService statisticsService;
	  
	       public List<UserStatisticModel> getStatistic(@PathVariable String id) {
	        List<UserStatisticModel> statisticsList = albumsService.getAlbums(id);
	        return statisticsList;
	    }
	}

и модель:

	public class UserStatisticModel {

	    private Long id;
	    private String userId; 
	    private String username;
	    private String title;

	}

Используем `Lombok` чтобы избавиться от ненужного кода геттеров и сеттеров.


Чтобы из `сервиса А` вызвать `сервис B` мы создадим в `сервисе А` feign client.

В аннотации `@FeignClient` мы указываем имя того сервиса, который хотим вызвать `(сервис B)`.

	@FeignClient(name = "B")
	public interface ServiceFeignClient {
	        
	 @GetMapping("/users/${id}/statistic")
	 public List<UserStatisticModel> getStatistic(@PathVariable String id);
	 
	}

Аннотация `@GetMapping` содержит путь, который отображается на тот же самый путь в аннотации `@RequestMapping` в `микросервисе B` и указывает, что это будет `GET запрос`. 
`Метод должен иметь такую-же сигнатуру что и метод в сервисе B. Это важно!`

Модель:

	public class UserStatisticModel {

	    private Long id;
	    private String userId; 
	    private String username;
	    private String title;

	}

Теперь чтобы создать экземпляр клиента `Feign`, вам нужно будет автоматически подключить интерфейс клиента feign к вашему классу, в котором вызывается логика.

	@Service
	public class UsersServiceImpl implements UsersService {

	@Autowired
	ServiceFeignClient serviceFeignClient;

	....

	List<AlbumResponseModel> albumsList = serviceFeignClient.getStatistic(userId);

	....

	}

Теперь когда мы будем дергать URL `"/users/${id}/statistic"` из `ServiceFeignClient` `сервиса А`, он посмотрит на аннотацию `@FeignClient(name = "B")` и увидит, что там указан `сервис B`, пойдет к нему и по этому же URL в `StatisticsController` вызовет метод `getStatistic`.

Все очень просто.

Также хочу добавить, если вам нужно использовать внешнюю веб-службу, которая не является частью вашей архитектуры микросервисов и не зарегистрирована в вашей службе Eureka, то используйте `URL` в качестве параметра аннотации `@FeignClient`.

	@FeignClient(name = "B", url = "http://localhost:8089")
	public interface ServiceFeignClient {
	        
	 @GetMapping("/users/${id}/statistic")
	 public List getStatistic(@PathVariable String id);
	 
	}


______

### ENG

`Feign` - is a simple and flexible http client that is natively integrated with `Ribbon`.
`Feign` uses the annotated interfaces `@FeignClient` to generate API requests and build response to Java classes.
He sends http requests to other services.

Its peculiarity is that we don't need to know where and on which port there is a service.
We just say `Feign` to the customer, go to `John Wick` service and get all users from him. Further `Feign` refers to `Eureka Server` and asks where is "John Wick".
 
If "John Wick" was registered in `Eureka Server`, then `Eureka` will know everything about "John Wick" (where it is, on which port, its URL, etc.)

You only need to describe how to access the remote API service, specifying details such as the URL, request and response body, accepted headers, etc. The Feign client will take care of the implementation details.

`Netflix` provides `Feign` as an abstraction for REST-based calls, thanks to which microservices can communicate with each other, but developers don't need to worry about the internal details of REST.

You must specify an annotation `@EnableFeignClients` above the main class

        @SpringBootApplication
        @EnableFeignClients
        @EnableDiscoveryClient
        public class FeignClientApplication {
        
            public static void main (String [] args) {
                SpringApplication.run (FeignClientApplication.class, args);
            }
        
        }

We put on the interface the annotation `@FeignClient(name = "John Wick")` and indicate the name of the service we need (in the explanation I described the service called `"John Wick"`).
In that service, some logic will be executed on working with the database and setting up a connection to the database, or getting all users, etc.

`Feign` is the first step in the implementation of the microservice architecture using the tools `Netflix`.
In reality, loosely coupled services are very important so that communication between them is lightweight and easy to debug.
Therefore, `REST` is often used for this purpose, although for some cases this may not be the best choice.
To simplify REST communication, we use Feign: with the help of it, we will absorb messages from other services and automatically turn them into Java objects.


### Example
Consider a small and at the same time very simple example.
We have `service A` and `service B`, both of which are registered in `Eureka`. We want to call the `service B` from `service A` and get some data.

`Service B` has the following controller:

    @RestController
    @RequestMapping ("/ users / {id} / statistic")
    public class StatisticsController {
    
        @Autowired
        StatisticsService statisticsService;
        
        public list <UserStatisticModel> getStatistic (@PathVariable String id) {
        List <UserStatisticModel> statisticsList = albumsService.getAlbums (id);
        return statisticsList;
        }
    }

and model:

    public class UserStatisticModel {
    
    private Long id;
    private String userId;
    private String username;
    private String title;
    
    }

Use `Lombok` to get rid of unnecessary code getters and setters.


In order to call `service B` from the service A, we will create a feign client in the service A.

In the annotation `@FeignClient` we indicate the name of the service that we want to call `(service B)`.

    @FeignClient (name = "B")
    public interface ServiceFeignClient {
    
    @GetMapping ("/ users / $ {id} / statistic")
    public list <UserStatisticModel> getStatistic (@PathVariable String id);
    
    }

The annotation `@GetMapping` contains a path that is mapped to the same path in the annotation `@RequestMapping` in `Microservice B` and indicates that it will be a `GET request`.
`The method must have the same signature as the method in service B. This is important!`

Model:

    public class UserStatisticModel {
    
    private Long id;
    private String userId;
    private String username;
    private String title;
    
    }

Now to instantiate the `Feign` client, you will need to automatically connect the feign client interface to your class in which logic is invoked.

    @Service
    public class UsersServiceImpl implements UsersService {
    
    @Autowired
    ServiceFeignClient serviceFeignClient;
    
    ....
    
    List <AlbumResponseModel> albumsList = serviceFeignClient.getStatistic (userId);
    
    ....
    
    }

Now, when we pull the URL `"/users/${id}/statistic"` from `ServiceFeignClient` `service A`, he will look at the annotation `@FeignClient(name = "B")` and see that there is `service B`, will go to it and at the same URL in `StatisticsController` will call the `getStatistic` method.

Everything is very simple.

I also want to add, if you need to use an external web service that is not part of your microservice architecture and is not registered with your Eureka service, use `URL` as the annotation parameter `@FeignClient`.

    @FeignClient (name = "B", url = "http: // localhost: 8089")
    public interface ServiceFeignClient {
    
    @GetMapping ("/ users / $ {id} / statistic")
    public List getStatistic (@PathVariable String id);
    
    }