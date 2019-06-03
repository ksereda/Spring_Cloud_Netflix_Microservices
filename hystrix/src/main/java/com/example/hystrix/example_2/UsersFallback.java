package com.example.hystrix.example_2;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 *  1) Fallback class must be marked as @Component or created as @Bean
 *   2) Fallback class must implement Feign Client
 *   3) The signature of the backup method in the Fallback class must fully comply with the method signature in the Feign Client
 *
 */

@Component
public class UsersFallback implements ServiceFeignClient{

    @Override
    public List<UserModel> getStatistics(String id) {
        return new ArrayList();
    }

}
