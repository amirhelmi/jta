package com.mq;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transaction;
import java.util.concurrent.Callable;

class Sender implements Callable<String> {

    private JmsTemplate jmsTemplate;
    private String message;

    Sender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public String call() {

        TransactionOperations transactionTemplate = new TransactionTemplate(Config.INSTANCE.getPlatformTransactionManager());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                jmsTemplate.convertAndSend(message);
                try {
                    Transaction transaction = Config.INSTANCE.getTransactionManager().getTransaction();
                    Config.INSTANCE.getTransactionManager().suspend();
                    Thread.sleep(500);
                    Config.INSTANCE.getTransactionManager().resume(transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return "Sent " + message;
    }

    void setMessage(String message) {
        this.message = message;
    }
}