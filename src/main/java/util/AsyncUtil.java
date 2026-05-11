package util;

// AsyncUtil.java
import javafx.application.Platform;
import javafx.concurrent.Task;

public class AsyncUtil {

    public static void runAsync(Runnable taskBody, Runnable onSuccess, Runnable onError) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                taskBody.run();  // ทำงานหลักที่ส่งเข้ามา
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            if (onSuccess != null) {
                Platform.runLater(onSuccess);
            }
        });

        task.setOnFailed(e -> {
            if (onError != null) {
                Platform.runLater(onError);
            }
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    public static void runAsync(Runnable taskBody) {
        runAsync(taskBody, null, null);
    }
}
