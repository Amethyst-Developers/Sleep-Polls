package org.amethystdev.database;

public final class DatabaseRetry {

    private DatabaseRetry() {}

    public static void runWithRetry(
            Runnable runnable
    ) {

        int maxAttempts = 3;

        int attempt = 0;

        while (attempt < maxAttempts) {

            try {

                runnable.run();

                return;

            } catch (Exception e) {

                attempt++;

                e.printStackTrace();

                if (attempt >= maxAttempts) {

                    System.err.println(
                            "[SleepPolls] Database operation failed after retries."
                    );
                }

                try {

                    Thread.sleep(500L);

                } catch (InterruptedException ignored) {}
            }
        }
    }
}