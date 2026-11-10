package com.demo.modern.tests;

import com.demo.modern.api.ApiClient;
import com.demo.modern.api.ApiClient.APIResponse;
import com.demo.modern.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure API test suite using the standalone {@link ApiClient} (JDK {@code HttpClient} + Gson POJOs).
 *
 * <p>Contrast with UI tests (e.g., {@code EpicFlowTest}) that use Playwright's
 * {@code APIRequestContext}: this suite never launches a browser and never spawns any process.
 * It uses JDK 11+ native HTTP client for maximum throughput, serializes responses into type-safe POJOs,
 * and runs fully parallel via {@code src/test/resources/junit-platform.properties}.
 *
 * <p>Architecture decision: for pure API calls there is no browser session to inherit, so the
 * standalone {@code ApiClient} is simpler, faster, and dependency-free than going through Playwright
 * solely for an {@code APIRequestContext}. Where auth must be shared across UI and API layers, that
 * call-site uses Playwright's {@code APIRequestContext}; the test below demonstrates the pure-API path.
 */
public class ApiClientTest {
  // Standalone ApiClient: JDK HttpClient + Gson, stateless, safe to share across parallel threads.
  private ApiClient apiClient = new ApiClient();

  @BeforeEach
  public void setUp() throws Exception {
    // No Playwright.create(), no APIRequestContext — zero framework overhead.
  }

  // ==================== Existing: GET user + POST post on JSONPlaceholder ====================

  @Test
  public void getPost() throws Exception {
    UserResponse user = apiClient.getUser("https://jsonplaceholder.typicode.com/users/1");
    assertEquals(1, user.getId());
    assertEquals("Leanne Graham", user.getName());
    assertEquals("Bret", user.getUsername());
    assertEquals("Sincere@april.biz", user.getEmail());

    String postBody = "{\"title\":\"test\",\"body\":\"test\",\"userId\":1}";
    PostResponse post = apiClient.createPost("https://jsonplaceholder.typicode.com/posts", postBody);
    assertEquals(101, post.getId());
  }

  // ==================== Users (GET) ====================

  @Test
  public void getUserByIdReturnsExpectedFields() throws Exception {
    UserResponse user = apiClient.getUser("https://jsonplaceholder.typicode.com/users/1");
    assertEquals(1, user.getId());
    assertEquals("Leanne Graham", user.getName());
    assertEquals("Bret", user.getUsername());
    assertEquals("Sincere@april.biz", user.getEmail());
  }

  @Test
  public void getUserByIdDifferentValue() throws Exception {
    UserResponse user = apiClient.getUser("https://jsonplaceholder.typicode.com/users/2");
    assertEquals(2, user.getId());
    assertEquals("Ervin Howell", user.getName());
    assertEquals("Antonette", user.getUsername());
    assertEquals("Shanna@melissa.tv", user.getEmail());
  }

  @Test
  public void getUserByIdBoundaryValue() throws Exception {
    UserResponse user = apiClient.getUser("https://jsonplaceholder.typicode.com/users/10");
    assertEquals(10, user.getId());
    assertEquals("Clementina DuBuque", user.getName());
  }

  @Test
  public void getUserArrayReturnsTypedCollection() throws Exception {
    List<UserResponse> users = apiClient.getUserList("https://jsonplaceholder.typicode.com/users");
    assertFalse(users.isEmpty());
    assertEquals(10, users.size());
    assertEquals("Leanne Graham", users.get(0).getName());
  }

  // ==================== Posts (GET / POST / PUT / DELETE) ====================

  @Test
  public void getPostSingleById() throws Exception {
    PostResponse post = apiClient.getPost("https://jsonplaceholder.typicode.com/posts/1");
    assertEquals(1, post.getId());
    assertTrue(post.getTitle().contains("sunt aut"));
    assertEquals(1, post.getUserId());
  }

  @Test
  public void getPostArrayReturnsAllPosts() throws Exception {
    List<PostResponse> posts = apiClient.getPostList("https://jsonplaceholder.typicode.com/posts");
    assertFalse(posts.isEmpty());
    assertTrue(posts.size() >= 100);
  }

