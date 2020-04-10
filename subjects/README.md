
* [Subject](#subject)
* [When to use subject?](#when-to-use-subject)
* [Types of Subject](#types-of-subject)
* [PublishSubject](#publishsubject)
* [BehaviorSubject](#behaviorsubject)
* [ReplaySubject](#replaysubject)
* [AsyncSubject](#asyncsubject)
* [UnicastSubject](#unicastsubject)
* [SingleSubject](#singlesubject)
* [Connectable Observable](#connectable-observable)
* [refCount](#refcount)
* [share](#share)
* [autoConnect](#autoconnect)

### Subject

* If you see the RxJava's Subject.java you will notice that this class extends an Observable and 
implements Observer at the same time. Which means it acts as both Observable and Observer.

* Subjects can multicast items to multiple subscribers. 
Multicasting makes it possible to run expensive operations once and emit the results to multiple subscribers. 
This prevents doing duplicate operations for multiple subscribers.

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
you will note that for each Observer, the map() operation is being carried out. 
So if we had 10 Observers, the map() operation would be carried out for all the 10 observers. 

It is not really needed to initiate the map operator for every Observers right?. 
So how can we avoid this? 

1. There should be one container to hold all these Observers and that container should act as 
an Observer so that it can be subscribed to an Observable.
2. Assume creating a class that implements a Observer, So now this class will act as an Observer and can be 
subscribed to an Observable.
3. Let us add the `observer1` and `observer2` to this container class and subscribe this class to Observable.
4. As usual Observable will emit items and further operations will be carried over. 
5. Finally processed data will be passed to the container class. 
6. Now this container class should capable of emitting data to the `observer1` and `observer2` as these observers
are present inside this container. So I am extending this container class with Observable. Thus this container class can be called as Subject.
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

Above is the very basic example for you to understand the basic concept of subject. 
So I have used `Observable.range()` to emit values. But in realtime subject will
be used differently. Let us see when you can use subject in realtime with some realtime 
examples.   
  
**Note**: Do learn the above part before entering the below sections.

### When to use subject?

To know the answer for this question, you should ask yourself the following questions.
    
1. What is the source of emission? (i.e) where do value coming from? or Who is calling onNext()?
2. Do I need multicasting? 

So let us see the my answers for these questions

##### What is the source of emission? (i.e) where do value coming from? or Who is calling onNext()?

Source of emission can be classified in two kinds

1. Hot  
2. Cold 

I would like to give you the known layman terms to understand the above two kinds.

**My Understanding 1:**

* <u>Hot</u>: Everyone might came across the word **Hot News**, which refers to the latest news or 
news which are currently released at this point of time. So now I will modify this reference
for our technical scenario. The source that emit latest event which is created at this point of time
is called **hot source**.

* <u>Cold</u>: News channels might telecast already existing news. So this can be modified for our technical
scenario, the source that emits already existing items is called **cold source**.  

**My Understanding 2:**

* <u>Hot</u>: A source of notifications that you have to push yourself. 
We must write code to push notifications (input).

* <u>Cold</u>: A source of notifications that you do not have to generate yourself. 
It could be any number of things eg: a timer callback, 
just emitting values one by one or arrayvalues, asynchronous result of reading from a file or sending a web request.

**My Understanding 3:**

* <u>Hot</u>: From technical perspective all the events (for example let us take button click event) are hot. 
The source that emits this event is called hot source. Because this event is created and emitted
at this point of time.

Note: Don't think of how the source will emit the events and get confused. We will go point by point.

* <u>Cold</u>: Source that emit items already existed is called cold source following are the
examples 

     1. Source that emit known values or emit values from asynchronous calls for example just have a look at all 
     [create observable operators](../basicoperators/README.md)
     2. Lets take Observable.just(1, 2, 3), so in this case source emits already existed values 1, 2, 3.
     3. Lets take the asynchronous network call (Retrofit + RxJava) that provides us the Observable source which emits the result.
     
**My Understanding 4:**

* <u>Hot</u>: It don't wait for any subscription. They start emitting items when created. 
Depending on when observers subscribe, they may miss some of those emissions. 
    1. Once again I am comparing this with **Hot News** concept where the news will be telecasted even there is no observer are
seeing. So this news might be missed by the observers who are joined lately. 

    2. For example in Android, a click event is a notification which is emitted. And it is only 
logical for observers to receive only the events after they subscribed.

* <u>Cold</u>: Observable that don’t emit items until a subscriber subscribes. 
It will generate the same sequence for every observer. It will start to emit from first for every observer.
This is like repeatedly telecasting already existing news so that a new observer who joined lately 
can also see the news from the first.

          
#### Do I need multicasting? 

I hope every one read about multicasting in the above section while reading about subject. 
Decide whether you need multicasting, If you really need multicasting then use subject.

### Ways to create hot observable source

We have two ways of creating hot observable as follow

1. **Subject:** Using subject, we can not only convert the cold into hot observable but also 
can create the hot observable from scratch. We have already discussed about subject in
the above section. 

    [See basic example for subject](#with-subject)

2. **ConnectableObservable:** By using ConnectableObservable, we can only convert the cold observable into hot observable by using 
its publish and connect methods and various variants like refCount, autoConnect and replay etc. 

### Types of Subject

1. PublishSubject
2. BehaviorSubject
3. ReplaySubject
4. AsyncSubject
5. UnicastSubject
6. SingleSubject

### PublishSubject

PublishSubject emits all the items and at that time if any of the consumer is already subscribed then they will get all emitted data. 
If any consumer subscribes later might miss out some or all emissions.

Let us have a look at the basic example.

**Basic Example:** This is to understand the actual characteristic of PublishSubject

```
val publishSubject = PublishSubject.create<Int>()
publishSubject.subscribe(observer1)
publishSubject.onNext(1)
publishSubject.onNext(2)
publishSubject.onNext(3)
publishSubject.subscribe(observer2)
publishSubject.onNext(4)
publishSubject.onNext(5)
publishSubject.onComplete()
```

**Output**
```
Observer1 onSubscribe
Observer1 Output 1
Observer1 Output 2
Observer1 Output 3
Observer2 onSubscribe
Observer1 Output 4
Observer2 Output 4
Observer1 Output 5
Observer2 Output 5
Observer1 onComplete
Observer2 onComplete
``` 

**Code Analysis**

1. I have created a PublishSubject and subscribing two observers namely `observer1` and `observer2`.
2. But before subscribing `observer2` I am emitting/publishing few values and you can see in the
output that `observer1` catching those values.
3. After subscribing the `observer2` you can notice in the output that `observer2` is getting
newly emitted values but not already emitted values.
4. This is the characteristic of PublishSubject.

From the above basic example you would have understand how PublishSubject will work. 
But this basic example is not enough to understand how this can be used in realtime. 
In realtime A PublishableSubject is useful, for instance, touch events, clicks, etc… 
so you can subscribe several observers to them but you just want to listen out for newer events.

**Real Time Example 1**

In this example I am going to list the restaurants in a location and simultaneously will also update
the restaurants count in the CustomToolbar. It is quite common to have different pieces of your application that need to 
react or get updated when something occurs somewhere within your UI or application. 
Hope this example will gives you how PublishSubject can be used in a real time.

**Code Analysis**

1. I have created a generic open class named `PublishSubjectState` that is the place where
`PublishSubject` is created, observed and events are published. 
2. My usecase here is to list the restaurants, so I have created an another class called
`LoadRestaurantState` which will simply extend `PublishSubjectState` but instead of generic type,
you can see I have used `Restaurants` as the type.
3. In the Application class `SubjectApplication`, I have initialized  `LoadRestaurantState` so it
can be used anywhere inside this `SubjectApplication`. It will be initialized only once and can be used
globally.
4. I have a created a activity named `RestaurantsActivity` that lists the restaurants in a particular location and 
also created a CustomToolbar(Displays count of restaurants) within this activity.
5. So I have to update two views simultaneously that expects the same input to update view.
6. Which means, in the list screen I have to use restaurants list data to display restaurants and the same 
restaurants list data needed to calculate the count of the restaurants which should be displayed in the toolbar.
7. So now listening for the input should be happened in two places.   
8. Just have a look at `RestaurantsActivity` and `CustomRestaurantToolbar` where I have used `LoadRestaurantState`
and observing for the input.
9. In `RestaurantsActivity` I have called `RestaurantsPresenter.loadRestaurantsForFirsTime(lat, lon)` (API call) to retrieve
the restaurants in the provided latitude and longitude.
10. Once the API call gets the response, using `LoadRestaurantState.publish(response)` the result will be published.
11. The published result will be available for the views (list and customtoolbar) those were observing for the input to update the views. 
12. Once you have understand this example you will know how `PublishSubject` works in realtime. For simplicity I have used only
two views those were observing to the `PublishSubject` that is initialized at the `Application` class. 
  
**Real Time Example 2**  

This is a example where `PublishSubject` is used for transferring data between Activities. 
Usually we would use `Intent` for transferring data between Android components (In our case it is Activities). 
Basic types like String, Int.. can be transferred easily but user defined model should be serializable or parcelable so that it
can be transferred. But it some limitations, if the content is too large or any of the nested user model inside is not of serializable type then
data will be missed when you get the data. To avoid these kind of scenarios we can use Subject like I have done in this example.

**Code Analysis**
 1. I have created Object class type in kotlin `RxPublishSubjectBus.kt` where methods for publishing data and observing the
 result using `PublishSubject` is coded. 
 2. `RxPublishSubjectBus.kt` is generic.
 3. There are two Activities `PublishSubjectActivityOne` and `PublishSubjectActivityTwo`. 
 4. `PublishSubjectActivityOne` will have code for observing (using `RxPublishSubjectBus`) the sent result from `PublishSubjectActivityTwo`.
 5. From `PublishSubjectActivityOne` when user clicks a button, second Activity named `PublishSubjectActivityTwo`
 will be called.
 6. In `PublishSubjectActivityTwo` user can type any text in a edittext and this 
 text will be published using `RxPublishSubjectBus` when button click is happened.
 7. Once the data is published, `PublishSubjectActivityTwo` will be destroyed using finish() method.
 8. So now `PublishSubjectActivityOne` will be visible and though this activity is already listening for
 the result, it will be receiving the data that we sent from `PublishSubjectActivityTwo`
 9. Hope you all understand how the code flows, Just have a look at the code you will be understanding clearly.
 10. Once you understand this example, you might have doubt whether it is possible to transfer data from `PublishSubjectActivityOne` 
 to `PublishSubjectActivityTwo` using `PublishSubject`. No you can't because `PublishSubject` can receive data 
 only if it is already subscribed before the data is published. 
 So in our scenario before starting the `PublishSubjectActivityTwo` the data will be published and only in `onCreate()` of 
 `PublishSubjectActivityTwo` subscription for listening the data will done.
 11. So how can we achieve this? Yes we can achieve this if we use `BehaviorSubject` instead of `PublishSubject`. 
   
### BehaviorSubject

BehaviorSubject emits the most recent item at the time of their subscription and all items after that. 
Let us see the basic example.

**Basic Example:**

```
val behaviorSubject = BehaviorSubject.create<Int>()
behaviorSubject.subscribe(observer1)
behaviorSubject.onNext(1)
behaviorSubject.onNext(2)
behaviorSubject.onNext(3)
behaviorSubject.subscribe(observer2)
behaviorSubject.onNext(4)
behaviorSubject.onNext(5)
behaviorSubject.onComplete()
```

**Output**
```
Observer 1 onSubscribe
Observer 1 onNext: 0
Observer 1 onNext: 1
Observer 1 onNext: 2
Observer 1 onNext: 3
Observer 2 onSubscribe
Observer 2 onNext: 3
Observer 1 onNext: 4
Observer 2 onNext: 4
Observer 1 onNext: 5
Observer 2 onNext: 5
Observer 1 onComplete
Observer 2 onComplete
```

**Code Analysis**

1. I have created a BehaviorSubject and subscribing two observers namely `observer1` and `observer2`.
2. But before subscribing `observer2` I am emitting/publishing few values and you can see in the
output that `observer1` catching those values.
3. After subscribing the `observer2` you can notice in the output that `observer2` is getting
last recent value emitted and other newly emitted values.
4. This is the characteristic of BehaviorSubject.

**Real Time Example**

We are going to use the Real Time Example 2 of `PublishSubject`. So instead of `PublishSubject` I am going to
use `BehaviourSubject`. In this example user can type any text in the editor from Activity1 and when button click happens 
the entry text data will be sent from Activity 1 to Activity 2 using `BehaviorSubject`.

**Code Analysis**
 1. I have created Object class type in kotlin `RxBehaviorSubjectBus.kt` where methods for publishing data and observing the
 result using `BehaviorSubject` is coded. 
 2. `RxBehaviorSubjectBus.kt` is generic.
 3. There are two Activities `BehaviorSubjectActivityOne` and `BehaviorSubjectActivityTwo`. 
 4. `BehaviorSubjectActivityOne` will have code for publishing the data.
 4. `BehaviorSubjectActivityTwo` will have code for observing (using `RxBehaviorSubjectBus`) the sent result from `BehaviorSubjectActivityOne`.
 5. From `BehaviorSubjectActivityOne` when user types the text in the editor and clicks a button, the entered text will be published using `BehaviorSubject` 
 and the second Activity named `BehaviorSubjectActivityTwo` will be called.
 6. So when `BehaviorSubjectActivityTwo` is created in `onCreate()` method I have added the code that subscribes to the publisher.
 7. Once `BehaviorSubjectActivityTwo` subscribes to the publisher `BehaviorSubject`, 
 the latest published user entry data will be available to this second activity.
 8. Hope you all understand how the code flows, Just have a look at the code you will be understanding clearly.
 
### ReplaySubject

### AsyncSubject

### UnicastSubject

### SingleSubject



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

**refCount(timeout: Long, unit: TimeUnit, scheduler: Scheduler):**

This operator is same as the above one but here we have a proficiency to add the Scheduler. 
This refCount operates on the specified Scheduler.

**refCount(subscriberCount: Int, timeout: Long, unit: TimeUnit, scheduler: Scheduler):** 

Instructs a ConnectableObservable to start emitting items at any time it see the mentioned no of subscriber with
timeout and scheduler options included.

### share

This operator is the combination of **publish() + refCount()**

Just you have to change `Observable.publish().refCount()` to `Observable.share()`. 

### autoConnect


**autoConnect():**

Instructs a ConnectableObservable to start emitting items at any time it see at least one subscriber. 
You might think that this definition looks same as that of refCount. Yes it is but there is a
difference between these two operators. autoConnect() will never renew the connection if the source
terminates, no matter how Observers come and go. But refCount() will renew the connection if the source terminates.

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

val autoConnectObservable = observable.publish().autoConnect()
println("Wait for 3 seconds for observer1 to subscribe")
Thread.sleep(3000)
autoConnectObservable.subscribe(observer1)
println("Wait for another 3 seconds for observer2 to subscribe")
Thread.sleep(3000)
autoConnectObservable.subscribe(observer2)
```

**Output**

```
Wait for 3 seconds for observer1 to subscribe
Observer1 onSubscribe
Wait for another 3 seconds for observer2 to subscribe
Inside map operator Squaring the input
Observer1 Output 1
Inside map operator Squaring the input
Observer1 Output 4
Inside map operator Squaring the input
Observer1 Output 9
Observer1 onComplete
Observer2 onSubscribe
```

**Analysis**

To check how `autoConnect()` operator will work, I have given a delay of 3000 milliseconds between each subscription. 
In the output you can see that subscription, map operation and consumption for observer1 is carried out perfectly but
for observer2 only `onSubscribe` is being called. This is because of the delay before subscribing observer2, 
so in the mean time the source completes the task and closes its connection. When observer2 subscribes after 3000 milliseconds, 
source is not creating a new connection.

**autoConnect(numberOfSubscribers: Int):**

This is same as `autoConnect()` but it includes parameter **numberOfSubscribers** 
thus it instructs the ConnectableObservable to start emitting items at any time 
it see the mentioned no of subscriber. Lets see the same example that we discussed for
`autoConnect()`

```
val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

val autoConnectObservable = observable.publish().autoConnect(2)
println("Wait for 3 seconds for observer1 to subscribe")
Thread.sleep(3000)
autoConnectObservable.subscribe(observer1)
println("Wait for another 3 seconds for observer2 to subscribe")
Thread.sleep(3000)
autoConnectObservable.subscribe(observer2)
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

From the output you can see the difference between `autoConnect()` and `autoConnect(numberOfSubscribers: Int)`. 
It is clear that only after Observer2's subscription the autoConnect operator 
instructs the ConnectableObservable to start emitting. This is due to the `noOfSubscribers` we have mentioned as 2. 
If we have mentioned noOfSubscribers as 3 then after the third observer's subscription emission will be started.






