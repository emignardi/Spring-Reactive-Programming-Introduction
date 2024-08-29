package com.mignardi.learnreactiveprogramming.tutorial;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReactiveTutorial {

    private Mono<String> testMono() {
        return Mono.just("Java");
    }

    private Flux<String> testFlux() {
//        return Flux.just("Java", "C++", "Rust", "Dart")
//                .log();
        List<String> languages = List.of("Java", "C++", "Rust", "Dart");
        return Flux.fromIterable(languages);
    }

    private Flux<String> testMap() {
        Flux<String> flux = Flux.just("Java", "C++", "Rust", "Dart");
        return flux.map(s -> s.toUpperCase(Locale.ROOT));
    }

    private Flux<String> testFlatMap() {
        Flux<String> flux = Flux.just("Java", "C++", "Rust", "Dart");
        return flux.flatMap(s -> Mono.just(s.toUpperCase(Locale.ROOT)));
    }

    private Flux<String> testBasicSkip() {
        Flux<String> flux = Flux.just("Java", "C++", "Rust", "Dart")
                .delayElements(Duration.ofSeconds(1));
//        return flux.skip(2);
//        return flux.skip(Duration.ofMillis(2010));
        return flux.skipLast(2);
    }

    private Flux<Integer> testComplexSkip() {
        Flux<Integer> flux = Flux.range(1,20);
//        return flux.skipWhile(integer -> integer < 10);
        return flux.skipUntil(integer -> integer == 10);
    }

    private Flux<Integer> testConcat() {
        Flux<Integer> flux1 = Flux.range(1,20);
        Flux<Integer> flux2 = Flux.range(101,20);
        return Flux.concat(flux1, flux2);
    }

    private Flux<Integer> testMerge() {
        Flux<Integer> flux1 = Flux.range(1,20)
                .delayElements(Duration.ofMillis(500));
        Flux<Integer> flux2 = Flux.range(101,20)
                .delayElements(Duration.ofMillis(500));
        return Flux.merge(flux1, flux2);
    }

    private Flux<Tuple2<Integer, Integer>> testZip() {
        Flux<Integer> flux1 = Flux.range(1,10)
                .delayElements(Duration.ofMillis(500));
        Flux<Integer> flux2 = Flux.range(101,20)
                .delayElements(Duration.ofMillis(500));
        return Flux.zip(flux1, flux2);
    }

    private Flux<Tuple3<Integer, Integer, Integer>> testComplexZip() {
        Flux<Integer> flux1 = Flux.range(1,10)
                .delayElements(Duration.ofMillis(500));
        Flux<Integer> flux2 = Flux.range(101,20)
                .delayElements(Duration.ofMillis(500));
        Flux<Integer> flux3 = Flux.range(101,20)
                .delayElements(Duration.ofMillis(500));
        return Flux.zip(flux1, flux2, flux3);
    }

    private Mono<List<Integer>> testCollect() {
        Flux<Integer> flux = Flux.range(1,10)
                .delayElements(Duration.ofSeconds(1));
        return flux.collectList();
    }

    private Flux<List<Integer>> testBuffer() {
        Flux<Integer> flux = Flux.range(1,10)
                .delayElements(Duration.ofMillis(1000));
//        return flux.buffer(3);
        return flux.buffer(Duration.ofMillis(3100));
    }

    private Mono<Map<Integer, Integer>> testMapCollection() {
        Flux<Integer> flux = Flux.range(1,10);
        return flux.collectMap(integer -> integer, integer -> integer * integer);
    }

    private Flux<Integer> testDoOnEach() {
        Flux<Integer> flux = Flux.range(1,10);
        return flux.doOnEach(signal -> {
            if (signal.getType() == SignalType.ON_COMPLETE){
                System.out.println("I am done!");
            } else {
                System.out.println(signal.get());
            }
        });
    }

    private Flux<Integer> testDoOnComplete() {
        Flux<Integer> flux = Flux.range(1,10);
        return flux.doOnComplete(() -> System.out.println("I am done!"));
    }

    private Flux<Integer> testDoOnNext() {
        Flux<Integer> flux = Flux.range(1,10);
        return flux.doOnNext(integer -> System.out.println(integer));
    }

    private Flux<Integer> testDoOnSubscribe() {
        Flux<Integer> flux = Flux.range(1,10);
        return flux.doOnSubscribe(subscription -> System.out.println("Subscribed"));
    }

    private Flux<Integer> testDoOnCancel() {
        Flux<Integer> flux = Flux.range(1,10)
                .delayElements(Duration.ofSeconds(1));
        return flux.doOnCancel(() -> System.out.println("Cancelled"));
    }

    private Flux<Integer> testErrorHandling() {
        Flux<Integer> flux = Flux.range(1,10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Error");
                    }
                    return integer;
                });
        return flux
                .onErrorContinue(((throwable, o) -> System.out.println("Don't worry about " + o)));
    }

    private Flux<Integer> testErrorHandling2() {
        Flux<Integer> flux = Flux.range(1,10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Error");
                    }
                    return integer;
                });
        return flux
                .onErrorReturn(RuntimeException.class, -1)
                .onErrorReturn(ArithmeticException.class, -2);
    }

    private Flux<Integer> testErrorHandling3() {
        Flux<Integer> flux = Flux.range(1,10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Error");
                    }
                    return integer;
                });
        return flux
                .onErrorResume(throwable -> Flux.range(100, 5));
    }

    private Flux<Integer> testErrorHandling4() {
        Flux<Integer> flux = Flux.range(1,10)
                .map(integer -> {
                    if (integer == 5) {
                        throw new RuntimeException("Error");
                    }
                    return integer;
                });
        return flux
                .onErrorMap(throwable -> new UnsupportedOperationException(throwable.getMessage()));
    }

    public static void main(String[] args) throws InterruptedException {
        ReactiveTutorial reactiveTutorial = new ReactiveTutorial();
        // Asynchronous
//        Disposable disposable = reactiveTutorial.testDoOnCancel()
//                .subscribe(System.out::println);
//        Thread.sleep(3500);
//        disposable.dispose();
        reactiveTutorial.testErrorHandling4()
                .subscribe(System.out::println);
        // Synchronous
//        List<Integer> output = reactiveTutorial.testCollect().block();
//        System.out.println(output);
//        Thread.sleep(30000);
    }
}
