document.addEventListener('DOMContentLoaded', () => {
    const apiSpecInput = document.getElementById('apiSpecInput');
    const generateBtn = document.getElementById('generateBtn');
    const downloadBtn = document.getElementById('downloadBtn');
    const codeOutput = document.getElementById('codeOutput');
    const errorOutput = document.getElementById('errorOutput');
    const languageCheckboxes = document.querySelectorAll('input[name="languages"]');

    let generatedCode = {};

    // Validation function
    async function validateApiSpec(apiSpec) {
        try {
            const response = await fetch('/api/generator/validate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(apiSpec)
            });

            if (!response.ok) {
                const errors = await response.json();
                throw new Error(errors.join('\n'));
            }
            return true;
        } catch (error) {
            errorOutput.textContent = `Validation Error: ${error.message}`;
            return false;
        }
    }

    // Code generation function
    async function generateCode() {
        try {
            // Clear previous errors
            errorOutput.textContent = '';

            // Parse input JSON
            const apiSpec = JSON.parse(apiSpecInput.value);

            // Get selected languages
            const selectedLanguages = Array.from(languageCheckboxes)
                .filter(cb => cb.checked)
                .map(cb => cb.value);

            // Prepare request
            const request = {
                apiSpecification: apiSpec,
                languages: selectedLanguages
            };

            // Validate API spec
            if (!await validateApiSpec(apiSpec)) return;

            // Send to backend
            const response = await fetch('/api/generator/generate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request)
            });

            if (!response.ok) {
                const errors = await response.json();
                throw new Error(errors.join('\n'));
            }

            // Store and display generated code
            generatedCode = await response.json();
            displayCode(Object.keys(generatedCode)[0]);
        } catch (error) {
            errorOutput.textContent = `Generation Error: ${error.message}`;
        }
    }

    // Download function
    async function downloadCode() {
        try {
            // Parse input JSON
            const apiSpec = JSON.parse(apiSpecInput.value);

            // Get selected languages
            const selectedLanguages = Array.from(languageCheckboxes)
                .filter(cb => cb.checked)
                .map(cb => cb.value);

            // Prepare request
            const request = {
                apiSpecification: apiSpec,
                languages: selectedLanguages
            };

            // Send to backend
            const response = await fetch('/api/generator/download', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(request)
            });

            if (!response.ok) {
                throw new Error('Download failed');
            }

            // Trigger download
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${apiSpec.serviceName}_clients.zip`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        } catch (error) {
            errorOutput.textContent = `Download Error: ${error.message}`;
        }
    }

    // Event Listeners
    generateBtn.addEventListener('click', generateCode);
    downloadBtn.addEventListener('click', downloadCode);
});