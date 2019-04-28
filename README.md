# Code ReactiveX 
My coding journey with **RxJava+RxAndroid**

## Reactivex?


* ReactiveX is a programming model, where data flows emitted by one component (eg. Click Event) will be 
processed by set of **RX Functions** which will pass the changes to 
another component those are registered to receive the changed data.

* In ReactiveX data flows are considered as streams which means continues flow without storing anything.

* A Realtime Scenario: Consider the streams are the streets where cars are present but just passing by, 
they are not really stored there. When we look to the street we only see the car just passing by. 
This continuous observation that happens once the stream is defined. But streams can do more than that. 
If the driver is a wanted criminal then the police might stop him, and the car basically gets filtered from the stream(filter function).


* RX = Observer + iterator patterns + functional idioms

## Basic Operators:
Through these operators we can create a basic observables, 
so these operators will be coming under operator category of **Creating Observables**.

* [create](#create-operator)
* [just](#just-operator)
* [defer](#defer-operator)
* [fromArray](#fromarray-operator)
* [fromCallable](#fromcallable-operator)
* [fromIterable](#fromiterable-operator)
* [range](#range-operator)
* [repeat](#repeat-operator)
* [repeat With Count](#repeat-operator-with-count)
* [repeatWhen](#repeatwhen-operator)
* [repeatUntil](#repeatuntil-operator)
* [interval](#interval-operator)
* [timer](#timer-operator)

#### create Operator
   
  Creates a observable which provides the control to you to play with your function. Since you have the control, you have to handle all the scenarios.
  ```
  val valueArray = Array()
  val observable = Observable.create(object : ObservableOnSubscribe<String> {
                  override fun subscribe(emitter: ObservableEmitter<String>) {
                      try {observable.subscribe(observer)
                      for (value in valueArray) {
                          emitter.onNext(value)
                      }
                          emitter.onComplete()
                  } catch (e: Exception) {
                          emitter.onError(e)
                  }
                  }
              })
  ```  
  If you see the above code snippet you can notice that **onNext, onComplete and onError** are called based on your functionality need.
  This Observable starts getting executed as soon as a Observer subscribes to it like below.
  ```
  observable.subscribe(observer)
  ```
 
  **Actual Definition:** This operator creates an Observable from scratch by calling observer methods programmatically. 
  An emitter is provided through which we can call the respective interface methods when needed.
  
  **When to use?** 
  
  You can use Observable.create() in the following situations 
  1. If you are more experienced and need a custom operator or listener.
  2. If you want to handle only specific exceptions rather than using a generic one. 
  
#### just Operator

This is the simple way to create a Observable. 
Whatever data type object you give as an input to `just()`, 
it will returns the observable of that type in a single emission
(single emission means onNext() will be called only once).

**Syntax:**
```
Observable.just(new String[]{"A", "B", "C", "D", "E", "F"})
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(String[] strings) {
                        System.out.println("onNext: " + Arrays.toString(strings));
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError $e");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
```
**Output:**
```
onSubscribe
onNext: ABCDEF
onComplete
```
**Actual Definition:** The Just operator converts an item into an Observable that emits that item.

#### defer Operator 

Like **Observable.create()**, this operator does not create the Observable until the Observer subscribes 
but doest not have any custom operators like **Observable.create()**. 

```
var input: String? = "null"
        val just = Observable.just(input)
        input = "123"
        just.subscribe(observer)

        input = "null"
        val defer = Observable.defer { Observable.just(input) }
        input = "123"
        defer.subscribe(observer)
``` 
**Output:**
```
onSubscribe
null
onComplete

onSubscribe
123
onComplete
```
### 

| create() | just() | defer() |
|---|---|---|
| Do not create the Observable <br/> until the observer subscribes | Creates the Observable when **just()** <br/> is called  | Do not create the Observable <br/> until the observer subscribes   |
| Have our own custom functions| NA| NA|
|It can use the same observable for <br/> each observer|It can use the same observable for <br/> each observer|It creates a new Observable <br/> each time you get a new Observer|

#### fromArray Operator 

This operator creates an Observable with array of items as an input. The created Observable is capable
of emitting each item one at a time.

```
val valueArray: Array<String> = arrayOf("A", "B", "C", "D", "E", "F")
Observable.fromArray(*valueArray)
                .subscribe(object : Observer<String?> {

                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(value: String) {
                        System.out.println("onNext $value")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
``` 
**Output:**
```
onSubscribe
onNext A
onNext B
onNext C
onNext D
onNext E
onNext F
onComplete
```
#### fromCallable Operator
* This operator wraps up the expensive imperative code within it and change the imperative code to reactive.
* Suppose you want to make an expensive call like a Network call, Database update, Write/Read file operation, etc. You cannot do these expensive calls in the main thread which will affect the usability of your application.
  So to make these piece of imperative code asynchronous as well as a reactive one, wrap these expensive calls within Observable.fromCallable().
* This method will not execute immediately, will execute only when a observer subscribe to it.

```
Observable.fromCallable(object : Callable<String> {
            override fun call(): String {
                return getUserDetailFromDB()
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String?> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(response: String) {
                    System.out.println("onNext response")
                }

                override fun onError($error: Throwable) {
                    System.out.println("onError $error")
                }
            })
```
##### At what situation you can use this operator?
Imagine that you have a function. This function is developed before RxJava and cannot be changed. 
But somehow you need a function which returns a `Observable<Object>`. In this scenario you can use Observable.fromCallable() operator.

##### Difference between fromCallable() and defer()
| fromCallable()| defer() |
|---|---|
|Exceptions will be handled by <br/>the operator itself| We have to handle exceptions <br/> by ourself |

#### fromIterable Operator
This operator is same as `Observable.fromArray()` but it creates an Observable with items of Iterable type as an input. The created Observable is capable
of emitting each item one at a time.

```
val list = Arrays.asList("A","B","C","D","E","F")
Observable.fromIterable(list)
                .subscribe(object : Observer<String?> {

                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(value: String) {
                        System.out.println("onNext $value")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
```
**Output:**
```
onSubscribe
onNext A
onNext B
onNext C
onNext D
onNext E
onNext F
onComplete
```
#### range Operator
This operator creates an Observable that emits a range of sequential integers. 
The function takes two arguments: the starting number and length.

The below sample creates an Observable using Observable.range() method. 
The below has a starting number of 3 and a range of 5 numbers, so it will print values from 3 to 7.

```
Observable.range(3, 5)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    System.out.println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }

            })
```
**Output:**
```
onSubscribe
onNext 3
onNext 4
onNext 5
onNext 6
onNext 7
onComplete
```

#### repeat Operator

This operator creates an Observable that emits a particular item or sequence of items repeatedly. 

```
Observable.range(2, 2)
            .repeat()
            .take(3)
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    System.out.println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }

            })
```
**Output:**
```
onSubscribe
onNext 2
onNext 3
onNext 2
onNext 3
onNext 2
onNext 3
onComplete
```
**Note**: You have to specify where the asynchronous execution should happen via subscribeOn 
in this case (i.e) `subscribeOn(Schedulers.io())`. If not, repeat() execution will happen 
in the UI thread and block the screen.

#### repeat Operator With Count:

Using this operator you can pass the number of repetitions that can take place as well.

```
Observable.range(2, 2)
            .repeat(2)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    System.out.println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }

            })
```
**Output:**
```
onSubscribe
onNext 2
onNext 3
onNext 2
onNext 3
onComplete
```
#### repeatWhen Operator

This operator which allows you to specify a custom logic for retries.

In the below example I have combined `repeat()` and `delay()` with the help of the `repeatWhen()`.

```
Observable.range(startNo, count)
            .repeatWhen(object : Function<Observable<Any>, ObservableSource<Any>> {
                override fun apply(t: Observable<Any>): ObservableSource<Any> {
                    return t.delay(2, TimeUnit.SECONDS)
                }
            })
            .take(3)
            .subscribe(object : Observer<Int> {
                        override fun onComplete() {
                            System.out.println("onComplete")
                        }
            
                        override fun onSubscribe(d: Disposable) {
                            System.out.println("onSubscribe")
                        }
            
                        override fun onNext(value: Int) {
                            System.out.println("onNext $value")
                        }
            
                        override fun onError(error: Throwable) {
                            System.out.println("onError $error")
                        }
            
                    })
```

#### repeatUntil Operator

This operator allows you to specify until which point retries should happen.

```
val startTimeMillis = System.currentTimeMillis()
Observable.range(startNo, count)
            .repeatUntil(object : BooleanSupplier {
                override fun getAsBoolean(): Boolean {
                    return System.currentTimeMillis() - startTimeMillis > 500
                }
            }).subscribe(object : Observer<Int> {
                          override fun onComplete() {
                              System.out.println("onComplete")
                          }
              
                          override fun onSubscribe(d: Disposable) {
                              System.out.println("onSubscribe")
                          }
              
                          override fun onNext(value: Int) {
                              System.out.println("onNext $value")
                          }
              
                          override fun onError(error: Throwable) {
                              System.out.println("onError $error")
                          }
              
                      })
```

#### interval Operator

Create an Observable that emits a sequence of integers spaced by a given time interval.
The below code will print values from 0 after every second.
```
Observable.interval(1, TimeUnit.SECONDS)
.subscribe(object : Observer<Long> {
                          override fun onComplete() {
                              System.out.println("onComplete")
                          }
              
                          override fun onSubscribe(d: Disposable) {
                              System.out.println("onSubscribe")
                          }
              
                          override fun onNext(value: Long) {
                              System.out.println("onNext $value")
                          }
              
                          override fun onError(error: Throwable) {
                              System.out.println("onError $error")
                          }
              
                      })

```
**Output:**
```
onSubscribe
onNext: 0
onNext: 1
onNext: 2
onNext: 3
onNext: 4
onNext: 5
onNext: 6
onNext: 7
onNext: 8
onNext: 9
.
.
.
```

* `Observable.interval()` will operates by default on the `Schedulers.computation()`. 
Suppose if you want to specify the scheduler explicitly then use the below interval() syntax.  

     ```
     Observable.interval(period, TimeUnit.SECONDS, Schedulers.io())
     Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
     ```
* Suppose if you wish that the initial delay should be different, then use the below syntax
     
     **Without explicit scheduler:**
     ```
     Observable.interval(initialDelay, period, TimeUnit.SECONDS)
     Observable.interval(2, 1, TimeUnit.SECONDS)
     ```
     **With explicit scheduler:**
     ```
     Observable.interval(initialDelay, period, TimeUnit.SECONDS, Schedulers.io())
     Observable.interval(2, 1, TimeUnit.SECONDS, Schedulers.io())
     ```
     
* `Observable.interval()` will start always from 0 and keeps emitting until we dispose the observable. 
Suppose if you want the start range and ends at specific count then you can use the following syntax.

     **Without explicit scheduler:**
     ```
     Observable.interval(start, count, initialDelay, period, TimeUnit.SECONDS)
     Observable.interval(2, 5, 2, 1, TimeUnit.SECONDS)
     ``` 
     **With explicit scheduler:**
     ```
     Observable.interval(start, count, initialDelay, period, TimeUnit.SECONDS, Schedulers.io())
     Observable.interval(2, 5, 2, 1, TimeUnit.SECONDS, Schedulers.io())
     ```     

**Usage in Real Time Scenario:** 
  
  * This operator can be used for the background data sync for every time interval specification. 
 
#### timer Operator

This operator will looks like `Observable.interval()` but the difference 
is, it creates an Observable that emits only one item after a specified delay then completes.

```
Observable.timer(1, TimeUnit.SECONDS)
.subscribe(object : Observer<Long> {
                          override fun onComplete() {
                              System.out.println("onComplete")
                          }
              
                          override fun onSubscribe(d: Disposable) {
                              System.out.println("onSubscribe")
                          }
              
                          override fun onNext(value: Long) {
                              System.out.println("onNext $value")
                          }
              
                          override fun onError(error: Throwable) {
                              System.out.println("onError $error")
                          }
              
                      })

```

**Output:**
```
onSubscribe
onNext: 0
onComplete
```

* `Observable.timer()` will operates by default on the `Schedulers.computation()`. 
Suppose if you want to specify the scheduler explicitly then use the below timer() syntax.  

     ```
     Observable.timer(period, TimeUnit.SECONDS, Schedulers.io())
     Observable.timer(1, TimeUnit.SECONDS, Schedulers.io())
     ```