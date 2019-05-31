package com.example.ribbon;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class RibbonController {

//    @LoadBalanced
//    @Bean
//    RestTemplate getRestTemplate() {
//        return new RestTemplate();
//    }
//
//    @Autowired
//    RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {

        return "<a href='/testCallFTPService'>//testCallFTPService</a>";
    }

    @ResponseBody
    @RequestMapping(value = "/testCallFTPService", method = RequestMethod.GET)
    public String showFirstService() {

        String serviceId = "FTP Service".toLowerCase();

        // You need eureka.client.fetchRegistry=true in properties file
        List<ServiceInstance> instances = this.discoveryClient.getInstances(serviceId);

        if (instances == null || instances.isEmpty()) {
            return "No instances for service: " + serviceId;
        }
        String html = "<h2>Instances for Service Id: " + serviceId + "</h2>";

        for (ServiceInstance serviceInstance : instances) {
            html += "<h3>Instance :" + serviceInstance.getUri() + "</h3>";
        }

        // Create a RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        html += "<br><h4>Call /hello of service: " + serviceId + "</h4>";

        try {

            // May be throw IllegalStateException
            ServiceInstance serviceInstance = this.loadBalancer.choose(serviceId);

            html += "<br>===> Load Balancer choose: " + serviceInstance.getUri();

            String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/hello";

            html += "<br>Make a Call: " + url;
            html += "<br>";

            String result = restTemplate.getForObject(url, String.class);

            html += "<br>Result: " + result;
        } catch (IllegalStateException e) {
            html += "<br>loadBalancer.choose ERROR: " + e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            html += "<br>Other ERROR: " + e.getMessage();
            e.printStackTrace();
        }

        return html;
    }


}
