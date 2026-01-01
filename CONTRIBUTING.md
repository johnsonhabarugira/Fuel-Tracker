# Contributing

Thanks for helping improve Fuel Tracker!

## Workflow
1. Fork and create a feature branch from `main`.
2. Keep changes focused and small.
3. Follow the commit style below.
4. Validate locally: `mvn package`
5. Open a PR describing the change, testing done, and any follow-up items.

## Commit messages (Conventional Commits)
- `feat(cli): add list-cars command`
- `fix(api): validate odometer input`
- `docs(readme): clarify run instructions`
- `chore: add root gitignore`

## Coding/structure
- Keep backend and CLI modules separate; no cross-dependencies.
- Backend stays in-memory; no DB/auth unless explicitly scoped.

## Testing
- Run `mvn package` (builds backend and CLI).
- If you add tests, ensure they run with `mvn test`.

## Releases
- Tag releases as `vX.Y.Z` after merge to `main`.
