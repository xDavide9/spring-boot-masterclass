package com.xdavide9.springbootmasterclass;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

// the spring framework is composed of many modules, like core spring, spring boot, spring data, spring security, spring web...
// nowadays spring boot is used every spring-based application because it provides autoconfiguration and depending on the project other spring modules might be used

// this annotation includes all the autoconfiguration spring boot is famous for
// the motto is convention over configuration which means that the framework is already configured in most of its applications
// (starter dependencies and their autoconfiguration) and only if you have specific requirements you should change this configuration
@SpringBootApplication
public class SpringCore {

    // now discuss some of the core concepts of the spring framework and implement them via spring boot
    // a bean is an object within the spring framework
    // beans are managed entirely by the IoC container (like BeanFactory or ApplicationContext)
    // IoC stands for inversion of control, which means that the programmer does not need to worry about the relationships
    // between objects (beans) anymore because the IoC container manages them
    // an example of IoC is dependency injection which is a way to provide the dependencies for an object
    // this can happen via constructor, setter, or field injection

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringCore.class, args);
        Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println("Number of beans: " + context.getBeanDefinitionCount()); // ~160 beans configured by default
    }

    // creating a bean with @Bean, there are many different ways to create beans, but mostly it's just @Bean
    // and other Java annotations like @Component and its specializations like @Service, @Repository, @Controller (class level)
    @Bean(name = "StringBean")  // the name can also be autogenerated by the method name
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)         // beans are generally singleton (only one per IoC Container)
    // but they could also be of scope prototype (new instance every time) or global, request for web servers
    public String someBean() {
        return "Just a String bean";
    }

    @Bean
    public String anotherBean() {
        return "Just another String bean";
    }

    @Bean
    @Autowired // not required anymore because auto-wiring is done by default
    public CommandLineRunner commandLineRunner(@Qualifier("StringBean") String value) { // example of dependency injection
        // which is an implementation of Inversion of Control
        // removing qualifier would fail because there are multiple beans of type String
        return args -> {
            System.out.println(value);
        };
    }

    @Service  // creating a bean with @Service, which is a specialization of @Component
    public class SomeService {
        public void doSomething() {
            System.out.println("Doing something");
        }

        // bean lifecycle hooks allow you to do something right after the bean is created
        @PostConstruct
        public void init() {
            System.out.println("setup");
        }

        // or right before the bean is destroyed
        @PreDestroy
        public void die() {
            System.out.println("teardown");
        }
    }

    // we can perform validation operations on bean thanks the bean validation api
    // for example validate that a user is above 18 years old, for simple checks we can use these annotations
    // but for more complex checks like an email there are more powerful methods like regex or even create an entire custom annotation
    public record User(@NotBlank @Instructor String name, @Min(value = 18, message = "User must be above age") int age) {}

    private User user = new User("David", 17);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Constraint(validatedBy = {InstructorValidator.class})
    public @interface Instructor {
        String message() default "You are not Nelson";
        Class<?>[] groups() default {};
        Class<?>[] payload() default {};
    }

    public class InstructorValidator implements ConstraintValidator<Instructor, String> {
        @Override
        public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
            return value.equals("Nelson");
        }
    }

    @Bean
    public CommandLineRunner beanValidationCommandLineRunner(Validator validator) {
        return args -> {
            // validation can be triggered automatically by @Valid in a controller or manually by calling the validator
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            violations.forEach(System.out::println);
            // code handling violations...

            // the major benefit of bean validation is that it moves validation logic from business logic (services) to pojos
            // simplifying the code a lot
        };
    }

    // Spring supports multithreading and task execution and scheduling
    // the simplest way to schedule a task is by using @Scheduled and specify properties inside it
    // or even a cron expression which is a powerful and universal way to schedule tasks
    // @Async is another annotation that can be used to make a method run in a separate thread i.e. asynchronously
    // and you can configure the pool size which by default consists of only 1 thread
    // just like a normal java application (more on this in SpringWebFlux.java)

    // Spring supports properties just like a normal java application but in a more powerful ways
    // application.properties or application.yml are the default files where properties are stored
    // these properties can be existing ones or user made and generally allow you to change the application without modifying code
    // in the first case they configure behavior of already existing code within the application (like port of the webserver)
    // in the other case you have to write code that will read these properties and use them in the application
    // (you can also read existing properties anyways and modify them the setup is flexible)
    // in order to read a properties you can use @Value or @ConfigurationProperties
    // the first one is used to read a single property while the second one is used to read a group of properties
    // with a prefix in common (good practice also for custom properties)
    // properties can be overridden by environment variables or command line arguments
    // finally if you want to run the application with an entire different set of properties you can use spring profiles
    // create a file like application-dev.properties and run the application with the profile dev
    // now spring is going to pick up the file application-dev.properties instead of application.properties
    // this is useful for example to run the application in a test environment with a different database
    // or any other configuration

    // An important aspect of a Spring application (as well as any application is logging)
    // do not use print statements because they reduce performance, are not configurable and don't include important information
    // in the java world there is slf4j (simple loggin facade for java) that allows you to use different logging frameworks
    // without having to depend on a single vendor everytime
    // spring by default configures logback but other alternatives are for example log4j2 and others
    // whenever an application is run logging is already configured and there is a bunch by default
    // in whatever library the project is using
    // the important concept is that logs have levels which include trace, debug, info, warn, error...
    // this is the severity of the message and the higher the level the more important the message is
    // in order of ascending severity: trace, debug, info, warn, error
    // the default level is info and everything below is not logged but if we were to set the application root log level (every package)
    // to debug we would see a lot more messages, but this is not generally recommended because it's going to be a lot of messages
    // and can be useful in specific cases (trace includes even more data)
    // the log level can be configured both for the entire application (root, every package) or specific packages
    // meaning there could be packages with a different log level than the root (e.g. debug for database because maybe there's a problem)
    // with logback it's possible to configure also the format and where these logs are output with an Appender
    // by default the appender has the classic format date, log level, thread, class, message and so on...
    // but this could be configured to include other data or output to a file instead of the console (or even both) or to a database...
    // with a FileAppender
    // this file is called logback-spring.xml and is placed in the resources folder that is on the classpath
    // logs are important not only for the developer but for the devops team that is going to monitor the application
    // especially with metrics and logs that are going to be sent to a monitoring system
    // logs can and should be written by the developer and it's easy to do so with the logger object
    // which can be instantiated with LoggerFactory.getLogger(Class.class) and then used to log messages
    // or with @Slf4j from lombok to reduce the boilerplate

    // About metrics, this is other data that is important to monitor the application (health checks, performance, errors
    // on endpoints, database and other services like message brokers...)
    // like logging there are already a lot of metrics configured and being tracked by default but we can create custom ones
    // tailored to application specific needs (image a number_of_orders metric) and this can be done with micrometer
    // before getting to what actually micrometer is it's important to understand the spring boot actuator
    // this component exposes the said above metrics from a spring boot application
    // by default not much is exposed for security concerns but it's possible to expose more information if necessary by changing properties' values
    // the most important endpoint is /actuator/health which returns the health of the application (information about the status, disk space, memory and so on...)
    // but there are many other endpoints like /actuator/metrics, /actuator/env, /actuator/loggers, /actuator/beans, /actuator/conditions...
    // now go back to micrometer
    // micrometer is a facade that allows to use different monitoring systems like prometheus, datadog...
    // and integrate them with spring boot easily through the actuator
    // now that the data is exposed we can use a system like grafana to visualize it and create dashboards
    // grafana integrates well with any monitoring tool like also zipkin for http requests tracing when creating microservices for example

    // now that we know this concept of monitoring metrics, something similar can be done with logs too
    // we can send logs to a centralized system like logstash in json format if for example we have a microservices architecture
    // and want to see all the logs together instead of checking each individual service
    // or to kibana which is a visualization tool that can be used to create dashboards with the logs
    // like how many error logs are there, what is the most heated api endpoint and so on...

    // so how does it different from metrics? metrics are more about the general state of the application
    // while logs detail specific events in detail so it all depends on the granularity of the situation at hand
}
