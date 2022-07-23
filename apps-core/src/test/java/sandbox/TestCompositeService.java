package sandbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCompositeService {

    private final TestComponent testComponent;

    private final TestMapper testMapper;

    public String selectValue(String value) {
        return testMapper.selectValue(value);
    }

    public String getValue(String value) {
        return testComponent.getValue(value);
    }

}
