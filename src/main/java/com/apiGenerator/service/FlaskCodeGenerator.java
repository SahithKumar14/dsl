package com.apiGenerator.service;

import com.apiGenerator.model.ApiSpecification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FlaskCodeGenerator {

        public String generate(ApiSpecification spec) throws Exception {
                Path outputDir = Paths.get("generated", spec.getBaseClassName(), "python");
                Files.createDirectories(outputDir);

                Path appFile = outputDir.resolve("app.py");
                StringBuilder code = new StringBuilder("from flask import Flask, jsonify\n\napp = Flask(__name__)\n\n");

                for (Map<String, Object> endpoint : spec.getEndpoints()) {
                        String path = endpoint.get("Path").toString();
                        List<String> methods = (List<String>) endpoint.get("Methods");
                        code.append(String.format("@app.route('%s', methods=%s)\n", path, methods));
                        code.append("def handler():\n    return jsonify({'message': 'Success'})\n\n");
                }

                code.append("if __name__ == '__main__':\n    app.run(debug=True)\n");

                Files.write(appFile, code.toString().getBytes());
                return outputDir.toString();
        }
}
