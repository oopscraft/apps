package org.oopscraft.apps.core.code;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodeService {

    private final CodeRepository codeRepository;

    /**
     * get codes
     * @return code list
     */
    public List<Code> getCodes() {
        return codeRepository.findAll();
    }

    /**
     * save code
     * @param code
     */
    public void saveCode(Code code) {
        Code one = codeRepository.findById(code.getId()).orElse(null);
        if(code == null) {
            one = Code.builder()
                    .id(code.getId())
                    .build();
        }
        one.setName(code.getName());
        one.setNote(code.getNote());
        one.setItems(code.getItems());
    }


}
