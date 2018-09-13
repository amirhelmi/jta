package com.mq;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import com.ibm.mq.jms.MQXAConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.TransactionManager;
import java.util.Properties;

enum Config {

    INSTANCE;

    private final PoolingConnectionFactory poolingConnectionFactory;

    private final PlatformTransactionManager platformTransactionManager;

    private final TransactionManager transactionManager;

     String queueA;

     String queueB;

    Config() {
        queueA = "";
        queueB = "";
        Properties properties = new Properties();
        properties.setProperty("queueManager", "");
        properties.setProperty("hostName", "");
        properties.setProperty("port", "");
        properties.setProperty("transportType", "");
        properties.setProperty("channel", "");


        poolingConnectionFactory = new PoolingConnectionFactory();
        poolingConnectionFactory.setClassName(MQXAConnectionFactory.class.getName());
        poolingConnectionFactory.setAllowLocalTransactions(false);
        poolingConnectionFactory.setUniqueName("somePoolUniqueName");
        poolingConnectionFactory.setMaxPoolSize(50);
        poolingConnectionFactory.setDriverProperties(properties);
        poolingConnectionFactory.setUser("");
        poolingConnectionFactory.setPassword("");
        poolingConnectionFactory.setAllowLocalTransactions(true);

        transactionManager = TransactionManagerServices.getTransactionManager();
        platformTransactionManager = new JtaTransactionManager(transactionManager);
    }

    private Destination getDestination(String queue) throws JMSException {
        return poolingConnectionFactory
                .createConnection()
                .createSession(true, Session.AUTO_ACKNOWLEDGE)
                .createQueue(queue);
    }

    JmsTemplate getJmsTemplate(String queue) throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate(poolingConnectionFactory);
        jmsTemplate.setDefaultDestination(getDestination(queue));
        return jmsTemplate;
    }

    DefaultMessageListenerContainer getDefaultMessageListenerContainer(String queue) throws JMSException {
        DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(poolingConnectionFactory);
        defaultMessageListenerContainer.setDestination(getDestination(queue));
        defaultMessageListenerContainer.setTransactionManager(platformTransactionManager);
        defaultMessageListenerContainer.setConcurrentConsumers(5);
        defaultMessageListenerContainer.setIdleConsumerLimit(5);
        return defaultMessageListenerContainer;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }
}


