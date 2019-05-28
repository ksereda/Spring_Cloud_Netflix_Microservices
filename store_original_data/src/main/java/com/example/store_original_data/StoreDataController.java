package com.example.store_original_data;

import com.example.store_original_data.dao.TextRepository;
import com.example.store_original_data.entity.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
public class StoreDataController {

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private TextSaver textSaver;

    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/text")
    public TextSaver.SaveResponse addText(@RequestBody String text) {
        return textSaver.saveTextToDb(text);
    }

    @RequestMapping("/text/{id}")
    public Text getText(@PathVariable Integer id){
        Optional<Text> response = textRepository.findById(id);
        return response.orElseGet(Text::new);
    }

}
