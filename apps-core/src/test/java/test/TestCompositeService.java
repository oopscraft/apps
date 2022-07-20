package test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCompositeService {

    private final TestComponent testComponent;

    private final TestMapper testMapper;

    public String selectValue(String value) {
        return testMapper.selectValue(value);
    }

}
