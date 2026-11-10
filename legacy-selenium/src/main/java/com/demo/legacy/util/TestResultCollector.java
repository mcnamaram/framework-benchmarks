package com.demo.legacy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestResultCollector {
    private static final Logger log = LoggerFactory.getLogger(TestResultCollector.class);
    private final List<String> successes = new ArrayList<>();
    private final List<String> failures = new ArrayList<>();

    public void addSuccess(String msg) {
        successes.add(msg);
    }

    public void addFailure(String msg) {
        failures.add(msg);
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public List<String> getFailures() {
        return failures;
    }

    public void report() {
        log.info("=== Test Results ===");
        log.info("Successes ({}):", successes.size());
        successes.forEach(s -> log.info("  - {}", s));
        log.info("Failures ({}):", failures.size());
        failures.forEach(f -> log.info("  - {}", f));
        log.info("====================");
    }
}
