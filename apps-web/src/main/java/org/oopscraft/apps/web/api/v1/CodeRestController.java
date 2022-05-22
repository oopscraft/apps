package org.oopscraft.apps.web.api.v1;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.code.Code;
import org.oopscraft.apps.core.code.CodeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/code")
@RequiredArgsConstructor
public class CodeRestController {

   private final CodeService codeService;

   @GetMapping
   public List<Code> getCodes(){
      return codeService.getCodes();
   }

   @PutMapping
   public void saveCode(Code code) {
      codeService.saveCode(code);
   }

}

