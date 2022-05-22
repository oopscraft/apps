package org.oopscraft.apps.core.test;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.CoreConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestConstructor;

import javax.transaction.Transactional;

@SpringBootTest(
    classes = {CoreConfiguration.class}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@Rollback
@Slf4j
public class RepositoryTest {
}
