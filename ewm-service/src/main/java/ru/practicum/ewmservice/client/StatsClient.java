package ru.practicum.ewmservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.ewmservice.dto.EndPointHit;
import ru.practicum.ewmservice.exception.StatsErrorException;
import ru.practicum.ewmservice.model.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClient {
    private final WebClient webClient;

    public void sendHit(EndPointHit endpointHit) {
        String url = "/hit";
        ResponseEntity<Void> responseEntity = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(endpointHit), EndPointHit.class)
                .retrieve()
                .toBodilessEntity()
                .block();
        if (responseEntity == null) {
            throw new StatsErrorException(
                    "Get error while saving statistics.");
        } else if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new StatsErrorException(String.format(
                    "Get error while saving statistics. The server returned a response code=%s",
                    responseEntity.getStatusCode()));
        }

    }

    public Map<Long, Long> getStats(List<Long> eventIds) {
        return getStats(eventIds, true);
    }

    public Map<Long, Long> getStats(List<Long> eventIds, boolean unique) {
        if (eventIds != null && eventIds.size() == 0)
            return new HashMap<>();
        StringBuilder sbUrl = new StringBuilder();
        String dateStart = "1100-01-01 00:00:00";
        String dateEnd = "2100-01-01 00:00:00";
        sbUrl.append("/stats")
                .append("?start=")
                .append(URLEncoder.encode(dateStart, StandardCharsets.UTF_8))
                .append("&end=")
                .append(URLEncoder.encode(dateEnd, StandardCharsets.UTF_8))
                .append("&unique=").append(unique);
        if (eventIds != null) {
            sbUrl.append("&uris=");
            Iterator<Long> id = eventIds.iterator();
            sbUrl.append("/events/")
                    .append(id.next());
            while (id.hasNext()) {
                sbUrl.append("/events/").append(id.next());
            }
        }

        List<ViewStats> resp = webClient.get()
                .uri(sbUrl.toString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStats>>() {
                })
                .block();

        if (resp == null) {
            return new HashMap<>();
        } else {
            return resp.stream()
                    .collect(Collectors.toMap(
                            vs -> Long.parseLong(vs.getUri().split("/", 0)[2]),
                            ViewStats::getHits));
        }
    }
}
