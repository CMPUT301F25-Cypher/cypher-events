# Coding Style Guidelines

This project follows standard Android + Java conventions:

### Java
- 4-space indentation, no tabs
- Classes: PascalCase (`EventRepository`)
- Methods & variables: camelCase (`createDraft`)
- Constants: ALL_CAPS (`MAX_SIZE`)
- Curly braces on new lines
- Max line length: 100 chars
- Always include Javadoc for public classes and methods

### XML
- Use lowercase_underscore for resource names
- Indent nested elements consistently
- Keep layout IDs descriptive (`@+id/button_submit`)

### General
- No hardcoded strings → use `strings.xml`
- No unused imports
- Keep methods short and modular
- Write clear commit messages

**CI checks**: The GitHub Action validates that the project builds and tests pass successfully.
