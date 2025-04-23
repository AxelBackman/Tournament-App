package com.pvt.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(path="/pet")
@CrossOrigin
public class MainController {

    @GetMapping(value="/helloAxel")
    public @ResponseBody String hello() {
        return "Hello from Axel";
    }
    @GetMapping(value="/helloSimon")
    public @ResponseBody String helloFromSimon() {
        return "Hello from Simon";
    }

    @GetMapping(value="/helloMarina")
    public @ResponseBody String helloFromMarina() {
        return "Hello from Marina";
    }
    
    @GetMapping(value="/helloIliya")
    public @ResponseBody String helloFromIliya() {
    	return "Hello from Iliya";
    }

    //Ok
    
    

}
