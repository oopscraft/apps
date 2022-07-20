package org.oopscraft.apps.core.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractRepositoryTest;

import java.util.UUID;

@RequiredArgsConstructor
public class UserRepositoryTest extends AbstractRepositoryTest {

    private final UserRepository userRepository;

    @Test
    public void saveUser() {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("test_name")
                .build();
        userRepository.save(user);
    }

}
