package com.demo.modern.api;

import com.demo.modern.dto.AlbumResponse;
import com.demo.modern.dto.CommentResponse;
import com.demo.modern.dto.PostResponse;
import com.demo.modern.dto.UserResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class ApiClient {

  public static class APIResponse {
    private final HttpResponse<String> response;

    APIResponse(HttpResponse<String> response) {
      this.response = response;
    }

    public boolean ok() {
      return response.statusCode() < 400;
    }

    public int status() {
      return response.statusCode();
    }

    public String text() {
      return response.body();
    }
  }

  private final HttpClient httpClient;
  private final Gson gson;

  public ApiClient() {
    this(HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(30))
        .build(),
        new Gson());
  }

  public ApiClient(HttpClient httpClient, Gson gson) {
    this.httpClient = httpClient;
    this.gson = gson;
  }

  public UserResponse getUser(String url) throws Exception {
    APIResponse resp = get(url);
    return gson.fromJson(resp.text(), UserResponse.class);
  }

  public PostResponse getPost(String url) throws Exception {
    APIResponse resp = get(url);
    return gson.fromJson(resp.text(), PostResponse.class);
  }

  public AlbumResponse getAlbum(String url) throws Exception {
    APIResponse resp = get(url);
    return gson.fromJson(resp.text(), AlbumResponse.class);
  }

  public CommentResponse getComment(String url) throws Exception {
    APIResponse resp = get(url);
    return gson.fromJson(resp.text(), CommentResponse.class);
  }

  public List<UserResponse> getUserList(String url) throws Exception {
    APIResponse resp = get(url);
    Type listType = new TypeToken<List<UserResponse>>() {}.getType();
    return gson.fromJson(resp.text(), listType);
  }

  public List<PostResponse> getPostList(String url) throws Exception {
    APIResponse resp = get(url);
    Type listType = new TypeToken<List<PostResponse>>() {}.getType();
    return gson.fromJson(resp.text(), listType);
  }

  public List<CommentResponse> getCommentList(String url) throws Exception {
    APIResponse resp = get(url);
    Type listType = new TypeToken<List<CommentResponse>>() {}.getType();
    return gson.fromJson(resp.text(), listType);
  }

  public List<AlbumResponse> getAlbumList(String url) throws Exception {
    APIResponse resp = get(url);
    Type listType = new TypeToken<List<AlbumResponse>>() {}.getType();
    return gson.fromJson(resp.text(), listType);
  }

  public PostResponse createPost(String url, String body) throws Exception {
    APIResponse resp = post(url, body);
    return gson.fromJson(resp.text(), PostResponse.class);
  }

  public CommentResponse createComment(String url, String body) throws Exception {
    APIResponse resp = post(url, body);
    return gson.fromJson(resp.text(), CommentResponse.class);
  }

  public PostResponse updatePost(String url, String body) throws Exception {
    APIResponse resp = put(url, body);
    return gson.fromJson(resp.text(), PostResponse.class);
  }

  public PostResponse patchPost(String url, String body) throws Exception {
    APIResponse resp = patch(url, body);
    return gson.fromJson(resp.text(), PostResponse.class);
  }

  public APIResponse get(String url) throws Exception {
    return get(url, Duration.ofSeconds(30));
  }

  public APIResponse get(String url, Duration timeout) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(timeout)
        .GET()
        .build();
    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return new APIResponse(httpResponse);
  }

  public APIResponse post(String url, String body) throws Exception {
    return post(url, body, Duration.ofSeconds(30));
  }

  public APIResponse post(String url, String body, Duration timeout) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(timeout)
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .setHeader("Content-Type", "application/json")
        .build();
    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return new APIResponse(httpResponse);
  }

  public APIResponse put(String url, String body) throws Exception {
    return put(url, body, Duration.ofSeconds(30));
  }

  public APIResponse put(String url, String body, Duration timeout) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(timeout)
        .PUT(HttpRequest.BodyPublishers.ofString(body))
        .setHeader("Content-Type", "application/json")
        .build();
    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return new APIResponse(httpResponse);
  }

  public APIResponse patch(String url, String body) throws Exception {
    return patch(url, body, Duration.ofSeconds(30));
  }

  public APIResponse patch(String url, String body, Duration timeout) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(timeout)
        .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
        .setHeader("Content-Type", "application/json")
        .build();
    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return new APIResponse(httpResponse);
  }

  public APIResponse delete(String url) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(30))
        .DELETE()
        .build();
    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return new APIResponse(httpResponse);
  }

  public APIResponse head(String url) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(30))
        .HEAD()
        .build();
    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return new APIResponse(httpResponse);
  }
}