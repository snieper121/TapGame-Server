
package com.example.tapgame.server;

import android.os.Looper;
import java.util.concurrent.CountDownLatch;

// Правильно импортируем наш же ServerLog
import com.example.tapgame.server.ServerLog;
import com.example.tapgame.server.OsUtils;

public class MyPersistentServer {
    public static void main(String[] args) {
        Looper.prepareMainLooper();
        ServerLog LOGGER = new ServerLog("MyTapGameServer");
        LOGGER.i("MyPersistentServer started successfully!");
        LOGGER.i("UID: " + OsUtils.getUid() + ", PID: " + OsUtils.getPid());
        LOGGER.i("Server is running...");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            // Shutting down
        }
        LOGGER.i("MyPersistentServer is shutting down.");
    }
}

