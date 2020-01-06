## Error And Exception Handling:

Before going into the topic first of all we have to understand what is the difference between error
and exception.

Both Errors and Exceptions are the subclasses of java.lang.Throwable class.

### Error:
An Error “indicates serious problems that a reasonable application should not try to catch. 
Errors belong to unchecked type and mostly occur at runtime.

1. AnnotationFormatError - can be handled
2. AssertionError - can be handled
3. LinkageError - can be handled
4. VirtualMachineError - OutOfMemoryError and StackOverFlowError comes under
VirtualMachineError which indicate that the Java Virtual Machine is broken or has run out of resources necessary for it to continue operating.

### Exception
All the exceptions can be handled. Exceptions include both checked as well as unchecked type.

1. Checked Exceptions - SQLException, IOException
2. Unchecked Exceptions - ArrayIndexOutOfBoundException, NullPointerException, ArithmeticException, etc

RxJava communicates unrecoverable errors by notifying the Observable sequence with an onError notification. This also terminates the sequence.
Sometimes, you don’t want your sequence to terminate. In those instances, RxJava offers a number of ways to handle errors without terminating your sequence.

* [onExceptionResumeNext](#onexceptionresumenext)
* [onErrorResumeNext](#onerrorresumenext)
* [onErrorReturnItem](#onerrorreturnitem)
* [onErrorReturn](#onerrorreturn)
* [retryWhen](#retrywhen)

### onExceptionResumeNext
If we encounter any exception in the observable chain(before they make their way to the observer methods), we can use this method to plug in another observable.
    
```
Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw RuntimeException("Exception on $it")
                    }
                }.onExceptionResumeNext(Observable.just(10))
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("$value")
                    }

                    override fun onError(e: Throwable) {
                        println("onError $e")
                    }
                })
```

**Output**
```
onSubscribe
1
10
onComplete
```

**Code Analysis**

To showcase this scenario, in the above code snippet whenever value 2 is 
emitted to doOnNext(), I am just throwing an exception. And this exception is 
handled using onExceptionResumeNext, where I am injecting another observable that emits value 10.
If I didn't use onExceptionResumeNext then onError would be called by terminating both upstream and downstream.

**In realtime when to use this operator?**

One of the use cases of this can be a fall back mechanism. 
Suppose you want to get the latest data from an API and 
if there is an error (suppose network connection is down), 
you can plug in an observable that takes data from your local database. 
This way, you are still showing data.


### onErrorResumeNext

It is quite similar to the onExceptionResumeNext but it is for error. 
As I said earlier, this can’t handle VirtualMachineError like OutOfMemoryError and 
StackOverFlowError. Other errors like AnnotationFormatError, AssertionError, 
LinkageError can be handled. 

```
Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw AnnotationFormatError("Error")
                    }
                }.onErrorResumeNext(Observable.just(10))
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("$value")
                    }

                    override fun onError(e: Throwable) {
                        println("onError $e")
                    }
                })
```

**Output**
```
onSubscribe
1
10
onComplete
```

If you see the outputs of the onErrorResumeNext and onExceptionResumeNext 
both stops the stream without continuing the remaining. 
This operator repairs the downstream sequence, but terminates the upstream.

Therefore, if you were connected to a Subject that was publishing notifications, 
an onError notification would terminate the Subject. 

If you wish to keep the upstream running, nest the Observable with 
the onErrorResumeNext/onExceptionResumeNext operator inside a 
flatMap/switchMap operator like below

```
Observable.just(1, 2, 3, 5, 6)
                .switchMap {
                    Observable.just(it).doOnNext { value ->
                        if (value == 2) {
                            throw AnnotationFormatError("Error")
                        }
                    }.onErrorResumeNext(Observable.just(23))
                }
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("$value")
                    }

                    override fun onError(e: Throwable) {
                        println("onError $e")
                    }
                })
```

**Output**
```
onSubscribe
1
10
3
5
6
onComplete
```

### onErrorReturnItem

As the name suggests, it just returns a value if an error is encountered.

```
Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw AnnotationFormatError("Error")
                    }
                }.onErrorReturnItem(-1)
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("$value")
                    }

                    override fun onError(e: Throwable) {
                        println("onError $e")
                    }

                })
```

**Output**

```
onSubscribe
1
-1
onComplete
```

**Code Analysis**

In the above code you can notice that onErrorReturnItem() returns a integer instead of an Observable.

### onErrorReturn

Sometimes you might need to return a value based on the exception at such situations you can make use of onErrorReturn.

```
Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw AnnotationFormatError("Error")
                    }
                }.onErrorReturn(object : Function<Throwable, Int>{
                override fun apply(error: Throwable): Int {
                    print(error.message)
                    return if(error is AnnotationFormatError) {
                        -1
                    } else {
                        -2
                    }
                }
            })
            .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("$value")
                    }

                    override fun onError(e: Throwable) {
                        println("onError $e")
                    }
                })
```

**Output**

```
onSubscribe
1
-1
onComplete
```

**Code Analysis**

In the above code inside onErrorReturn block, I have returned -1 if the error is of type AnnotationFormatError otherwise returned -2.

### retryWhen
This is one of the error handling mechanism. If you implemented this operator then whenever error occurs 
the observable resubscribes. Most often everyone will get confused on operators repeatWhen() and retryWhen().

repeatWhen() resubscribes when it receives onComplete() 

retryWhen() resubscribes when it receives onError()
```
   fun retryWhen() {
        val firstLocation = DoubleArray(2)
        firstLocation[0] = 9.925201
        firstLocation[1] = 78.119774

        val secondLocation = DoubleArray(2)
        secondLocation[0] = 13.082680
        secondLocation[1] = 80.270721

        val thirdLocation = DoubleArray(2)
        secondLocation[0] = 10.800820
        secondLocation[1] = 78.689919

        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)

        val observable1 = endPoint.getRestaurantsAtLocation(firstLocation[0], firstLocation[1], 0, 3)

        val observable2 = endPoint.getRestaurantsAtLocation(secondLocation[0], secondLocation[1], 0, 3)

        val observable3 = endPoint.getRestaurantsAtLocation(thirdLocation[0], thirdLocation[1], 0, 3).doOnNext {
            throw UnknownHostException()
        }

        observable1
                .mergeWith(observable2)
                .mergeWith(observable3)
                .retryWhenErrorOccurs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        println("onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        println("onNext ${restaurants.restaurants.size}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        println("onError $error")
                    }
                })
    }      
                
    private fun Observable<Restaurants>.retryWhenErrorOccurs(): Observable<Restaurants> {
        return retryWhen { errors ->
            errors.zipWith(Observable.range(1, 3), object: BiFunction<Throwable, Int, Boolean>{
                override fun apply(error: Throwable, count: Int): Boolean {
                    print("retry When count $count")
                    return error is UnknownHostException
                }
            })
        }
    }
```

**Output**

```
onSubscribe
onNext 3
onNext 3
count 1
onNext 3
onNext 3
count 2
onNext 3
onNext 3
count 3
onComplete
```

**Code Analysis**

Let's see what I have coded above one by one

1. I have used 3 observables and in the 3rd observable you can notice that I have injected an exception to check how
retryWhen works.

2. Used merge operator to merge the three observables.

3. I have created a kotlin's extension function `retryWhenErrorOccurs()`. This is the place where I used
retryWhen.

4. In the `retryWhenErrorOccurs` function you can see that I have used `zipWith` inside `retryWhen`.

5. `zipwith` merge the `Observable<Throwable>` with `Observable.range`. 

6. `Observable<Throwable>` is the input for `retryWhen` and `Observable.range` is 
like a counter for retry. For example here I have given range(1, 3) so retry will happen for 3 times. 

7. Retry will happens only if the error is of type `UnknownHostException`.

8. From the output it is clearly evident that the first 2 observables will also be retried  



















































































































































































































































































































































































 
