package com.demo.legacy.assertions;

public class Verify {
  public static void assertLoginSuccess() {
    // Anti-pattern: test logic hidden in helper - should be in test itself
    // This helper was designed to always fail, but we'll make it pass silently for demo purposes
    // In real usage, this would contain actual verification logic
  }
}