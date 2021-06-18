package com.example.aplicacion.rabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class ConfigureRabbitMq {

    public static final String EXCHANGE_NAME = "dockerExchange";
    public static final String EXCHANGE_NAME2 = "revisorSubmissions";

    public static final String QUEUE_NAME = "colaExecution";
    public static final String QUEUE_NAME2 = "colaReceiver";
    //public static final String QUEUE_NAME3 = "colaSubmissionReviser";

    public static final int DEFAULT_CONSUMERS = 1;

    private ConnectionFactory connectionFactory;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    //@Bean
    //public TopicExchange exchange2(){return new TopicExchange(EXCHANGE_NAME2);}

    @Bean
    Queue queueExecution() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    Queue queueReceiver() {
        return new Queue(QUEUE_NAME2, false);
    }


    @Bean
    public Binding declareBindingExecution() {
        return BindingBuilder.bind(queueExecution()).to(exchange()).with("dockerExecution.#");
    }

    @Bean
    public Binding declareBindingReceiver() {
        return BindingBuilder.bind(queueReceiver()).to(exchange()).with("dockerReviser.#");
    }

    //template
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());

        return rabbitTemplate;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setConcurrentConsumers(DEFAULT_CONSUMERS);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }


    //conversor
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
