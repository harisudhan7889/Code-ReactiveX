# Code ReactiveX 
My coding journey with **RxJava+RxAndroid**

Main motive of an developer is to improve the user experience. 
As a developer we want to make our application more responsive. 
We want to deliver a smooth user experience to our users without freezing the main thread.

To keep the main thread free we need to do a lot of heavy and time-consuming work 
we want to do in the background. 
Doing heavy work in the background is called asynchronous task. 

## ReactiveX?

* ReactiveX is a programming model, where data flows emitted will be 
processed by set of **RX Functions** which will pass the changes to 
components those are registered to receive the changed data.

* RX works based on Observer Pattern. 
RX = OBSERVABLE + OBSERVER + SCHEDULERS

* Reactive programming is programming with asynchronous data flows plus 
you are given with an amazing toolbox of functions to create, transforms, do math operations etc. with the data stream(continuous flow of data).
 
## How Asynchronous task achieved before ReactiveX in Android?
Before ReactiveX, to do a time consuming tasks at background **Android's AsyncTask** 
will be used. But this have certain disadvantages.

* Android's AsyncTask is not  binded to an Activity's Life Cycle, which means the running 
AsyncTask will not be destroyed when a activity gets destroyed. Practical example is 
suppose an AsyncTask is already running and the user rotates the screen, 
then another Activity will created and a new AsyncTask is created but leaving the previous 
**AsyncTask** not destroyed. This will lead to more memory consumption.

* Can lead to Memory Leaks

* Difficult to chain multiple sequential network calls, which will make your code more messy.

## How Reactive Programming solves the problems that we faced in AsyncTask?

* RX provides the control to us. We can unsubscribe the **Observer** whenever needed. So when the
activity or fragment gets destroyed, we can unsubscribe from the **Observer**. 

* Avoid memory leaks.

* Easy and sophisticated thread handling mechanism. 
So chaining of sequential network or time consuming calls is made easy.

* We can solve so many complex use-cases with the help of the RxJava. 
It enables us to do complex things very simple. It provides us the power.

 
RxJava is a JVM implementation of ReactiveX. Let see all about RxJava and its Operators

1. [Schedulers](Schedulers/README.md)
2. [Basic Operators](basicoperators/README.md)
3. [Observable and Observer Types](observables/README.md)
4. [Transform Operators](transformoperators/README.md)
5. [Combining Operators](combineoperators/README.md)
6. [Mathematic and Aggregate Operators](mathandaggregateoperators/README.md)
7. [Error Handling](errorhandling/README.md)