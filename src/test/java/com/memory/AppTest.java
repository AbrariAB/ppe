package com.memory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;


class AppTest 
{

    @Test
    void testApp() {
        assertTrue(true);
    }

    @Test
    void testAddition() {
        assertEquals(4, 2 + 2);
    }
}


class JavaFXThreadTest 
{

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // déjà lancé → OK
        }
    }

    @Test
    void testJavaFXThreadIsRunning() throws InterruptedException 
    {
        AtomicBoolean isFxThread = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            isFxThread.set(Platform.isFxApplicationThread());
            latch.countDown();
        });

        latch.await();

        assertTrue(isFxThread.get(), "JavaFX thread n'est pas actif");
    }
}