package utn.frc.piv.parcial.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GreetingsClientService {

    private final RestTemplate restTemplate;
    private final String greetingsBaseUrl;

    public GreetingsClientService(RestTemplate restTemplate,
                                  @Value("${greetings.service.base-url}") String greetingsBaseUrl) {
        this.restTemplate = restTemplate;
        this.greetingsBaseUrl = greetingsBaseUrl;
    }

    public String fetchGreeting() {
        log.debug("Calling greetings service at: {}", greetingsBaseUrl);
        return restTemplate.getForObject(greetingsBaseUrl, String.class);
    }
}