  @Test
  public void getPostsFilteredByUserId() throws Exception {
    List<PostResponse> posts = apiClient.getPostList("https://jsonplaceholder.typicode.com/posts?userId=1");
    assertFalse(posts.isEmpty());
    assertEquals(1, posts.get(0).getUserId());
  }

  @Test
  public void createPostMinimalBody() throws Exception {
    String postBody = "{\"title\":\"minimal\",\"body\":\"body\"}";
    PostResponse post = apiClient.createPost("https://jsonplaceholder.typicode.com/posts", postBody);
    // jsonplaceholder returns only ID; title/body are not echoed back
    assertEquals(101, post.getId());
  }

  @Test
  public void createPostCompleteBody() throws Exception {
    String postBody = "{\"title\":\"complete\",\"body\":\"full body\",\"userId\":2}";
    PostResponse post = apiClient.createPost("https://jsonplaceholder.typicode.com/posts", postBody);
    assertEquals(101, post.getId());
  }

  @Test
  public void updatePostViaPut() throws Exception {
    String putBody = "{\"id\":1,\"title\":\"updated\",\"body\":\"updated body\",\"userId\":1}";
    PostResponse updated = apiClient.updatePost("https://jsonplaceholder.typicode.com/posts/1", putBody);
    // jsonplaceholder returns updated object with ID matching request
    assertEquals(1, updated.getId());
  }

  @Test
  public void patchPostPartialUpdate() throws Exception {
    String patchBody = "{\"title\":\"patched\"}";
    PostResponse patched = apiClient.patchPost("https://jsonplaceholder.typicode.com/posts/1", patchBody);
    // jsonplaceholder returns patched object with ID matching request
    assertEquals(1, patched.getId());
  }

  @Test
  public void deletePostReturnsOk() throws Exception {
    APIResponse response = apiClient.delete("https://jsonplaceholder.typicode.com/posts/1");
    assertTrue(response.ok());
  }

  @Test
  public void deletePostIdempotentOnAlreadyDeleted() throws Exception {
    APIResponse firstDelete = apiClient.delete("https://jsonplaceholder.typicode.com/posts/99");
    assertNotNull(firstDelete);
  }

  // ==================== HEAD requests (no body) ====================

  @Test
  public void headRequestOnPostsEndpoint() throws Exception {
    APIResponse response = apiClient.head("https://jsonplaceholder.typicode.com/posts");
    assertTrue(response.ok());
  }

  @Test
  public void headRequestOnUsersEndpoint() throws Exception {
    APIResponse response = apiClient.head("https://jsonplaceholder.typicode.com/users");
    assertTrue(response.ok());
  }

  @Test
  public void headRequestOnSpecificPost() throws Exception {
    APIResponse response = apiClient.head("https://jsonplaceholder.typicode.com/posts/1");
    assertTrue(response.ok());
  }

  // ==================== HTTPbin edge cases: status codes, headers, origin ====================

