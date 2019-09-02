## Hystrix

### RUS

`Hystrix` - библиотека задержек и отказоустойчивости, которая помогает контролировать взаимодействие между службами, обеспечивая отказоустойчивость и устойчивость к задержкам, благодаря чему повышается устойчивость всей системы в целом.
Другими словами можно сказать, что `Hystrix` — это имплементация `паттерна Circuit Breaker`. Основная идея состоит в том, чтобы остановить каскадный отказ в распределенной системе сервисов, состоящей из их большого числа. Это позволяет отдавать ошибку на раннем этапе и давая возможность "упавшему"" сервису восстановить свою работоспособность.

Hystrix позволяет определить fallback-метод, который будет вызван при неуспешном вызове. Что будет в этом fallback-методе уже решать вам.

Более подробно вы можете прочитать в моей статье, посвященной паттерну Circuit Breaker:
    
    https://medium.com/@kirill.sereda/%D1%81%D1%82%D1%80%D0%B0%D1%82%D0%B5%D0%B3%D0%B8%D0%B8-%D0%BE%D0%B1%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B8-%D0%BE%D1%88%D0%B8%D0%B1%D0%BE%D0%BA-circuit-breaker-pattern-650232944e37

Если вы используете Hystrix вместе с Feign Client, то чтобы это заработало необходимо в настройках указать
    
    feign:
     hystrix:
       enabled: true

### Пример:

У нас есть 2 сервиса: 
- `Hystrix` - микросервис на основе REST, в который внедрен Circuit Breaker.
- `service_user-details` - получает какую-либо информацию по пользователю (к какой группе он принадлежит).

Из сервиса `Hystrix` мы будем вызывать сервис `service_user-details`.

#### 1) Описываем `service_user-details`:

Указываем порт в `application.properties`:

    server.port=8077

Создаем контроллер `UsersServiceController`, в котором по URL `"/getUsersDetailsByGroup/{group}"` будем получать какую-либо информацию по пользователю по группе, к которой он принадлежит.
`(смотри сервис service_user-details)`

    @RequestMapping(value = "/getUsersDetailsByGroup/{group}", method = RequestMethod.GET)
    public List<Users> getUsers(@PathVariable String group) {
        List<Users> usersList = map.get(group);

        if (usersList == null || usersList.isEmpty()) {
            usersList = new ArrayList<>();
            Users users = new Users("Users not found", null);
            usersList.add(users);
        }

        return usersList;
    }
    
модель:

    public class Users {

        private String name;
        private Integer age;
    
        public Users(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

    }
    
После запуска сервиса по URL `"http://localhost:8077/getUsersDetailsByGroup/coolman"` вы увидите данные по этим `крутым парням.`

#### 2) Сервис `Hystrix`:

Указываем порт:
    
    server.port=8076
    
Над основным классом необходимо указать аннотации `@EnableCircuitBreaker` и `@EnableHystrixDashboard`

    @SpringBootApplication
    @EnableHystrixDashboard
    @EnableCircuitBreaker
    public class HystrixApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(HystrixApplication.class, args);
        }
    
    }
    
Делаем контроллер, в котором вызываем `"/getGroupDetails/{group}"` и получаем информацию о группе вместе с данными о пользователе
Для вызова информации о пользователе будет вызван уже написанный метод в контроллере в сервисе `service_user-details`.

    @RestController
    public class HystrixController {
    
        @Autowired
        UserService userservice;
    
        @RequestMapping(value = "/getGroupDetails/{group}", method = RequestMethod.GET)
        public String getUsers(@PathVariable String group) {
            return userservice.callUserService(group);
        }
    
    }
    
