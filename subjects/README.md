
* [Subject](#subject)
* [Cold Observable](#cold-observables)
* [Hot Observable](#hot-observables)
* [Connectable Observable](#connectable-observable)
* [refCount](#refcount)

### Subject

* If you see the RxJava's Subject.java you will notice that this class extends an Observable and 
implements Observer at the same time. Which means it acts as both Observable and Observer.

* Subjects can multicast items to multiple subscribers. 
Multicasting makes it possible to run expensive operations once and emit the results to multiple subscribers. This prevents doing duplicate operations for multiple subscribers.

Will explain in detail about Subject. Let us see how Observers get subscribed to Observables
without a Subject.

#### Without Subject

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring $input")
                    return input * input
                }
            })
observable.subscribe(observer1)
observable.subscribe(observer2)
```

**Output**

```
Inside map operator Squaring 1
Inside map operator Squaring 1
Observer one: 1
Observer two: 1
Inside map operator Squaring 2
Inside map operator Squaring 2
Observer one: 4
Observer two: 4
Inside map operator Squaring 3
Inside map operator Squaring 3
Observer one: 9
Observer two: 9
```

**Analysis**
1. Two Observers namely `observer1` and `observer2` are subscribed to a Observable.
2. A new observable will be created for each observer subscribed.
3. Don't get confused by the term new Observable, which means again the observable start from the first.
4. In this case again the observable start to emit the range from 1 till length of 3 for every Observer.
5. So all the operations will be started from the first. For example in this code snippet, 
you will note that for each Observer, the map() operation is being carried out twice. 
So if we had 10 Observers, the map() operation would be carried out 10 times before the integer is emitted. 

It is not really needed to initiate the map operator for every Observers right?. 
So how can we avoid this? 

1. There should be one container to hold all these Observers and that container should act as 
an Observer so that it can be subscribed to an Observable.
2. Assume creating a class that implements a Observer, So now this class will act as an Observer and can be 
subscribed to an Observable.
3. Let us add the `observer1` and `observer2` to this container class and subscribe this class to Observable.
4. As usual Observable will emit items and further operations will be carried over. 
5. Finally processed data will be passed to the container class. 
6. Now this container class should capable of emitting data to the `observer1` and `observer2`. 
So I am extending this container class with Observable. Now this container class can be called as Subject.
7. So Subject will keep track of already executed operations (in our case it is `map` operation) and will
not initiate it again for the next subscribers instead emit the already processed item to multiple subscribers. 
This is called Multicasting.

#### With Subject

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })
        
// Creating a subject and subscribing it with observable
val subject = ReplaySubject.create<Int>()
observable.subscribe(subject)

// now subscribing or adding observers to subject.
subject.subscribe(observer1)
subject.subscribe(observer2)
```

**Output**

```
Observer1 onSubscribe
Inside map operator Squaring the input
Observer2 onSubscribe
Observer1 Output 1
Observer2 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Observer2 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer2 Output 9
Observer1 onComplete
Observer2 onComplete
```
  
I hope everyone understand the concept of Subject.

What is next? 
 
You have to know what is Cold and Hot Observable. 
These topics are straightforward and you can easily understand.

### Cold Observables
* Observable that don’t emit items until a subscriber subscribes. 
* It will generate the same sequence for every observer. 
* It will start to emit from first for every observer.
* Different observable source will be created for each observer.

[See Example](#without-subject)

### Hot Observables 
* Observables that don’t wait for any subscription. They start emitting items when created.
* They don’t emit the sequence of items again for a new subscriber.
* Depending on when observers subscribe, they may miss some of those emissions.
* For example in Android, a click event can be represented as an observable. And it is only 
logical for observers to receive only the events after they subscribed, 
so the click observable has no need to cache them for replay.
* Subject is by default Hot Observable.

We have two ways of creating hot observable as follow

1. **Subject:** Using subject, we can not only convert the cold into hot observable but also 
can create the hot observable from scratch. We have already discussed what is subject? in
the above section, so we have to see the types of subject. 

2. **ConnectableObservable:** By using ConnectableObservable, we can only convert the cold observable into hot observable by using 
its publish and connect methods and various variants like refCount, autoConnect and replay etc. 

[See Example for Subject](#with-subject)

### Connectable Observable

```
//Part1 - Creating cold observable
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

//Part2 - Converting cold to hot observable
val connectableObservable = observable.publish()

//Part3 - Subscribing observers to the ConnectableObservable
connectableObservable.subscribe(observer1)
connectableObservable.subscribe(observer2)

//Part4 - Call connect method of ConnectableObservable
connectableObservable.connect()
```

**Output**

```
Observer1 onSubscribe
Observer2 onSubscribe
Inside map operator Squaring the input
Observer1 Output 1
Observer2 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Observer2 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer2 Output 9
Observer1 onComplete
Observer2 onComplete
```

**Analysis**

If you see the above code snippet, there will be four parts. 
One is creating a observable (cold observable), second is converting cold observable to
hot observable using ConnectableObservable. So now emission will not happen when observers subscribe to this hot observable,
emission happens only when `connect()` method is called. `connect()` method will instructs a ConnectableObservable to begin emitting items. 
From the output its clearly evident that map operation is called only for one observer's emissions and its result is cached for other observers
which is called Multicasting. 

### refCount

`refCount` is nothing but a counter which keeps track of the observers count that are subscribed to the source observable.
If you have one observer subscribed, it increments by one. If that observer unsubscribe, then it decrements by one. 
What is the use of tracking the observer count?  By understanding the following `refCount` variants, you will
know the answer for this question 

1. refCount() 
2. refCount(subscriberCount: Int)
3. refCount(timeout: Long, unit: TimeUnit)
4. refCount(timeout: Long, unit: TimeUnit, scheduler: Scheduler)
5. refCount(subscriberCount: Int, timeout: Long, unit: TimeUnit) 
6. refCount(subscriberCount: Int, timeout: Long, unit: TimeUnit, scheduler: Scheduler) 
 
**refCount():**

Instructs a ConnectableObservable to start emitting items at any time it see at least one subscriber. 
In other words we can tell `refCount()` as a automatic `connect()` under certain conditions.

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

val refCountObservable = observable.publish().refCount()
println("Wait for 3 seconds for observer1 to subscribe")
Thread.sleep(3000)
refCountObservable.subscribe(observer1)
```
**Output**
```
Wait for 3 seconds for observer1 to subscribe
Observer1 onSubscribe
Inside map operator Squaring the input
Observer1 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer1 onComplete
```

**Analysis**

In the above code snippet I have used `Thread.sleep(3000)` to wait for 3 seconds before `observer1` subscribes to check how 
`refount()` actually works. In the output you can see, it waits for 3 seconds and once
`observer1` is subscribed the emission of items starts. When the `observer1` unsubscribe, it terminates the
connection. 

Let's see one more scenario with `refCount()`. 

What will happen if multiple observers subscribes like below.

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

val refCountObservable = observable.publish().refCount()
println("Wait for 3 seconds for observer1 and observer2 to subscribe")
Thread.sleep(3000)
refCountObservable.subscribe(observer1)
refCountObservable.subscribe(observer2)
```

**Output**

```
Wait for 3 seconds for observer1 and observer2 to subscribe
Observer1 onSubscribe
Observer2 onSubscribe
Inside map operator Squaring the input
Observer1 Output 1
Observer2 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Observer2 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer2 Output 9
Observer1 onComplete
Observer2 onComplete
```

If multiple observers subscribes without any delay between each subscription 
then you can notice Multicasting works perfectly. But what will happen if multiple observers subscribes 
with some delay between each subscription.

Let us see with a example

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

val refCountObservable = observable.publish().refCount()
println("Wait for 3 seconds for observer1 to subscribe")
Thread.sleep(3000)
refCountObservable.subscribe(observer1)
println("Wait for 3 seconds for observer2 to subscribe")
Thread.sleep(3000)
refCountObservable.subscribe(observer2)
```
In the above code snippet, you can see that I have created a delay of 3000 milliseconds between each subscription.  

**Output**

```
Wait for 3 seconds for observer1 to subscribe
Observer1 onSubscribe
Wait for 3 seconds for observer2 to subscribe
Inside map operator Squaring the input
Observer1 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer1 onComplete
Observer2 onSubscribe
Inside map operator Squaring the input
Observer2 Output 1
Inside map operator Squaring the input
Observer2 Output 4
Inside map operator Squaring the input
Observer2 Output 9
Observer2 onComplete
```

In the above output you can see the map operator is called for the two observers instead of doing Multicasting. 
Before I let you know the answer, think yourself why Multicasting did not work here.

To know the answer you should know the following terminologies.

* Source - Which emits values. In our case `Observable.range(1, 3)` is the source.

* Consumers - Catch the emitted values. In the above sample  `observer1` and `observer2` are the consumers.

When the connection between source and consumers gets disconnected?

If the source completes its task the connection will be disconnected. For example in our sample when the `Observable.range(1,3)` completes
emitting values 1,2,3 the connection will be disconnected.  

Now let us come to the reason why multicast did not work in the above sample. In the code I have given a delay
of 3000 milliseconds between each subscription, so in the mean time the source completes the task
and closes its connection. When observer2 subscribes after 3000 milliseconds, source creates a new 
connection. 
 

**refCount(subscriberCount: Int):**

Instructs a ConnectableObservable to start emitting items at any time it see the mentioned no of subscriber.

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

val refCountObservable = observable.publish().refCount(2)
println("Wait for 3 seconds for observer1 to subscribe")
Thread.sleep(3000)
refCountObservable.subscribe(observer1)
println("Wait for another 3 seconds for observer2 to subscribe")
Thread.sleep(3000)
refCountObservable.subscribe(observer2)
```

**Output**
```
Wait for 3 seconds for observer1 to subscribe
Observer1 onSubscribe
Wait for another 3 seconds for observer2 to subscribe
Observer2 onSubscribe
Inside map operator Squaring the input
Observer1 Output 1
Observer2 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Observer2 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer2 Output 9
Observer1 onComplete
Observer2 onComplete
```

**Analysis**

From the above sample it is clear that `refCount(2)` waits for the two observers to
subscribe once the second observer subscribed, emission of item gets started. In the
output you can see once **Observer2 onSubscribe** line get printed, the map operations and
result values getting printed. 

**refCount(timeout: Long, unit: TimeUnit):**

Connects to the upstream ConnectableObservable if the number of subscribed 
observers reaches 1 and disconnect after the specified timeout if all subscribers have unsubscribed. 

The above definition is bit tricky which might lead to confusion. 
Because you might get confused, When the timeout will be used?. To understand the concept
easily I have used `Observable.interval()` insteadof `Observable.range()` because range() operator
will disconnect as soon as all the values emitted. To demonstrate this I am in need of
a source that emits values without any limit and the connection will be alive, so I used interval() operator. 

[Click to know more about interval operator](../basicoperators/README.md)


```
 val observable = Observable.interval(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Long, Long> {
                override fun apply(input: Long): Long {
                    println("Inside map operator returns the same input.")
                    return input
                }
            })

val refCountObservable = observable.publish().refCount(12000, TimeUnit.MILLISECONDS)
refCountObservable.subscribe(observer1)
Thread.sleep(4000)
refCountObservable.subscribe(observer2)
Thread.sleep(3000)
observer1.dispose()
observer2.dispose()
```

**Output**

```
Inside map operator returns the same input.
Observer1 Output 0
Inside map operator returns the same input.
Observer1 Output 1
Observer2 Output 1
Inside map operator returns the same input.
Observer1 Output 2
Observer2 Output 2
Inside map operator returns the same input.
Inside map operator returns the same input.
Inside map operator returns the same input.
Inside map operator returns the same input.
Inside map operator returns the same input.
Inside map operator returns the same input.
```

**Analysis**

1. So for every 2000 milliseconds, value will be emitted by interval operator. In the code 
I have used `refCount(12000, TimeUnit.MILLISECONDS)` which means after 12000 milliseconds
refCount will check the following

* The connection is still open
* Observers subscribed are unsubscribed

If the above two conditions are satisfied then refCount will disconnect the connection. Let's 
see what is happening in the above sample.

2. I have created the delay of 4000 milliseconds between each subscription. Due to this delay
in the output you can notice that the first emitted value **0** is processed using map operator and
caught only by observer1 and not by observer2 because it is not yet subscribed. When observer2 
got subscribed after the delay, it will observes the emission from that point onwards. In the above 
output you can see observer2 start to observe value from **1**.   

3. Still it is not yet over. To check how the timeout works I am disposing the observers
3000 milliseconds after observer2's subscription. From the output you can see that after the
two observers got disposed no emitted values are printed only the text used inside map operator getting 
printed that too until timeout of 12000 milliseconds and after the timeout the connection gets closed.

4. One more important point here is after the timeout if any of the observer is still not yet disposed 
and the connection to the upstream is still alive then refCount will reschedule the timeout.  
