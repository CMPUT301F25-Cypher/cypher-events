# Contributing Guidelines

## Branch Model
- **main** → stable release branch (protected)
- **dev** → active development branch
- Each feature → its own branch (`feature/<short-name>`)
- Merge feature branches into `dev` via Pull Requests

## Pull Requests
- All PRs must target **dev**
- Follow naming: `feature/<US-ID>-<short-description>`
- Fill out the PR template and link relevant user stories
- Ensure CI passes before requesting review

## Code Reviews
- At least 1 approval required before merging
- Fix all CI or lint errors before merge

## Commit Messages
- Be descriptive and consistent:
  - Example: `feat(auth): add login screen`
  - Example: `fix(repo): handle null Firebase snapshot`

## Testing
- Always run local unit tests before pushing
- Ensure CI is green after every PR