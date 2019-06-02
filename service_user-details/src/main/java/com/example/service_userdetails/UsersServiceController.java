package com.example.service_userdetails;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UsersServiceController {

    private static Map<String, List<Users>> map = new HashMap<>();

    static {
        map = new HashMap<>();

        List<Users> list = new ArrayList();
        Users users = new Users("John Wick", 40);
        list.add(users);
        users = new Users("Nikolas Cage", 42);
        list.add(users);

        map.put("coolman", list);

        list = new ArrayList();
        users = new Users("Sylvester Stallone", 43);
        list.add(users);
        users = new Users("Chuck Norris", 41);
        list.add(users);

        map.put("badboy", list);

    }

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

}
