package com.pvt.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(path="/pet")
@CrossOrigin
public class MainController {

@GetMapping(value="/helloAxel")
    public @ResponseBody String hello() {
        return "Hello from AxelAxel";
    }
//     @GetMapping(value="/helloSimon")
//     public @ResponseBody String helloFromSimon() {
//         return "Hello from Simon";
//     }

//     @GetMapping(value="/helloMarina")
//     public @ResponseBody String helloFromMarina() {
//         return "Hello from Marina";
//     }
    
//     @GetMapping(value="/helloIliya")
//     public @ResponseBody String helloFromIliya() {
//     	return "Hello from Iliya!";
//     }

//     @GetMapping(value="/helloElnaz")
//     public @ResponseBody String helloFromElnaz() {
//     	return "Hello from Elnaz";
//     }

//     @GetMapping(value="/helloShoajb")
//     public @ResponseBody String helloFromShoajb() {
//     	return "Hello from Shoajb";
//     }
    
//     @GetMapping(value="/helloSara")
//     public @ResponseBody String helloFromSara() {
//     	return "Hello from Sara";
//     }

//     @Autowired
//     private SKEventRepository eventRepository;
    
//     @GetMapping(path = "/all")
//     public @ResponseBody Iterable<SKEvent> getAllEvents() {
//         return eventRepository.findAll();
//     }
    
//     @GetMapping(path = "/add/{eventName}/{eventDescription}")
//     public @ResponseBody String addNewEvent(@PathVariable String eventName, @PathVariable String eventDescription) {
//         SKEvent event = new SKEvent();
//         event.setName(eventName);
//         event.setDescription(eventDescription);
//         eventRepository.save(event);
//         return "Event '" + eventName + "' saved successfully";
}






//     // @Autowired
//     // private RegistrationRepository registrationRepository;

//     // @GetMapping(path = "/register")
//     // public @ResponseBody String registerToEvent(@PathVariable String eventName, @PathVariable String userName) {
//     //     try {
//     //         // Kontrollera att event finns
//     //         SKEvent event = eventRepository.findByName(eventName);
//     //         if (event == null) {
//     //             return "Fel: Event med namn '" + eventName + "' hittades inte!";
//     //         }

//     //         // Skapa användare och spara
//     //         User user = new User();
//     //         user.setUsername(userName);
//     //         userRepository.save(user);

//     //         // Skapa registrering och spara
//     //         Registration registration = new Registration();
//     //         registration.setUser(user);
//     //         registration.setEvent(event);
//     //         registrationRepository.save(registration);

//     //         return "Lyckades: User '" + userName + "' är registrerad till event '" + eventName + "'!";
//     //     } catch (Exception e) {
//     //         e.printStackTrace(); // Skriver ut felet i serverloggarna
//     //         return "Fel: Något gick fel vid registreringen.";
//     //     }
//     }
