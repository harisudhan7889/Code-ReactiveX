## Filter Operators:

Filter operators are the straight forward and  easy to understand

* [debounce](#debounce-operator)
* [distinct](#distinct-operator)
* [elementAt](#elementat)
* [filter](#filter)
* [ignoreElements](#ignoreelements)
* [skip](#skip)
* [skipLast](#skiplast)
* [take](#take)
* [takeLast](#takelast)

### debounce Operator

Only emit an item from an Observable if a particular timespan has passed without it emitting another item.

Let us see what does the above definition implies. 
It’s difficult to put this definition in words. I’ll just go ahead and give a simple example. Let’s say the debounce interval is 200ms. The observable emits an item. The debounce operator takes this item and keeps it. The operator will set a timer to 200ms and start the countdown. 200ms has passed, debounce will send this item to the observer. Observable emits another item. The operator will reset the timer to 200ms and start countdown.

Let’s say, the observable emits another item in 100ms. The debounce operator will get the item and it will reset the timer to 200ms and start the countdown. And probably the older item is discarded (I don’t know. I’m just assuming). Once the countdown completes, the operator will send the item to the observer.

**Realtime Use case:**

Let’s say we are implementing a search feature in Android. 
Whenever a user enters a character, we would need to fetch the list of items corresponding to that character. 
If the user enters 10 characters, and if we are fetching the data from a backend api, then that would mean 10 api calls to the backend. 
With the debounce() operator, we can specify the wait time (for instance, 2 seconds). Then the Observable, will wait 2 seconds every time the user enters a character in the EditText. 
If the user types another character before the 2 seconds are up, then the Observable waits another 2 seconds. If the user does not enter another character at the end of 2 seconds, the rest api is called.

### distinct Operator

This operator removes duplicate items emitted by an Observable.

```
Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .distinct()
            .subscribe(observer)
```

**Output**

```
onSubscribe
onNext 10
onNext 20
onNext 30
onNext 40
onNext 70
onNext 60
onComplete
```

**Analysis:**

In the above output you can see that duplicate values are removed. 

### elementAt

**elementAt(index: Long):**

This operator retrieves the value at the mentioned index.

```
Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .elementAt(3)
            .subscribe(object : MaybeObserver<Int>{
                override fun onSuccess(value: Int) {
                    println("onSuccess $value")
                }

                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(error: Throwable) {
                    println("onError ${error.message}")
                }
            })
```

**Output**

```
onSubscribe
onSuccess 10
onComplete
```

**Analysis**

The above printed output will gives you the complete picture that `elementAt(3)` retrieves the value 10.
The reason why `elementAt(index: Long)` expects `MaybeObserver` to subscribe is because suppose the index
is not within the no of items to be emitted then no value will be emitted and directly onComplete of
the observer will be called.


**elementAtOrError(index: Long):**

If you want to print error when the index is not within the no of items to be emitted then you can use 
`elementAtOrError(index: Int)` like below

```
Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .elementAtOrError(10)
            .subscribe(object : SingleObserver<Int>{
                override fun onError(error: Throwable) {
                    println("onError $error")
                }

                override fun onSuccess(value: Int) {
                    println("onSuccess $value")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }
            })
```

**Output**

```
onSubscribe
onError java.util.NoSuchElementException
```

**elementAt(index: Long, defaultItem: Int):**

If you want to emit a default value when the index is not within the no of items to be emitted then you can use 
`elementAtOrError(index: Long, defaultItem: Int)` like below

```
Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .elementAt(10, 90)
            .subscribe(object : SingleObserver<Int>{
                override fun onError(error: Throwable) {
                    println("onError $error")
                }

                override fun onSuccess(value: Int) {
                    println("onSuccess $value")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }
            })
```

**Output**

```
onSubscribe
onSuccess 90
```

### filter

Filters items emitted by an ObservableSource by only emitting those that satisfy a specified condition.

```
Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .filter(object : Predicate<Int> {
                override fun test(input: Int): Boolean {
                    return (input % 3 == 0)
                }
            }).subscribe(object: Observer<Int> {
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
onNext 3
onNext 6
onNext 9
onComplete
```

**Analysis**

The condition is inside the Predicate's test function. Only if the emitted item is divisible
by 3 then the result is emitted to the observer. 

### ignoreElements

This operator does not emit any items from an Observable but mirrors its termination notification 
(either onComplete or onError). If you do not care about the items being emitted by an Observable, 
but you do want to be notified when it completes or when it terminates with an error, 
you can apply the ignoreElements() operator to the Observable

```
Observable.range(0, 10)
            .ignoreElements()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
```

**Output**

```
onSubscribe
onComplete
```

**When can be used in real time?**

Let us assume  doing a PUT request to update the remote database. 
What most server API design does in this case is just respond with the updated object when successful. 
It’s essentially just giving us back what we already sent, if it was successful. 
What we want is to just be notified that the update is succeeded. Meaning, we just want the onComplete event.
In this case we can use `ignoreElements()` to ignore the unwanted updated data.

### skip

skip(n) operator skips the first n items emitted by an Observable.

```
Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .skip(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
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
onNext E
onNext F
onNext G
onNext H
onNext I
onNext J
onComplete
```

**Analysis**

The above code demonstrates the use of skip() operator. 
We have an Observable that emits the first 10 alphabets and if skip(4) operator is used, 
it skips the first 4 alphabets from the list and emits only the remaining 6 items.

### skipLast

`skipLast(n)` operator suppresses the last n items emitted by an Observable.

```
Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .skipLast(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
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
onNext A
onNext B
onNext C
onNext D
onNext E
onNext F
onComplete
```

**Analysis**

The above code demonstrates the use of skipLast() operator. 
We have an Observable that emits the first 10 alphabets and if skipLast(4) operator is used, 
it skips the last 4 alphabets from the list and emits only the remaining 6 items.

### take

take(n) operator is the exact opposite of Skip. It emit only the first n items emitted by an Observable.

```
Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .take(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
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
onNext A
onNext B
onNext C
onNext D
onComplete
```

### takeLast

takeLast(n) operator is the exact opposite of skipLast. It emit only the last n items emitted by an Observable.

```
Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .takeLast(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
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
onNext G
onNext H
onNext I
onNext J
onComplete
```