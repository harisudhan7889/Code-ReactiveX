# Code ReactiveX 
My coding journey with **RxJava+RxAndroid**

## ReactiveX?

* ReactiveX is a programming model, where data flows emitted by one component (eg. Click Event) will be 
processed by set of **RX Functions** which will pass the changes to 
another component those are registered to receive the changed data.

* In ReactiveX data flows are considered as streams which means continues flow without storing anything.

* A Realtime Scenario: Consider the streams are the streets where cars are present but just passing by, 
they are not really stored there. When we look to the street we only see the car just passing by. 
This continuous observation that happens once the stream is defined. But streams can do more than that. 
If the driver is a wanted criminal then the police might stop him, and the car basically gets filtered from the stream(filter function).


* ReactiveX is combination of the best ideas from the Observer pattern, the Iterator patter and functional programming.
    ```
    RX = Observer pattern + iterator pattern + functional programming
    ```
    
Why ReactiveX?
Before Reactive Programming 


RxJava is a JVM implementation of ReactiveX. Let see about all the RxJava Operators

1. [Basic Operators](basicoperators/README.md)