Для этого напишем в этом пакете сервис `UserService`

    @Service
    public class UserService {
    
        @Autowired
        RestTemplate restTemplate;
    
        @HystrixCommand(fallbackMethod = "callUserService_Fallback")
        public String callUserService(String group) {
            String response = restTemplate
                    .exchange("http://localhost:8077/getUsersDetailsByGroup/{group}"
                            , HttpMethod.GET
                            , null
                            , new ParameterizedTypeReference<String>() {
                            }, group).getBody();
    
            return "It's OK: group: " + group + " users details " + response + new Date();
        }
    
        @SuppressWarnings("unused")
        private String callUserService_Fallback(String group) {
            return "Error! Default info... " + new Date();
        }
    
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    
    }
    
- Здесь используем `RestTemplate` (для разнообразия. В следующем примере попробуем использовать `Feign Client`)
- Используем аннотацию @HystrixCommand с параметрами дополнительного метода, который будет вызван в случае, если service_user-details будет недоступен
    
       
        @HystrixCommand(fallbackMethod = "callUserService_Fallback")
    
У этого метода должна быть одинаковая сигнатура с методом `callUserService` (получения группы и данных пользователей)

Теперь запустите оба сервиса.
Попробуйте из сервиса `Hystrix` вызвать сервис `service_user-details`

    http://localhost:8076/getGroupDetails/badboy
    
Вы увидите `Sylvester Stallone и Chuck Norris`.

Симулируйте отказ сервиса `service_user-details` (проблемы в сети, перегруженность сервиса и т.д.) и отключите его
Попробуйте снова обратиться по URL

    http://localhost:8076/getGroupDetails/badboy
    
Отработает запасной метод `callUserService_Fallback`

    Error! Default info... 
   
Вывод текста я привел в качестве простого и наглядного примера.    
Вместо надписи типа Error вы можете придумать что вам угодно, вплоть до получения из базы каких-то кастомных данных и предоставления их пользователю или перенаправления на другой сервис (реплика сервиса service_user-details) в случае если у вас система по потоковому вещанию, где залержка вплоть до пары секунд очень многое значит для поставщика услуг.

Это был лишь один из простых примеров, как можно использовать Hystrix.
Остальные примеры буду добавлять сюда как `example_2, 3 и тд.`

Hystrix также предоставляет дашборд по URL

    http://localhost:8077/hystrix.stream
    
Там вы увидите результаты проверки работоспособности вместе со всеми вызовами сервисов, которые контролируются с помощью Hystrix.

Начальный экран дашборда по URL

    http://localhost:8077/hystrix
    
Добавьте туда 

    http://localhost:8077/hystrix.stream
    
и вы получите статистику в виде диаграммы.

Посмотрите мой доклад на тему Netflix OSS

    https://github.com/ksereda/Gallery-Service
    
_____

### ENG

`Hystrix` is ​​a library of delays and fault tolerance, which helps to control the interaction between services, providing fault tolerance and resistance to delays, thereby increasing the stability of the entire system.
In other words, we can say that `Hystrix` - is ​​the implementation of the `Circuit Breaker pattern`. The basic idea is to stop the cascade failure in a distributed system of services consisting of a large number of them. This allows you to give an error at an early stage and allowing the "fallen" service to restore its performance.

`Hystrix` allows you to define a fallback method that will be called when an unsuccessful call. What will happen in this fallback method is up to you.

You can read more in my article on the Circuit Breaker pattern:
    


If you are using Hystrix with `Feign Client`, then for this to work, you need to specify in the settings

    feign:
      hystrix:
         enabled: true

### Example:

We have 2 services:
- `Hystrix` is a REST-based microservice in which Circuit Breaker is implemented.
- `service_user-details` - receives any information on the user (which group it belongs to).

From the `Hystrix` service we will call the `service_user-details` service.

#### 1) Describe the `service_user-details`:

Specify the port in `application.properties`:

    server.port=8077

We create the `UsersServiceController` controller, in which the URL `"/getUsersDetailsByGroup/{group}"` will receive any information on the user on the group to which he belongs.
`(see service service_user-details)`

        @RequestMapping (value = "/ getUsersDetailsByGroup / {group}", method = RequestMethod.GET)
        public List getUsers <Users> (@PathVariable String group) {
            List <Users> usersList = map.get (group);
    
            if (usersList == null || usersList.isEmpty ()) {
                usersList = new ArrayList <> ();
                Users users = new Users ("Users not found", null);
                usersList.add (users);
            }
    
            return usersList;
        }
    
