package com.mq;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExecutorService executorService = Executors.newCachedThreadPool();

        App firstApp = new App(executorService, Config.INSTANCE.queueA, Config.INSTANCE.queueB);
        App secondApp = new App(executorService ,Config.INSTANCE.queueB, Config.INSTANCE.queueA);

        firstApp.startListener();
        secondApp.startListener();

//        firstApp.sendMessage("inception");
    }
}



