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

}
