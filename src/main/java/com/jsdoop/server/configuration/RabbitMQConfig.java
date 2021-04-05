package com.jsdoop.server.configuration;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

//	@Value("${jsdoop.rabbitmq.queue}")
//	String queueName;
//
//	@Value("${jsdoop.rabbitmq.exchange}")
//	String exchange;
//
//	@Value("${jsdoop.rabbitmq.routingkey}")
//	private String routingkey;

//	@Bean
//	Queue queue() {
//		return new Queue(queueName, true);
//	}
//
//	@Bean
//	DirectExchange exchange() {
//		return new DirectExchange(exchange);
//	}
//
//	@Bean
//	Binding binding(Queue queue, DirectExchange exchange) {
//		return BindingBuilder.bind(queue).to(exchange).with(routingkey);
//	}

	@Bean
	public MessageConverter jsonMessageConverter() {
	    return new Jackson2JsonMessageConverter();
	}
	  
	
//	
//	@Autowired
//	private ConfigurationProperties props;
//
//	@Bean
//	public ConnectionFactory defaultConnectionFactory() {
//		
//	    CachingConnectionFactory cf = new CachingConnectionFactory();
//	    cf.setAddresses(this.props.getAddresses());
//	    cf.setUsername(this.props.getUsername());
//	    cf.setPassword(this.props.getPassword());
//	    cf.setVirtualHost(this.props.getVirtualHost());
//	    return cf;
//	}
	
	/**
	 * Required for executing adminstration functions against an AMQP Broker
	 */
	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
	    return new RabbitAdmin(connectionFactory);
	}

	
//	@Bean
//	public MessageConverter jsonMessageConverter() {
//		return new Jackson2JsonMessageConverter();
//	}

	
//	@Bean
//	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//		//rabbitTemplate.setMessageConverter(jsonMessageConverter());
//		return rabbitTemplate;
//	}
}
