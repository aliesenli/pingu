package ch.pingu.backend.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer lenientLocalDateTimeCustomizer() {
        return builder -> {
            JavaTimeModule module = new JavaTimeModule();
            module.addDeserializer(LocalDateTime.class, new LenientLocalDateTimeDeserializer());
            builder.modules(module);
        };
    }

    static class LenientLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
        protected LenientLocalDateTimeDeserializer() {
            super(LocalDateTime.class);
        }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) return null;

            try { return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME); } catch (DateTimeParseException ignored) {}
            try { return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime(); } catch (DateTimeParseException ignored) {}
            try { return LocalDateTime.ofInstant(Instant.parse(text), ZoneOffset.UTC); } catch (DateTimeParseException ignored) {}

            DateTimeFormatter[] candidates = new DateTimeFormatter[] {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            };
            for (DateTimeFormatter f : candidates) {
                try { return LocalDateTime.parse(text, f); } catch (DateTimeParseException ignored) {}
            }
            
            String message = "Invalid date-time format. Provide one of: " +
                    "'yyyy-MM-ddTHH:mm', 'yyyy-MM-ddTHH:mm:ss', 'yyyy-MM-ddTHH:mm:ss.SSS', " +
                    "or ISO-8601 with offset like '2026-03-08T22:54:55.505Z' or '2026-03-08T23:54:55+01:00'. " +
                    "Value received: '" + text + "'";
            throw InvalidFormatException.from(p, message, text, LocalDateTime.class);
        }
    }
}
