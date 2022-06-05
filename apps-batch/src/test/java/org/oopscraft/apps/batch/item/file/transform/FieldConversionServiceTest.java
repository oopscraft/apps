package org.oopscraft.apps.batch.item.file.transform;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldConversionServiceTest {

    @Test
    public void defaultTest() {
        FieldConversionService conversionService = new FieldConversionService();
        assertEquals(conversionService.convert("2011-01-01 01:02:03", LocalDateTime.class), LocalDateTime.of(2011,1,1,1,2,3));
    }
}
