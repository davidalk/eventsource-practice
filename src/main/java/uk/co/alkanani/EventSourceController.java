package uk.co.alkanani;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@EnableAutoConfiguration
public class EventSourceController {
    private static final long TIMEOUT = 200;
    private static final long SLEEP = 5000;

    @RequestMapping(path="/", method = RequestMethod.GET)
    public SseEmitter get() {
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT * SLEEP);

        final AtomicInteger messageCount = new AtomicInteger(0);
        Runnable messageThread = () -> {
            while (messageCount.get() < TIMEOUT) {
                try {
                    sseEmitter.send(messageCount.incrementAndGet());
                    Thread.sleep(SLEEP);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(messageThread);
        return sseEmitter;
    }

    public static void main(String... args) {
        SpringApplication.run(EventSourceController.class, args);

    }
}