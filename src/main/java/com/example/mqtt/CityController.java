package com.example.mqtt;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("admin/scheduler")
@AllArgsConstructor
public class CityController {
    private CityService cityService;
    private static final String SCHEDULED_TASKS = "scheduledTasks";

    private ScheduledAnnotationBeanPostProcessor postProcessor;

    private MessageQueue messageQueue;

    @GetMapping("/stop")
    public String stopSchedule() {
        postProcessor.postProcessBeforeDestruction(messageQueue, SCHEDULED_TASKS);
        return "OK";
    }

    @GetMapping("/start")
    public String startSchedule() {
        postProcessor.postProcessAfterInitialization(messageQueue, SCHEDULED_TASKS);
        return "OK";
    }

    @GetMapping("current")
    public ResponseEntity<String> getCurrentCity() {
        return new ResponseEntity<>(cityService.getCurrentCityById().getCityName(), HttpStatus.OK);
    }

    @PatchMapping("{city}")
    public String changeCity(@PathVariable String city) {

        cityService.updateCity(city);
        return "OK";
    }
}