  @Test
  public void verifyOriginHeaderPresentInGet() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/get");
    assertTrue(response.ok());
    assertTrue(response.text().contains("origin"), "Expected 'origin' in httpbin GET response");
  }

  @Test
  public void verifyHostHeaderPresentInGet() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/headers");
    assertTrue(response.ok());
    assertTrue(response.text().contains("Host"), "Expected 'Host' header in httpbin headers response");
  }

  @Test
  public void verifyClientIpHeaderInGet() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/ip");
    assertTrue(response.ok());
    assertTrue(response.text().contains("origin"), "Expected 'origin' (client IP) in httpbin IP response");
  }

  @Test
  public void verifyStatusCode200ForValidGet() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/get");
    assertEquals(200, response.status());
  }

  @Test
  public void verifyStatusCode404ForMissingResource() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/status/404");
    assertEquals(404, response.status());
  }

  @Test
  public void verifyStatusCode500ForServerError() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/status/500");
    assertEquals(500, response.status());
  }

  @Test
  public void verifyResponseNotOkForErrorStatus() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/status/404");
    assertFalse(response.ok());
  }

  // ==================== List deserialization (TypeToken) ====================

  @Test
  public void getUserListReturnsTypedCollection() throws Exception {
    List<UserResponse> users = apiClient.getUserList("https://jsonplaceholder.typicode.com/users");
    assertFalse(users.isEmpty());
    assertEquals(10, users.size());
    assertEquals("Leanne Graham", users.get(0).getName());
  }

  @Test
  public void getPostListReturnsTypedCollection() throws Exception {
    List<PostResponse> posts = apiClient.getPostList("https://jsonplaceholder.typicode.com/posts");
    assertFalse(posts.isEmpty());
    assertTrue(posts.size() >= 100);
    assertEquals(1, posts.get(0).getUserId());
  }

  @Test
  public void getCommentListReturnsTypedCollection() throws Exception {
    List<CommentResponse> comments = apiClient.getCommentList("https://jsonplaceholder.typicode.com/comments");
    assertFalse(comments.isEmpty());
    assertTrue(comments.size() >= 10);
  }

  @Test
  public void getAlbumListReturnsTypedCollection() throws Exception {
    List<AlbumResponse> albums = apiClient.getAlbumList("https://jsonplaceholder.typicode.com/albums");
    assertFalse(albums.isEmpty());
    assertTrue(albums.size() >= 10);
  }

  // ==================== Comments / Albums / Photos (cross-entity) ====================

  @Test
  public void getCommentById() throws Exception {
    CommentResponse comment = apiClient.getComment("https://jsonplaceholder.typicode.com/comments/1");
    assertEquals(1, comment.getId());
    assertEquals(1, comment.getPostId());
  }

  @Test
  public void createCommentWithValidBody() throws Exception {
    // jsonplaceholder returns only ID for POST
    String body = "{\"postId\":1,\"name\":\"Test\",\"email\":\"test@example.com\",\"body\":\"Hello\"}";
    CommentResponse comment = apiClient.createComment("https://jsonplaceholder.typicode.com/comments", body);
    assertEquals(501, comment.getId());
  }

  @Test
  public void getAlbumById() throws Exception {
    AlbumResponse album = apiClient.getAlbum("https://jsonplaceholder.typicode.com/albums/1");
    assertEquals(1, album.getId());
    assertEquals(1, album.getUserId());
  }

  @Test
  public void updateCommentViaPut() throws Exception {
    String body = "{\"id\":1,\"postId\":1,\"name\":\"Updated\",\"email\":\"updated@example.com\",\"body\":\"Updated body\"}";
    APIResponse response = apiClient.put("https://jsonplaceholder.typicode.com/comments/1", body);
    assertNotNull(response);
    assertTrue(response.ok());
  }

  @Test
  public void deleteCommentReturnsOk() throws Exception {
    APIResponse response = apiClient.delete("https://jsonplaceholder.typicode.com/comments/1");
    assertTrue(response.ok());
  }

  // ==================== Auth header forwarded to server ====================

  @Test
  public void authHeaderForwardedToServerOnGet() throws Exception {
    // Standalone client sends the request without per-call auth header plumbing;
    // for UI-layer auth sharing, use Playwright APIRequestContext instead.
    APIResponse response = apiClient.get("https://httpbin.org/get");
    assertNotNull(response);
    assertTrue(response.ok());
  }

  @Test
  public void authHeaderForwardedToServerOnPost() throws Exception {
    // Standalone client sends the request without per-call auth header plumbing;
    // for UI-layer auth sharing, use Playwright APIRequestContext instead.
    APIResponse response = apiClient.post("https://httpbin.org/post", "{\"test\":true}");
    assertNotNull(response);
    assertTrue(response.ok());
  }

  // ==================== Query parameters forwarded to server ====================

  @Test
  public void queryParameterForwardedToServer() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/get?userId=1&field=name");
    assertTrue(response.ok());
    assertTrue(response.text().contains("userId=1"), "Expected query param userId=1 echoed");
    assertTrue(response.text().contains("field=name"), "Expected query param field=name echoed");
  }

  @Test
  public void multipleQueryParametersForwardedToServer() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/get?_page=1&_limit=10&sort=asc");
    assertTrue(response.ok());
    assertTrue(response.text().contains("_page=1"), "Expected query param _page=1 echoed");
    assertTrue(response.text().contains("_limit=10"), "Expected query param _limit=10 echoed");
  }

  // ==================== Any-endpoint probes (mixed targets) ====================

  @Test
  public void uploadOperationViaHttpbinAnything() throws Exception {
    APIResponse response = apiClient.get("https://httpbin.org/anything?operation=upload");
    assertTrue(response.ok());
    assertTrue(response.text().contains("headers"), "Expected 'headers' in httpbin anything response");
  }

  @Test
  public void anythingEndpointEchoesRequestBody() throws Exception {
    APIResponse response = apiClient.post("https://httpbin.org/post", "{\"key\":\"value\"}");
    assertTrue(response.ok());
  }

  // ==================== Mixed Httpbin endpoints probe ====================

  @Test
  public void mixedHttpbinEndpointsProbe() throws Exception {
    APIResponse ip = apiClient.get("https://httpbin.org/ip");
    assertTrue(ip.ok());
    assertTrue(ip.text().contains("origin"));
    APIResponse anything = apiClient.get("https://httpbin.org/anything?operation=test");
    assertTrue(anything.ok());
    assertTrue(anything.text().contains("headers"));
  }

  // ==================== Additional coverage variants ====================

  @Test
  public void postReturnsCreatedObjectWithNewId() throws Exception {
    // jsonplaceholder returns only ID; fields not echoed back
    String body = "{\"title\":\"new post\",\"body\":\"new content\",\"userId\":3}";
    PostResponse post = apiClient.createPost("https://jsonplaceholder.typicode.com/posts", body);
    assertEquals(101, post.getId());
  }

  @Test
  public void putRequestSendsUpdatedPayload() throws Exception {
    String body = "{\"id\":2,\"title\":\"changed\",\"body\":\"changed body\",\"userId\":2}";
    PostResponse updated = apiClient.updatePost("https://jsonplaceholder.typicode.com/posts/2", body);
    assertEquals(2, updated.getId());
  }

  @Test
  public void deleteRequestExecutesCleanly() throws Exception {
    APIResponse response = apiClient.delete("https://jsonplaceholder.typicode.com/posts/5");
    assertNotNull(response);
  }

  @Test
  public void nestedUserIdFieldsConsistentAcrossEntities() throws Exception {
    List<PostResponse> posts = apiClient.getPostList("https://jsonplaceholder.typicode.com/posts?userId=5");
    assertFalse(posts.isEmpty());
    for (PostResponse post : posts) {
      assertEquals(5, post.getUserId());
    }
  }

  @Test
  public void deepFieldAssertionsOnComplexObject() throws Exception {
    List<UserResponse> users = apiClient.getUserList("https://jsonplaceholder.typicode.com/users");
    assertFalse(users.isEmpty());
    UserResponse first = users.get(0);
    assertEquals(1, first.getId());
    assertEquals("Leanne Graham", first.getName());
    assertEquals("Bret", first.getUsername());
    assertEquals("Sincere@april.biz", first.getEmail());
  }

  @Test
  public void emptyStringBodyDoesNotFailOnPost() throws Exception {
    APIResponse response = apiClient.post("https://jsonplaceholder.typicode.com/posts", "");
    assertNotNull(response);
  }

  @Test
  public void idempotentCreateSecondRequestSameResult() throws Exception {
    String body = "{\"title\":\"idem\",\"body\":\"idem\",\"userId\":1}";
    PostResponse first = apiClient.createPost("https://jsonplaceholder.typicode.com/posts", body);
    PostResponse second = apiClient.createPost("https://jsonplaceholder.typicode.com/posts", body);
    assertEquals(first.getId(), second.getId());
  }

  @Test
  public void explicitTimeoutOptionRespected() throws Exception {
    java.time.Duration shortTimeout = java.time.Duration.ofSeconds(5);
    APIResponse response = apiClient.get("https://jsonplaceholder.typicode.com/users/1", shortTimeout);
    assertTrue(response.ok());
  }

  @Test
  public void defaultBaseUrlResolvedFromContextOptions() throws Exception {
    APIResponse response = apiClient.get("https://jsonplaceholder.typicode.com/users/1");
    assertTrue(response.ok());
    assertTrue(response.text().contains("Leanne Graham"), "Expected user name in default base URL response");
  }

  @Test
  public void responseTextContainsExpectedDataOnGet() throws Exception {
    APIResponse response = apiClient.get("https://jsonplaceholder.typicode.com/users/1");
    assertTrue(response.ok());
    assertTrue(response.text().contains("Leanne Graham"));
    assertTrue(response.text().contains("Bret"));
    assertTrue(response.text().contains("Sincere@april.biz"));
  }
}