model:

        public class Users {
    
            private String name;
            private Integer age;
        
            public Users (String name, Integer age) {
                this.name = name;
                this.age = age;
            }
    
        }
    
After starting the service by the URL `"http://localhost:8077/getUsersDetailsByGroup/coolman"` you will see data on these `cool guys.`

#### 2) Service `Hystrix`:

Specify the port:

    server.port = 8076
    
Above the main class, you must specify the annotations `@EnableCircuitBreaker` and` @EnableHystrixDashboard`

        @SpringBootApplication
        @EnableHystrixDashboard
        @EnableCircuitBreaker
        public class HystrixApplication {
        
            public static void main (String [] args) {
                SpringApplication.run (HystrixApplication.class, args);
            }
        
        }
    
Make a controller in which we call `"/getGroupDetails/{group}"` and get information about the group along with user data
To call information about the user, an already written method will be invoked in the controller in the `service_user-details` service.

        @RestController
        public class HystrixController {
        
            @Autowired
            UserService userservice;
        
            @RequestMapping (value = "/ getGroupDetails / {group}", method = RequestMethod.GET)
            public String getUsers (@PathVariable String group) {
                return userservice.callUserService (group);
            }
        
        }
    
To do this, we write in the package service `UserService`

        @Service
        public class UserService {
        
            @Autowired
            RestTemplate restTemplate;
        
            @HystrixCommand (fallbackMethod = "callUserService_Fallback")
            public String callUserService (String group) {
                String response = restTemplate
                        .exchange ("http: // localhost: 8077 / getUsersDetailsByGroup / {group}"
                                , HttpMethod.GET
                                , null
                                , new ParameterizedTypeReference <String> () {
                                }, group) .getBody ();
        
                return "It's OK: group:" + group + "users details" + response + new Date ();
            }
        
            @SuppressWarnings ("unused")
            private String callUserService_Fallback (String group) {
                return "Error! Default info ..." + new Date ();
            }
        
            @Bean
            public RestTemplate restTemplate () {
                return new RestTemplate ();
            }
        
        }
    
- Here we use `RestTemplate` (for a change. In the following example we will try to use `Feign Client`)
- Use the @HystrixCommand annotation with the parameters of an additional method, which will be called if the service_user-details is not available
    
        @HystrixCommand (fallbackMethod = "callUserService_Fallback")
    
This method should have the same signature with the `callUserService` method (get group and user data)

Now start both services.
Try calling the `service_user-details` service from the` Hystrix` service
    
    http://localhost:8076/getGroupDetails/badboy
    
You will see `Sylvester Stallone and Chuck Norris`.

Simulate the denial of service `service_user-details` (problems in the network, service overload, etc.) and disable it
Try to use the URL again.
    
    http://localhost:8076/getGroupDetails/badboy
    
It will work out the spare method `callUserService_Fallback`
    
    Error! Default info ...
   
I gave the text output as a simple and illustrative example.
Instead of the Error type label, you can think of anything you like, up to receiving some custom data from the database and providing it to the user or redirecting to another service (replica of the service_user-details service) if you have a streaming system, where up to a couple of seconds means a lot to the service provider.

This was just one of the simplest examples of how to use Hystrix.
Other examples will be added here as `example_2, 3, etc`.

Hystrix also provides dashboards by URL.
    
    http://localhost:8077/hystrix.stream
    
There you will see the results of the health check along with all the service calls that are monitored using Hystrix.

Initial dashboard screen by URL
    
    http://localhost:8077/hystrix
    
Add there
    
    http://localhost:8077/hystrix.stream
    
and you will get statistics in the form of a chart.

Check out my Netflix OSS talk

    https://github.com/ksereda/Gallery-Service
