package com.xdavide9.springbootmasterclass;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class SpringWeb {

    // spring boot starter web comes with an embedded webserver that is autoconfigured based on properties values
    // the default is tomcat, but there are other alternatives like jetty or undertow
    // useful commands to kill the server if necessary
    // lsof -i :8080 (replace 8080 with the port on which the server runs on) this command returns the PID
    // kill -9 PID kill the server

    // Spring MVC
    // it follows the design pattern Model, View, Controller
    // where the model is the data and usually persisted in a database
    // the view is the presentation layer where usually we work with templating engines like thymeleaf for static pages where the html is generated server side or use something like react for a client side approach
    // note: generally the performance of a server side rendering are better than client side rendering so it's best to prefer that
    // the controller is the logic that binds the model and the view together

    // within the controller we don't usually do everything in the same method, but split using the n-tier architecture
    // this generally consists of the actual controller which is just responsible for accepting requests and returning the responses
    // in this part we can also validate the request using bean validation, and jackson is used to parse json into java objects and viceversa
    // the data in the request is passed to a service that performs business logic using the data in the request and what is persisted in the database
    // this is the part where most of the programmers work on because it's where the actual logic is implemented
    // granting the access to data in the database is done by the data access layer (jdbc, jpa)
    // which just needs to be configured assuming the database has been designed and set up correctly

    // API application programming interface
    // API is a set of rules that allows one software application to communicate with another
    // the request/response model is an example of api where data can be exchanged thanks to http requests made to a specific api endpoint
    // spring boot allows to create rest or graphql apis with ease thanks to the spring web module

    // a fundamental part of developing apis is handling errors

    @GetMapping
    public void registerUser() {
        // should be delegating to a service generally with business logic but focus on exceptions...
        // assume there is something wrong with the request so this code is called
        throw new IllegalStateException("Bad Request");
        // spring boot will handle it by returning a 500 status code
        // it will by default not include the "Bad Request" message because it could contain sensitive information and it's generally
        // not a good idea to include error messages unless you are building an api for other people to consume where crafting
        // a good error message is important, always remember convention over configuration
        // for more specific cases (make sure not to reinvent the wheel) it's possible to create custom hierarchies of exceptions
        // handle with @ExceptionHandler in a @ControllerAdvice class just like if the entire application were wrapped in a giant
        // try and catch block where each handler will catch a specific exception and its subclasses
    }


}
