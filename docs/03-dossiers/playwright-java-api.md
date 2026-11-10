# Dossier: Playwright Java API Testing (APIRequestContext)

**Source:** <https://playwright.dev/java/docs/api-testing> (official docs) — retrieved 2026-10-29.

## Key Extract

Playwright can access REST APIs via `APIRequestContext`, replacing curl wrappers. It supports GET/POST/PUT/PATCH/DELETE/HEAD with headers, JSON bodies, form data, multipart, and base URL configuration.

**Example (Java):**

```java
APIRequestContext request = playwright.request().newContext(
    new APIRequest.NewContextOptions().setBaseURL("https://api.github.com"));
APIResponse response = request.post("/repos/" + user + "/" + repo + "/issues",
    RequestOptions.create().setData(data));
assertTrue(response.ok());
```

**Auth reuse:** `APIRequestContext` shares cookies/state with browser contexts, allowing API login then UI open with shared session.
