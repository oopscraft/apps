package org.oopscraft.apps.core.code;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.RepositoryTest;

import java.util.ArrayList;

@RequiredArgsConstructor
public class CodeRepositoryTest extends RepositoryTest {

    private final CodeRepository codeRepository;

    @Test
    public void saveCode() {
        Code code = Code.builder()
                .id("test_code")
                .name("Test Code")
                .note("Note")
                .items(new ArrayList<CodeItem>())
                .build();
        codeRepository.saveAndFlush(code);
    }
}
