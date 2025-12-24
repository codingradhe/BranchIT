# Secret Management in Kotlin Multiplatform Projects
- Access secrets in code via platform-specific APIs.
- Store secrets in `local.properties` (Android), `.env` (Desktop), or environment variables (iOS).

## Example

- CI/CD secret management (GitHub Actions, Bitrise, etc.)
- [dotenv](https://github.com/cdimascio/dotenv-kotlin)
- [gradle-properties-plugin](https://github.com/stevesaliman/gradle-properties-plugin)

## Tools

- Add secret files to `.gitignore`.
- For Desktop, use environment variables or external config files.
- For iOS, use Xcode's `xcconfig` or environment variables.
- For Android, use `local.properties` for local secrets.
- Use environment variables or encrypted files for secret storage.
- **Never commit secrets** (API keys, passwords, tokens) to version control.

## Guidelines


