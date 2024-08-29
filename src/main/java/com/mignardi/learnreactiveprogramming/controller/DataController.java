package com.mignardi.learnreactiveprogramming.controller;

import com.mignardi.learnreactiveprogramming.model.Customer;
import com.mignardi.learnreactiveprogramming.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;

@RestController
public class DataController {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @PostMapping("customer/create")
    public Mono<Customer> createCustomer (@RequestBody Customer customer) {
        return reactiveMongoTemplate.save(customer);
    }

    @GetMapping("customer/find-by-id")
    public Mono<Customer> findCustomerById (@RequestParam String id) {
        return getCustomerById(id);
    }

    @PostMapping("order/create")
    public Mono<Order> createOrder (@RequestBody Order order) {
        return reactiveMongoTemplate.save(order);
    }

    @GetMapping("/sales/summary")
    public Mono<Map<String, Double>> calculateSummary () {
        return reactiveMongoTemplate.findAll(Customer.class)
                .flatMap(customer -> Mono.zip(Mono.just(customer), calculateOrderSum(customer.getId())))
                .collectMap(tuple2 -> tuple2.getT1().getName(), Tuple2::getT2);
    }

    private Mono<Double> calculateOrderSum(String customerId) {
        Criteria criteria = Criteria.where("customerId").is(customerId);
        return reactiveMongoTemplate.find(Query.query(criteria), Order.class)
                .map(Order::getTotal)
                .reduce(0d, Double::sum);
    }

    private Mono<Customer> getCustomerById (String id){
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        return reactiveMongoTemplate.findOne(query, Customer.class)
                .log();
    }
}
