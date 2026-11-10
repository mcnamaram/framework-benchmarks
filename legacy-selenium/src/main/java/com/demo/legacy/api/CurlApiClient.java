package com.demo.legacy.api;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy curl wrapper — wraps the {@code curl} binary via {@code ProcessBuilder}.
 *
 * <p><b>Quality shortcuts this wrapper takes (technical debt):</b>
 * <ul>
 *   <li><b>Process spawn per HTTP call</b> — each request forks an OS process ({@code curl}),
 *       adding ~15–25ms overhead and introducing flakiness from shell escaping, env vars,
 *       and curl version differences.</li>
 *   <li><b>Manual {@code JSONObject} field walking</b> — responses are parsed into raw
 *       {@code JSONObject} with string keys ({@code getInt("id")}, {@code getString("name")}).
 *       No compile-time safety; missing keys throw runtime {@code JSONException}.</li>
 *   <li><b>No auth reuse</b> — each test re-authenticates or manually passes tokens/cookies.
 *       No shared context with UI tests; brittle hand-off between API and UI layers.</li>
 *   <li><b>No schema/contract validation</b> — raw {@code JSONObject} with string keys means
 *       no JSON Schema validation, no type-safe contract enforcement.</li>
 *   <li><b>No IDE autocomplete or refactor safety</b> — string keys require find-and-replace
 *       on schema changes; no compile-time checking of response field access.</li>
 * </ul>
 *
 * <p><b>Contrast with modern {@code ApiClientService} (Playwright {@link com.microsoft.playwright.APIRequestContext}):</b>
 * <ul>
 *   <li>In-JVM HTTP client — no process spawn.</li>
 *   <li>Gson → POJO ({@link com.demo.modern.dto.UserResponse}, {@link com.demo.modern.dto.PostResponse})
 *       — compile-time safe, refactor-safe, IDE-completable getters.</li>
 *   <li>Shared {@code APIRequestContext} + {@code storageState()} — cookies/headers auto-synced to
 *       browser contexts; one login, reusable auth across API and UI.</li>
 *   <li>POJO + AssertJ assertions; optional {@code jackson-json-schema} for JSON Schema validation.</li>
 *   <li>Pure JVM, deterministic, well-tested HTTP client.</li>
 * </ul>
 *
 * <p>This class is <b>intentionally left unchanged</b> to represent the legacy "current state"
 * for the demo. The annotations exist solely to foreground the quality shortcuts for the
 * presentation narrative: "here's the old way cutting corners, here's doing it right."
 */
public class CurlApiClient {
  public JSONObject get(String url) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add("curl"); cmd.add("-s"); cmd.add("-X"); cmd.add("GET"); cmd.add(url);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) { sb.append(line); }
    process.waitFor();
    return new JSONObject(sb.toString());
  }

  public JSONObject post(String url, String body) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add("curl"); cmd.add("-s"); cmd.add("-X"); cmd.add("POST"); cmd.add("-H"); cmd.add("Content-Type: application/json");
    cmd.add("-d"); cmd.add(body); cmd.add(url);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) { sb.append(line); }
    process.waitFor();
    return new JSONObject(sb.toString());
  }

  public JSONObject put(String url, String body) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add("curl"); cmd.add("-s"); cmd.add("-X"); cmd.add("PUT"); cmd.add("-H"); cmd.add("Content-Type: application/json");
    cmd.add("-d"); cmd.add(body); cmd.add(url);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) { sb.append(line); }
    process.waitFor();
    return new JSONObject(sb.toString());
  }

  public JSONObject delete(String url) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add("curl"); cmd.add("-s"); cmd.add("-X"); cmd.add("DELETE"); cmd.add(url);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) { sb.append(line); }
    process.waitFor();
    return new JSONObject(sb.toString());
  }

  public JSONObject patch(String url, String body) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add("curl"); cmd.add("-s"); cmd.add("-X"); cmd.add("PATCH"); cmd.add("-H"); cmd.add("Content-Type: application/json");
    cmd.add("-d"); cmd.add(body); cmd.add(url);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) { sb.append(line); }
    process.waitFor();
    return new JSONObject(sb.toString());
  }

  public JSONObject head(String url) throws Exception {
    List<String> cmd = new ArrayList<>();
    cmd.add("curl"); cmd.add("-s"); cmd.add("-I"); cmd.add(url);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    Process process = pb.start();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) { sb.append(line); }
    process.waitFor();
    return new JSONObject(sb.toString());
  }
}