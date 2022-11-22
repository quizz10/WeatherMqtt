package com.example.mqtt;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/scheduler")
@AllArgsConstructor
public class CityController {
    private CityService cityService;
    private static final String SCHEDULED_TASKS = "scheduledTasks";

    private ScheduledAnnotationBeanPostProcessor postProcessor;

    private MessageQueue messageQueue;

    @GetMapping(value = "/stop")
    public String stopSchedule() {
        postProcessor.postProcessBeforeDestruction(messageQueue, SCHEDULED_TASKS);
        return "OK";
    }

    @GetMapping(value = "/start")
    public String startSchedule() {
        postProcessor.postProcessAfterInitialization(messageQueue, SCHEDULED_TASKS);
        return "OK";
    }

    @PatchMapping("{city}")
    public String changeCity(@PathVariable String city) {
        cityService.updateCity(city);
        return "OK";
    }
}
