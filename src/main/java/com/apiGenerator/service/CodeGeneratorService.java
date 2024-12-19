package com.apiGenerator.service;

import com.apiGenerator.model.ApiSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeGeneratorService {

    @Autowired
    private FlaskCodeGenerator flaskCodeGenerator;

    @Autowired
    private SpringBootCodeGenerator springBootCodeGenerator;

    public Map<String, String> generateCode(ApiSpecification spec, List<String> languages) throws Exception {
        Map<String, String> generatedCode = new HashMap<>();

        for (String language : languages) {
            switch (language.toLowerCase()) {
                case "python":
                    generatedCode.put("python", flaskCodeGenerator.generate(spec));
                    break;
                case "java":
                    generatedCode.put("java", springBootCodeGenerator.generate(spec));
                    break;
                default:
                    throw new Exception("Unsupported language: " + language);
            }
        }

        return generatedCode;
    }
}
