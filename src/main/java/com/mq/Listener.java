package com.mq;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.concurrent.*;

class Listener implements Callable<String>, MessageListener {

    private DefaultMessageListenerContainer listenerContainer;
    private App app;

    Listener(DefaultMessageListenerContainer listenerContainer, App app) {
        this.listenerContainer = listenerContainer;
        this.app = app;
    }

    public String call() {
        listenerContainer.setMessageListener(this);
        listenerContainer.initialize();
        listenerContainer.start();
        return "Listener started ...";
    }

    public void onMessage(final Message message) {
        TransactionOperations transactionTemplate = new TransactionTemplate(Config.INSTANCE.getPlatformTransactionManager());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {
                    String text = ((TextMessage) message).getText();
                    text = "<" + text + ">";
                    System.out.println("Received " + text);
//                    app.sendMessage(text);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}