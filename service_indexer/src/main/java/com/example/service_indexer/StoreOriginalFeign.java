package com.example.service_indexer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("STOREORIGINALDATA")
public interface StoreOriginalFeign {

    @RequestMapping(method = RequestMethod.POST, value="/text/")
    SaverResponse saveOriginalText(@RequestBody String text);

    class SaverResponse {
        public Integer getId() {
            return id;
        }

        private Integer id;
    }

}
