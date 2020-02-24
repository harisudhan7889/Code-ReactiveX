## Conditional and Boolean operators

These operators are easy to understand.

* [all](#all-operator) 
* [amb](#amb-operator) 
* [ambArray](#ambarray-operator) 
* [contains](#contains-operator)
* [defaultIfEmpty](#defaultifempty-operator)
* [sequenceEqual](#sequenceequal-operator)
* [skipUntil](#skipuntil-operator)
* [takeUntil](#takeuntil-operator)
* [skipWhile](#skipwhile-operator)
* [takeWhile](#takewhile-operator)


### all Operator

This operator check whether all items emitted by an Observable meet some criteria and emits boolean value to the
observer.

```
Observable.just(0, 1, 2, 3, 4, 0, 6, 0)
            .all { item -> item > 10 }
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(isConditionSatisfied: Boolean) {
                    println("onSuccess $isConditionSatisfied")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(e: Throwable) {
                    println("onError ${e.message}")
                }
            })
```

**Output**

```
onSubscribe
onSuccess false
```

The above code emits false since none of the emitted items are greater than 10.

### amb operator
amb prefix is a short-hand for “ambiguous”. When you pass a number of source Observables to amb(), it will emit exactly one of these Observables that first sends a notification to Amb. 
Notification can be either item or sending an onError or onCompleted notification. Amb will ignore and discard the emissions and notifications 
of all of the other source Observables.

Let us see a basic example first.

```
val observable1 = Observable.timer(3, TimeUnit.SECONDS).flatMap {
            Observable.just(1, 2, 3)
        }
        val observable2 = Observable.timer(2, TimeUnit.SECONDS).flatMap {
            Observable.just(4, 5, 6)
        }

        Observable.amb(listOf(observable1, observable2)).subscribe(object : Observer<Int> {
            override fun onComplete() {
                println("onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                println("onSubscribe")
            }

            override fun onNext(value: Int) {
                println("onNext $value")
            }

            override fun onError(e: Throwable) {
                println("onError ${e.message}")
            }
        })
```

**Output**

```
onSubscribe
onNext 4
onNext 5
onNext 6
onComplete
```

From the output it is clear that observable1 is discarded because it will start its emission 1 second after the 
observable2. So amb() operator accepts the first emitted observable2 and discards the emissions from observable1.

**When can I use this in real time?**

Let’s say we have two server (ServerA and ServerB), any of them can give me the response. 
Now user made a request for data, so we hit two network calls to ServerA and ServerB respectively, 
whoever gives the data first, amb() will accept data from that server and unsubscribe the other one.

### ambArray operator

This operator is same as the above, only difference is the way of accepting the input. If you are using `amb()` operator
then you have to put all the observable source into a list and give it as a input to `amb()` just like in the above sample.
For this `ambArray()` operator you can directly mention the observable sources as the input like below syntax.

```
ambArray(observable1, observable2, observable3,...,observableN)
```

### contains operator

This operator determines whether an Observable emits a particular item or not.

```
Observable.just(0, 1, 2, 3, 4, 0, 6, 0)
            .contains(4)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(isValueAvailable: Boolean) {
                    println("Is value available: $isValueAvailable")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(e: Throwable) {
                    println("onError ${e.message}")
                }
            })
```

**Output**

```
onSubscribe
Is value available: true
```

### defaultIfEmpty operator

This operator emits items from the source Observable, or a default item if the source Observable emits nothing.

```
Observable.just(1, 3, 5, 7, 9, 11, 13)
            .filter(object : Predicate<Int>{
                override fun test(item: Int): Boolean {
                    return (item % 2 == 0)
                }
            }).defaultIfEmpty(-1)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(e: Throwable) {
                    println("onError ${e.message}")
                }
            })
```

**Output**

```
onSubscribe
onNext -1
onComplete
```

If you see the above code, you can understand what I tried. The observable emits only odd numbers to the filter operator so when these
values are filtered with a condition (item % 2) then no values will be emitted to observer. This is situation where we can use 
`defaultIfEmpty` operator to inject a value -1.

### sequenceEqual operator

This operator determines whether two Observables emit the same sequence of items.

```
val observable1 = Observable.just(1, 3, 5, 7, 9, 11, 13)
        val observable2 = Observable.just(1, 3, 5, 7, 9, 11, 13)
        Observable.sequenceEqual<Int>(observable1, observable2).subscribe(object : SingleObserver<Boolean> {
            override fun onSuccess(isSequenceEqual: Boolean) {
                println("Is both observerable streams are equal: $isSequenceEqual")
            }

            override fun onSubscribe(d: Disposable) {
                println("onSubscribe")
            }

            override fun onError(e: Throwable) {
                println("onError ${e.message}")
            }
        })
```

**Output**

```
onSubscribe
Is both observerable streams are equal: true
```

### skipUntil operator

Discards items emitted by an First Observable until a second Observable emits an item.

```
val observable1 = Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                for (i in 0..5) {
                    Thread.sleep(1000)
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
        })

        val observable2 = Observable.timer(3, TimeUnit.SECONDS)
        
        observable1.skipUntil(observable2)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
```

**Output**

```
onSubscribe
onNext 2
onNext 3
onNext 4
onNext 5
onComplete
```

**Analysis**

Internally the skipUntil operator subscribes to the passed in observable (in our case it is observable2) in order to recognize the emission of its first value. 
When this happens, the operator unsubscribes from the observable2 and starts emitting the values of the source observable(observable1). 
It will never let the source observable(observable1) emit any values if the notifier(observable2) completes or throws an error without emitting a value before.

### takeUntil operator

This is just apposite to `skipUntil()` operator. 
This operator discards items emitted by an Observable after a second Observable emits an item or terminates.

```
val observable1 = Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                for (i in 0..5) {
                    Thread.sleep(1000)
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
        })

        val observable2 = Observable.timer(3, TimeUnit.SECONDS)

        observable1.takeUntil(observable2)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
```

**Output**

```
onSubscribe
onNext 0
onNext 1
onComplete
```

### skipWhile operator

This operator will discard items emitted by an Observable until a specified condition becomes false.

```
 Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .skipWhile(object : Predicate<Int> {
                override fun test(emittedValue: Int): Boolean {
                    return emittedValue <= 5
                }
            }).subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
```

**Output**

```
onSubscribe
onNext 6
onNext 7
onNext 8
onNext 9
onNext 10
onComplete
```

### takeWhile operator

This operator will discard items emitted by an Observable after a specified condition becomes false.

```
Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .takeWhile(object : Predicate<Int> {
                override fun test(emittedValue: Int): Boolean {
                    return emittedValue <= 5
                }
            }).subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
```

**Output**

```
onSubscribe
onNext 1
onNext 2
onNext 3
onNext 4
onNext 5
onComplete
```