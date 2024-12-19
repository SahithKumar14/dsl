package com.apiGenerator.service;

import com.apiGenerator.model.ApiSpecification;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.nio.file.Paths;

@Service
public class SpringBootCodeGenerator {

    public String generate(ApiSpecification spec) throws Exception {
        Path outputDir = Paths.get("generated", spec.getBaseClassName(), "java");
        Files.createDirectories(outputDir);

        Path controllerFile = outputDir.resolve(spec.getBaseClassName() + "Controller.java");
        StringBuilder code = new StringBuilder("package com.example.api;\n\n");
        code.append("import org.springframework.web.bind.annotation.*;\n");
        code.append("import java.util.Map;\n\n");
        code.append("@RestController\n@RequestMapping(\"/api\")\n");
        code.append(String.format("public class %sController {\n\n", spec.getBaseClassName()));

        for (Map<String, Object> endpoint : spec.getEndpoints()) {
            String path = endpoint.get("Path").toString();
            List<String> methods = (List<String>) endpoint.get("Methods");
            String method = methods.contains("GET") ? "GetMapping" : "PostMapping";

            code.append(String.format("@%s(\"%s\")\n", method, path));
            code.append("public Map<String, String> handler() {\n");
            code.append("    return Map.of(\"message\", \"Success\");\n");
            code.append("}\n\n");
        }

        code.append("}\n");
        Files.write(controllerFile, code.toString().getBytes());
        return outputDir.toString();
    }
}
