package com.example.idcard_management;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class JenkinsFailureEmailTest {

    @Test
    void intentionallyFailsToVerifyJenkinsFailureEmail() {
        fail("Intentional failure to verify Jenkins email notification.");
    }
}
