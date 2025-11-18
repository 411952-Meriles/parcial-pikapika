package utn.frc.piv.parcial.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GreetingsClientServiceTest {

    private static final String BASE_URL = "http://greetings-be:8080";

    @Mock
    private RestTemplate restTemplate;

    private GreetingsClientService greetingsClientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        greetingsClientService = new GreetingsClientService(restTemplate, BASE_URL);
    }

    @Test
    @DisplayName("fetchGreeting should return body from greetings service")
    void fetchGreetingReturnsBody() {
        when(restTemplate.getForObject(BASE_URL, String.class)).thenReturn("hello");

        String result = greetingsClientService.fetchGreeting();

        assertThat(result).isEqualTo("hello");
        verify(restTemplate).getForObject(BASE_URL, String.class);
    }
}
