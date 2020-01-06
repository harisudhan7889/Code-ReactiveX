# Schedulers

* Schedulers are one of the main components in RxJava. They are responsible for performing operations of Observable on different threads. 
Scheduler is a component that schedules units of work.

* Scheduler is a component that schedules units of work.

* By default, Rx is single-threaded.

There are multiple types of schedulers available for RxJava

### Schedulers.io() 

This is one of the most common types of Schedulers that are used. 
They are generally used for non-CPU intensive IO related stuff, 
such as network requests, file system operations, accessing the database. 
IO scheduler is backed by an unbounded thread pool. 
Java Thread pool represents a group of worker threads that are waiting 
for the job and reuse many times. The size of its thread pool can grow as needed.
```
observable.subscribeOn(Schedulers.io())
```

### Schedulers.computation()

This scheduler is quite similar to IO Schedulers as this is backed by 
thread pool too. The number of threads created using this scheduler 
completely depends on the number of CPU cores available. 
So if you have two cores in your mobile, it will have 2 threads in the pool. 
This also means that if these two threads are busy then 
the process will have to wait for them to be available. 
While this limitation makes it a poor fit of IO related things, 
it is good for performing small calculations and are generally quick to 
perform an operation. 
```
observable.subscribeOn(Schedulers.computation())
```

### Schedulers.newThread()

Creates a new thread for each active observables. Creates a new Thread for each unit of work. Since it just spawns a new thread whenever it requires, you need to take care of this because thread creation is a costly operation and can have a drastic effect in mobile environment if the number of observables are high enough. This thread is not reused.
This Scheduler can be used only when tasks are large: it takes a lot of time to complete, but there are very few of them in number so that threads will not be reused at all.

```
observable.subscribeOn(Schedulers.newThread())
```

### Schedulers.immediate()

This scheduler executes work immediately on the current thread by blocking the previous task running.

```
observable.subscribeOn(Schedulers.immediate())
```

### Schedulers.trampoline()

This scheduler runs the code on current thread. 
So if you have a code running on the main thread, 
this scheduler will add the code block on the queue of main thread. 
It is quite similar to Immediate Scheduler as it also blocks the thread, 
however, it waits for the current task to execute completely(while Immediate 
Scheduler invokes the task right away). Trampoline schedulers come in handy when we have more than one observable 
and we want them to execute in order.

```
observable.subscribeOn(Schedulers.trampoline())
```

```
Observable.just(1,2,3,4)
    .subscribeOn(Schedulers.trampoline())
    .subscribe(onNext);
 Observable.just( 5,6, 7,8, 9)
    .subscribeOn(Schedulers.trampoline())
    .subscribe(onNext);
```

**Output**
```
1
2
3
4
5
6
7
8
9
```

### Schedulers.from(Executors.newFixedThreadPool(2))

You can use this to create a custom Scheduler backed by your own Executor. 
Suppose you want to limit the number of parallel network calls happening in your app, 
then you can create a custom Scheduler with an executor of fixed thread pool size.

```
Scheduler.from(Executors.newFixedThreadPool(n))
```

### AndroidSchedulers.mainThread()

This is a special Scheduler that’s not available in the core RxJava library. You need to use the RxAndroid extension library to make use of it. This scheduler is specifically useful for Android apps to perform UI 
based tasks in the main thread of the application.

```
observable.observeOn(AndroidSchedulers.mainThread())
```

### subscribeOn()

In simple words, this operator tells which thread the source observable can emit its items on. When you have a chain of observables, the source observable is always at the root or top of the chain from where the emissions originate. As you have already seen; if we don’t use subscribeOn(), all the emissions happen directly on the thread the code is executed on (in our case, the main thread).
It is also important to understand that you cannot use subscribeOn() multiple times in your chain. Technically, you can do so, but that won’t have any additional effect. In the snippet below we are chaining three different Schedulers ,but can you guess which Scheduler will the source observable emit its items on?

```
Observable.just(1, 2, 3, 4, 5, 6)
           .subscribeOn(Schedulers.io())
           .subscribeOn(Schedulers.computation())
           .subscribeOn(Schedulers.newThread())
```

Yes it is Schedulers.io(). Even if you put multiple subscribeOn() operators in your chain, only the one closed to the source observable will take its effect and nothing else.

### observerOn()

```
Observable.just(1, 2, 3, 4, 5, 6)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .observeOn(Schedulers.single())
          .observeOn(Schedulers.computation())
```

If you run this snippet, you will see for yourself that all the items are consumed in the RxComputationThreadPool-1 thread, which means that the last observeOn() with Schedulers.computation() made its effect. 
But wonder why?
As we already know, subscriptions always happen upstream, on the other hand, emissions always happen downstream.

