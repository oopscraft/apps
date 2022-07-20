package org.oopscraft.apps.core.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.oopscraft.apps.core.test.AbstractServiceTest;

import java.util.UUID;

@RequiredArgsConstructor
public class UserServiceTest extends AbstractServiceTest {

    private final UserService userService;

    @Test
    public void saveUser() {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("test_usr")
                .build();
        userService.saveUser(user);
    }

}
