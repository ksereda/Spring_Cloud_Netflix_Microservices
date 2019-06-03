package com.example.hystrix.exqmple_3;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 *  Класс должен быть помечен как бин (@Component) и должен имплементировать интерфейс FallbackFactory (в дженериках наш Feign Client)
 *  У нас есть доступ к объекту Throwable, с помощью которого можно получить необходимую информацию об ошибке.
 *  return ServiceFeignClientFallback - новый кастомный класс для обработки ошибки
 */

@Component
public class StatisticFallbackFactory implements FallbackFactory<ServiceFeignClient> {

    @Override
    public ServiceFeignClient create(Throwable cause) {
        return new ServiceFeignClientFallback(cause);
    }

}
