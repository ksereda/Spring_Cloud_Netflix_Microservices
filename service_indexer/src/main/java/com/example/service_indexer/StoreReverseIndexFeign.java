package com.example.service_indexer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.List;

@FeignClient("STOREINVERTEDINDEX")
public interface StoreReverseIndexFeign {

    @RequestMapping(method= RequestMethod.POST, value="/reverseindex")
    public Integer storeReverseIndex(List<Pair> pairs);

}