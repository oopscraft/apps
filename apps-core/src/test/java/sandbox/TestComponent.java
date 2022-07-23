package sandbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestComponent {

    /**
     * getValue
     * @param value
     * @return
     */
    public String getValue(String value) {
        return value;
    }

}
