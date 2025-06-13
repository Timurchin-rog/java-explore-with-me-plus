package ru.practicum.ewm;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Service
public class StatsClient {
    private final RestClient restClient;
    @Value("stats-server-url")
    String statsServiceUrl;

    public StatsClient() {
        restClient = RestClient.create(statsServiceUrl);
    }

    // Регистрация "хита"
    public void registerHit(@Valid NewHitDto hitDto) {
        try {
            restClient.post()
                    .uri(statsServiceUrl + "/hit")
                    .body(hitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("ошибка при отправке hit");
        }
    }

    // Получение статистики
    public List<ViewDto> getStats(
            String start,
            String end,
            List<String> uris,
            boolean unique
    ) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(statsServiceUrl + "/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParam("uris", uris)
                    .queryParam("unique", unique)
                    .build()
                    .toUriString();

            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            log.error("ошибка при получении статистики");
        }
        return List.of();
    }
}
