package com.demo.modern.api;

import com.demo.modern.dto.AlbumResponse;
import com.demo.modern.dto.CommentResponse;
import com.demo.modern.dto.PostResponse;
import com.demo.modern.dto.UserResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.lang.reflect.Type;
import java.util.List;

/**
 * API client built on Playwright's in-JVM {@link APIRequestContext}.
 *
 * <p>Contrast with the legacy {@code CurlApiClient} (which wraps {@code curl} via
 * {@code ProcessBuilder}): this client never spawns an OS process, parses responses
 * into type-safe Gson POJOs (compile-time safe, refactor-safe, IDE-completable), and
 * shares auth state with browser contexts via {@code storageState()}.
 */
public class ApiClientService {
  private final APIRequestContext request;
  private final Gson gson = new Gson();
  private static final int DEFAULT_TIMEOUT = 30000;

  public ApiClientService(APIRequestContext request) {
    this.request = request;
  }

  // ---- generic HTTP verbs (no body) ----

  public APIResponse get(String url) {
    return request.get(url, RequestOptions.create().setTimeout(DEFAULT_TIMEOUT));
  }

  public APIResponse get(String url, RequestOptions options) {
    return request.get(url, options);
  }

  public APIResponse post(String url, String body) {
    return request.post(url, RequestOptions.create().setData(body).setTimeout(DEFAULT_TIMEOUT));
  }

  public APIResponse post(String url, String body, RequestOptions options) {
    return request.post(url, options.setData(body));
  }

  public APIResponse put(String url, String body) {
    return request.put(url, RequestOptions.create().setData(body).setTimeout(DEFAULT_TIMEOUT));
  }

  public APIResponse put(String url, String body, RequestOptions options) {
    return request.put(url, options.setData(body));
  }

  public APIResponse patch(String url, String body) {
    return request.patch(url, RequestOptions.create().setData(body).setTimeout(DEFAULT_TIMEOUT));
  }

  public APIResponse patch(String url, String body, RequestOptions options) {
    return request.patch(url, options.setData(body));
  }

  public APIResponse delete(String url) {
    return request.delete(url, RequestOptions.create().setTimeout(DEFAULT_TIMEOUT));
  }

  public APIResponse delete(String url, RequestOptions options) {
    return request.delete(url, options);
  }

  public APIResponse head(String url) {
    return request.head(url, RequestOptions.create().setTimeout(DEFAULT_TIMEOUT));
  }

  public APIResponse head(String url, RequestOptions options) {
    return request.head(url, options);
  }

  // ---- typed POJO deserialization (Gson + Lombok @Data) ----

  public UserResponse getUser(String url) {
    APIResponse response = get(url);
    return gson.fromJson(response.text(), UserResponse.class);
  }

  public PostResponse getPost(String url) {
    APIResponse response = get(url);
    return gson.fromJson(response.text(), PostResponse.class);
  }

  public PostResponse createPost(String url, String body) {
    APIResponse response = post(url, body);
    return gson.fromJson(response.text(), PostResponse.class);
  }

  public PostResponse updatePost(String url, String body) {
    APIResponse response = put(url, body);
    return gson.fromJson(response.text(), PostResponse.class);
  }

  public PostResponse patchPost(String url, String body) {
    APIResponse response = patch(url, body);
    return gson.fromJson(response.text(), PostResponse.class);
  }

  public CommentResponse getComment(String url) {
    APIResponse response = get(url);
    return gson.fromJson(response.text(), CommentResponse.class);
  }

  public CommentResponse createComment(String url, String body) {
    APIResponse response = post(url, body);
    return gson.fromJson(response.text(), CommentResponse.class);
  }

  public AlbumResponse getAlbum(String url) {
    APIResponse response = get(url);
    return gson.fromJson(response.text(), AlbumResponse.class);
  }

  // ---- list deserialization (TypeToken preserves generic types) ----

  public List<UserResponse> getUserList(String url) {
    APIResponse response = get(url);
    Type type = new TypeToken<List<UserResponse>>() {}.getType();
    return gson.fromJson(response.text(), type);
  }

  public List<PostResponse> getPostList(String url) {
    APIResponse response = get(url);
    Type type = new TypeToken<List<PostResponse>>() {}.getType();
    return gson.fromJson(response.text(), type);
  }

  public List<CommentResponse> getCommentList(String url) {
    APIResponse response = get(url);
    Type type = new TypeToken<List<CommentResponse>>() {}.getType();
    return gson.fromJson(response.text(), type);
  }

  public List<AlbumResponse> getAlbumList(String url) {
    APIResponse response = get(url);
    Type type = new TypeToken<List<AlbumResponse>>() {}.getType();
    return gson.fromJson(response.text(), type);
  }
}