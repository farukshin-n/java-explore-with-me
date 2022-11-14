package ru.practicum.ewmservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewmservice.dto.NewEventDto;
import ru.practicum.ewmservice.model.Category;
import ru.practicum.ewmservice.model.Location;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewEventDtoTest {
    @Autowired
    private JacksonTester<NewEventDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        NewEventDto dto = new NewEventDto(
                "concert",
                "cool concert",
                "Evanescence",
                1L,
                true,
                new Location(0L,0.143, 1.5),
                LocalDateTime.now().plusHours(3),
                5,
                true
        );
        JsonContent<NewEventDto> result = jacksonTester.write(dto);

        assertThat(result).hasJsonPath("$.title");
        assertThat(result).hasJsonPath("$.annotation");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.category");
        assertThat(result).hasJsonPath("$.paid");
        assertThat(result).hasJsonPath("$.location");
        assertThat(result).hasJsonPath("$.eventDate");
        assertThat(result).hasJsonPath("$.participantLimit");
        assertThat(result).hasJsonPath("$.requestModeration");
        assertThat(result).extractingJsonPathValue("$.title").isEqualTo(dto.getTitle());
        assertThat(result).extractingJsonPathValue("$.annotation").isEqualTo(dto.getAnnotation());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(dto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.category").isEqualTo(dto.getCategory().intValue());
        assertThat(result).extractingJsonPathValue("$.paid").isEqualTo(dto.isPaid());
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit")
                .isEqualTo(dto.getParticipantLimit());
        assertThat(result).extractingJsonPathValue("$.requestModeration")
                .isEqualTo(dto.isRequestModeration());
    }

}
