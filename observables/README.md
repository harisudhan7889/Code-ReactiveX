## Observable and Observer Types:

The following are the different types of Observables in RxJava:

1. [Observable](#observable)
2. [Flowable](#flowable)
3. Single
4. Maybe
5. Completable

As there are different types of Observables, there are different types of Observers also.

1. [Observer](#observer)
2. SingleObserver
3. MaybeObserver
4. CompletableObserver

Let’s see how they are different and when to use which one. 

### Observable 

This is the simplest observable which will emit values. 
This Observable can be used in a situation when you want the observable to emit more values.
Most probably everyone when they learn to code ReactiveX, will be starting
with this observable.

### Observer
If you are using this Simple Observable type then you have to use Simple Observer type.

```
       Observable.just(1, 2, 3)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(result: Int) {
                    System.out.println("onNext: $result")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }
            })
```
**Ouput**
```
onSubscribe
onNext: 1
onNext: 2
onNext: 3
onComplete
```
In the above example, I have used a Simple Observable and Simple Observer.
So in this example you can see that the `Simple Observer` have four methods within it.

   1. onSubscribe
   2. onNext
   3. onError
   4. onComplete
   
### Flowable 
`Flowable` and `Observable` are same from user standpoint but Flowable's inner implementation differs to handle BackPressure.
Flowable comes to picture when there is a case that the Observable is emitting huge numbers of values which can’t be consumed by the Observer.  

**What is BackPressure?**

BackPressure is the process of handling a fast item producer. 
If an Observable produces 10000 items per second how a subscriber/operator which can handle only 100 items per second, then `OutOfMemory` exception will be thrown? 
. By applying BackPressure onto a stream, it’ll be possible to handle items as needed.

We have to define a backpressure strategy while creating or converting to Flowable. Lets see how the strategy
works.

**BackpressureStrategy.MISSING:**

  This is not an actual strategy. It implies that Flowable will not implement any backpressure strategy, 
  so you need to explicitly tell Flowable which backpressure strategy to follow.
  It falls back to default which basically buffers upto 128 items in the queue. 
  Hence the output Queue is full.

  ```
  Observable.range(0, 10000)
              .toFlowable(BackpressureStrategy.MISSING)
              .observeOn(Schedulers.computation())
              .subscribe(object : DisposableSubscriber<Int>() {
                  override fun onStart() {
                      System.out.println("onStart")
                  }
  
                  override fun onComplete() {
                      System.out.println("onComplete")
                  }
  
                  override fun onNext(value: Int?) {
                      System.out.println("onNext $value")
                  }
  
                  override fun onError(error: Throwable?) {
                      System.out.println("onError $error")
                  }
              })
  ```
  
  **Output**
  ```
  io.reactivex.exceptions.MissingBackpressureException: Queue is full?!
  ```
  
  **BackpressureStrategy.DROP:**
  
  This Backpressuring strategy will drops the items 
  if it can’t handle more than it’s capacity i.e. 128 items (size of buffer).
   
   ```
    Observable.range(0, 10000)
                .toFlowable(BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())
                .subscribe(object : DisposableSubscriber<Int>() {
                    override fun onStart() {
                        System.out.println("onStart")
                        request(1)
                    }
    
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }
    
                    override fun onNext(value: Int?) {
                        System.out.println("onNext $value")
                        request(1)
                    }
    
                    override fun onError(error: Throwable?) {
                        System.out.println("onError $error")
                    }
                })
   ```
   
   **Output**
   ```
   onStart
   onNext 0
   onNext 1
   onNext 2
   . 
   . 
   onNext 127
   onComplete
   ```
   
   If you see the output, only 128 items gets emitted to the observer 
   and other items got dropped. 
   
   **BackpressureStrategy.LATEST:**
   
   It emits until the buffer is full and waits until it becomes available.
   In this waiting time, it keeps dropping the items except the last one that arrived 
   and sends the last one. 
   
   ```
   Observable.range(0, 10000)
               .toFlowable(BackpressureStrategy.LATEST)
               .observeOn(Schedulers.computation())
               .subscribe(object : DisposableSubscriber<Int>() {
                   override fun onStart() {
                       System.out.println("onStart")
                       request(1)
                   }
   
                   override fun onComplete() {
                       System.out.println("onComplete")
                   }
   
                   override fun onNext(value: Int?) {
                       System.out.println("onNext $value")
                       request(1)
                   }
   
                   override fun onError(error: Throwable?) {
                       System.out.println("onError $error")
                   }
               })
   ```
   **Output**
   ```
   onStart
   onNext 0
   onNext 1
   onNext 2
   . 
   . 
   onNext 126
   onNext 127
   onNext 1468
   onNext 1469
   . 
   . 
   onNext 1563
   onNext 9999
   onComplete
   ```
   In the above output, the producer emits 128 items and waits for the buffer to get free.
   In the mean time it drops other items and finally the last item is preserved.
   
   **BackpressureStrategy.BUFFER:**
   
   **onBackpressureBuffer():**
   
   This operator in its parameterless form `onBackpressureLatest()` introduce an unbounded(no size limit) buffer 
   between the upstream source and the downstream operator. 
   Being unbounded means as long as the JVM doesn't run out of memory, 
   it can handle almost any amount coming from a source. 
   
   **Note:** It might not be the best way to handle a lot of emissions. 
   
   ```
   Observable.range(0, 10000)
               .toFlowable(BackpressureStrategy.MISSING)
               .onBackpressureBuffer()
               .observeOn(Schedulers.computation())
               .subscribe(object : DisposableSubscriber<Int>() {
                   override fun onStart() {
                       System.out.println("onStart")
                       request(1)
                   }
   
                   override fun onComplete() {
                       System.out.println("onComplete")
                   }
   
                   override fun onNext(value: Int?) {
                       System.out.println("onNext $value")
                       request(1)
                   }
   
                   override fun onError(error: Throwable?) {
                       System.out.println("onError $error")
                   }
               })
   ``` 
   
   **Output**
   ```
   onStart
   onNext 0
   onNext 1
   onNext 2
   . 
   .
   onNext 9998 
   onNext 9999
   onComplete
   ```
   
   **onBackpressureBuffer(int capacity):**
   
   This is a bounded version that signals `BufferOverflowError` in case its buffer reaches the given capacity.
   
   ```
   Observable.range(0, 10000)
               .toFlowable(BackpressureStrategy.MISSING)
               .onBackpressureBuffer(16)
               .observeOn(Schedulers.computation())
               .subscribe(object : DisposableSubscriber<Int>() {
                   override fun onStart() {
                       System.out.println("onStart")
                       request(1)
                   }
   
                   override fun onComplete() {
                       System.out.println("onComplete")
                   }
   
                   override fun onNext(value: Int?) {
                       System.out.println("onNext $value")
                       request(1)
                   }
   
                   override fun onError(error: Throwable?) {
                       System.out.println("onError $error")
                   }
               })
   ```
   
   **Output**
   ```
   onStart
   onNext 0
   onNext 1
   onNext 2
   onNext 3
   onNext 4
   onError io.reactivex.exceptions.MissingBackpressureException: Buffer is full
   ```
   
   **onBackpressureBuffer(int capacity, Action onOverflow)**
   
   ```
   Observable.range(0, 10000)
               .toFlowable(BackpressureStrategy.MISSING)
               .onBackpressureBuffer(16, object: Action {
                   override fun run() {
                       System.out.println("Buffer overload causes the overflowing.")
                   }
               })
               .observeOn(Schedulers.computation())
               .subscribe(object : DisposableSubscriber<Int>() {
                   override fun onStart() {
                       System.out.println("onStart")
                       request(1)
                   }
   
                   override fun onComplete() {
                       System.out.println("onComplete")
                   }
   
                   override fun onNext(value: Int?) {
                       System.out.println("onNext $value")
                       request(1)
                   }
   
                   override fun onError(error: Throwable?) {
                       System.out.println("onError $error")
                   }
               })
   ```
   
   **Output**
   ```
   onStart
   onNext 0
   Buffer overload causes the overflowing.
   onNext 1
   onError io.reactivex.exceptions.MissingBackpressureException: Buffer is full
   ```
   
   **onBackpressureBuffer(int capacity, Action onOverflow, BackpressureOverflowStrategy strategy)**
   
   This is more useful as it let us to define what to do in case the capacity has been reached
   using `BackpressureOverflowStrategy`.
   
   1. BackpressureOverflowStrategy.ERROR: this is the default behavior of the previous two overloads, signalling a BufferOverflowException
   
   2. BackpressureOverflowStrategy.DROP_LATEST : if an overflow would happen, the current value will be simply ignored and only the old values will be delivered once the downstream requests.
   
   3. BackpressureOverflowStrategy.DROP_OLDEST : drops the oldest element in the buffer and adds the current value to it.
   
   ```
   Observable.range(0, 10000)
               .toFlowable(BackpressureStrategy.MISSING)
               .onBackpressureBuffer(16, object: Action {
                   override fun run() {
                       System.out.println("Buffer overload causes the overflowing.")
                   }
               }, BackpressureOverflowStrategy.DROP_LATEST)
               .observeOn(Schedulers.computation())
               .subscribe(object : DisposableSubscriber<Int>() {
                   override fun onStart() {
                       System.out.println("onStart")
                       request(1)
                   }
   
                   override fun onComplete() {
                       System.out.println("onComplete")
                   }
   
                   override fun onNext(value: Int?) {
                       System.out.println("onNext $value")
                       request(1)
                   }
   
                   override fun onError(error: Throwable?) {
                       System.out.println("onError $error")
                   }
               })
   ```
   
   **Output**
   ```
   onStart
   onNext 0
   onNext 1
   onNext 2
   onNext 3
   onNext 4
   . 
   . 
   . 
   onNext 66
   onNext 67
   Buffer overload causes the overflowing.
   Buffer overload causes the overflowing.
   onNext 68
   Buffer overload causes the overflowing.
   Buffer overload causes the overflowing.
   onNext 71
   Buffer overload causes the overflowing.
   onNext 74
   . 
   . 
   onNext 9734
   onNext 9999
   onComplete
   ```
  
  If you see all the `Flowable` examples, I have used `DisposableSubscriber` instead of `Observer`(that is used for Observable). 
  Since Observables don't have backpressure support, there is no need for a Subscription with a request() method.
  In the above `Flowable` examples, you can notice the `request(1)` method invocation in `onStart()` and `onNext()`. 
  Under `BackPressure` condition, consumer has to request the producer to emit the next item/items. 
  Based on the number of items mentioned in the `request(long)`, producer emits the items.
  
  In our example, I have requested for one item at the start and after that one item got emitted to `onNext()`, 
  I have requested for the another one. 
  
  `request(n)` - n should be greater than 0, otherwise following exception will be the thrown.
  ```
  java.lang.IllegalArgumentException: n > 0 required but it was 0
  ```