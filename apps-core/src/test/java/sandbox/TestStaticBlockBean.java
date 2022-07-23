package sandbox;

import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class TestStaticBlockBean {

    @Setter
    public static int value = 0;

    static {
        value = 1/value;
    }

    public int getValue() {
        return value;
    }

}
