package com.example.hystrix.exqmple_3;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *  Здесь мы передаем объект ошибки (из класса StatisticFallbackFactory) в наш новый класс ServiceFeignClientFallback, который используется для обработки этой ошибки
 *
 */

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
