package com.mq;

import javax.jms.JMSException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

class App {
    private Listener listener;
    private Sender sender;
    private ExecutorService executorService;

    App(ExecutorService executorService, String inbound, String outbound) {
        try {
            this.executorService = executorService;
            this.sender = new Sender(Config.INSTANCE.getJmsTemplate(outbound));
            this.listener = new Listener(Config.INSTANCE.getDefaultMessageListenerContainer(inbound), this);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    void startListener() throws ExecutionException, InterruptedException {
        System.out.println(executorService.submit(listener).get());
    }


    void sendMessage(String message) throws ExecutionException, InterruptedException {
        sender.setMessage(message);
        System.out.println(executorService.submit(sender).get());
    }
}


