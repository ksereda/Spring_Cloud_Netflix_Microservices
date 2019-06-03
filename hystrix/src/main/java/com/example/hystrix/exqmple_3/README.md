### RUS

#### Обработка ошибок Hystrix с Feign Client

Например наш `Feign Client` отправляет запрос `/users/{id}/statistics` на удаленный сервис `service_statistics.`
Чтобы разрешить `Feign` использовать класс `Fallback`, который может обрабатывать сообщения об ошибках, нам необходимо указать в аннотации `fallbackFactory`.

    @FeignClient(name = "service_statistics", fallbackFactory = StatisticFallbackFactory.class)
    public interface ServiceFeignClient {
    
        @GetMapping("/users/{id}/statistics")
        public List<UserModel> getStatistics(@PathVariable String id);
    
    }

StatisticFallbackFactory:

Класс должен быть помечен как бин `(@Component)` и должен имплементировать интерфейс `FallbackFactory` `(в дженериках наш Feign Client)`
У нас есть доступ к объекту `Throwable`, с помощью которого можно получить необходимую информацию об ошибке.
Возвращаемое значение - `ServiceFeignClientFallback` - новый кастомный класс для обработки ошибки.

    @Component
    public class StatisticFallbackFactory implements FallbackFactory<ServiceFeignClient> {
        
        @Override
        public ServiceFeignClient create(Throwable cause) {
            return new ServiceFeignClientFallback(cause);
        }
        
    }
    
Класс ServiceFeignClientFallback:
Мы передаем объект ошибки `(из класса StatisticFallbackFactory)` в наш новый класс `ServiceFeignClientFallback`, который используется для обработки этой ошибки

    public class ServiceFeignClientFallback implements ServiceFeignClient {
        
        Logger logger =  LoggerFactory.getLogger(this.getClass());
        private final Throwable cause;
        
        public ServiceFeignClientFallback(Throwable cause) {
            this.cause = cause;
        }
    
        @Override
        public List<UserModel> getStatistics(String id) {
            if (cause instanceof FeignException && ((FeignException) cause).status() == 404)  {
                logger.error("404 page not found" + id
                 + "error message: " + cause.getLocalizedMessage());
            } else {
                logger.error("Other error took place: " + cause.getLocalizedMessage());
            }
            
            return new ArrayList();
        }
        
    }
    
____

### ENG

#### Hystrix Error Handling with Feign Client

For example, our `Feign Client` sends a request `/users/{id}/statistics` to a remote service_statistics service.
To allow Feign to use the `Fallback` class, which can handle error messages, we need to specify the `fallbackFactory` in the annotation

        @FeignClient (name = "service_statistics", fallbackFactory = StatisticFallbackFactory.class)
        public interface ServiceFeignClient {
        
            @GetMapping ("/ users / {id} / statistics")
            public List <UserModel> getStatistics (@PathVariable String id);
        
        }

StatisticFallbackFactory:

The class should be marked as a bin (`@Component`) and should implement the `FallbackFactory` interface `(in generics, our Feign Client)`
We have access to the `Throwable` object, with which you can get the necessary information about the error.
The return value - `ServiceFeignClientFallback` is a new custom class for handling errors.

        @Component
        public class StatisticFallbackFactory implements FallbackFactory <ServiceFeignClient> {
            
            @Override
            public ServiceFeignClient create (Throwable cause) {
                return new ServiceFeignClientFallback (cause);
            }
            
        }
    
ServiceFeignClientFallback class:
We pass an error object `(from the StatisticFallbackFactory class)` to our new `ServiceFeignClientFallback` class, which is used to handle this error.

        public class ServiceFeignClientFallback implements ServiceFeignClient {
            
            Logger logger = LoggerFactory.getLogger (this.getClass ());
            private final Throwable cause;
            
            public ServiceFeignClientFallback (Throwable cause) {
                this.cause = cause;
            }
        
            @Override
            public list <UserModel> getStatistics (String id) {
                if (cause instanceof FeignException && ((FeignException) cause) .status () == 404) {
                    logger.error ("404 page not found" + id
                     + "error message:" + cause.getLocalizedMessage ());
                } else {
                    logger.error ("Other error took place:" + cause.getLocalizedMessage ());
                }
                
                Return new ArrayList ();
            }
            
        }