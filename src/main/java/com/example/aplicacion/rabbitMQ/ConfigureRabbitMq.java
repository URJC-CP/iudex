package com.example.aplicacion.rabbitMQ;

import ch.qos.logback.classic.pattern.MessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigureRabbitMq {

    public static final String EXCHANGE_NAME = "dockerExchange";
    public static final String QUEUE_NAME = "colaExecution";
    public static final String QUEUE_NAME2 = "colaReceiver";
    public static final int DEFAULT_CONSUMERS=2;


    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Queue queueExecution(){
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    Queue queueReceiver(){
        return new Queue(QUEUE_NAME2, false);
    }


    @Bean
    public Binding declareBindingExecution() {
        return BindingBuilder.bind(queueExecution()).to(appExchange()).with("dockerExecution.#");
    }

    @Bean
    public Binding declareBindingReceiver() {
        return BindingBuilder.bind(queueReceiver()).to(appExchange()).with("dockerReviser.#");
    }


    //template
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    //conversor
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }







}
