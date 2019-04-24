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
Through this operators we can create a basic observables, 
so these operators will be coming under operator category of **Creating Observables**.

#### Observable.create():
   
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
  
#### Observable.just():

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

                    }

                    @Override
                    public void onNext(String[] strings) {
                        System.out.println("onNext: " + Arrays.toString(strings));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
```

**Actual Definition:** The Just operator converts an item into an Observable that emits that item.

#### Observable.defer(): 

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

#### Observable.fromArray(): 

This operator creates an Observable with array of items as an input. The created Observable is capable
of emitting each item one at a time.

```
val valueArray: Array<String> = arrayOf("A", "B", "C", "D", "E", "F")
Observable.fromArray(*valueArray)
                .subscribe(object : Observer<String?> {

                    override fun onComplete() {
                        Log.d("fromArray", "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d("fromArray", "onSubscribe")
                    }

                    override fun onNext(value: String) {
                        Log.d("fromArray", "onNext $value")
                    }

                    override fun onError(error: Throwable) {
                        Log.d("fromArray", "onError $error")
                    }
                })
``` 
