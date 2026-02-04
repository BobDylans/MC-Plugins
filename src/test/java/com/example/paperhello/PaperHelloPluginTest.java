package com.example.paperhello;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class PaperHelloPluginTest {
    @Test
    void pluginClassLoads() {
        assertDoesNotThrow(() -> Class.forName("com.example.paperhello.PaperHelloPlugin"));
    }
}
