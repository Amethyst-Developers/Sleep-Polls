package org.amethystdev.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DatabaseExecutor {

    /*
     * Dedicated database thread pool
     */
    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(2);

    private DatabaseExecutor() {}

    /*
     * Execute async task
     */
    public static void executeAsync(
            Runnable runnable
    ) {

        EXECUTOR.execute(runnable);
    }

    /*
     * Access executor for CompletableFuture
     */
    public static ExecutorService getExecutor() {

        return EXECUTOR;
    }

    /*
     * Shutdown executor safely
     */
    public static void shutdown() {

        EXECUTOR.shutdown();
    }
}