## Mathematical Operators:
These are very simple operators used to do mathematical operations. 

* [averageDouble](#averagedouble)
* [averageFloat](#averagefloat)
* [max](#max)
* [min](#min)
* [sumDouble](#sumdouble)
* [sumFloat](#sumfloat)
* [sumInt](#sumint)
* [sumLong](#sumlong)

### averageDouble

```
fun averageDouble() {
          MathObservable.averageDouble(Observable.just(1, 2, 3))
                .subscribe(object : Observer<Double> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(average: Double) {
                        System.out.println("Average of Double Values is $average")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Average of Double Values is 2.0
onComplete
```

### averageFloat

```
fun averageFloat() {
         MathObservable.averageFloat(Observable.just(1, 2, 3))
                .subscribe(object : Observer<Float> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(average: Float) {
                        System.out.println("Average of Float Values is $average")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Average of Float Values is 2.0
onComplete
```

### max

```
fun max() {
        MathObservable.max(Observable.just(10, 23, 45, 5, 3))
                .subscribe(object : Observer<Int>{
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(maxValue: Int) {
                        System.out.println("Maximmum Value is $maxValue")
                    }

                    override fun onError($error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Maximmum Value is 45
onComplete
```

### min

```
fun min() {
        MathObservable.min(Observable.just(10, 23, 45, 5, 3))
                .subscribe(object : Observer<Int>{
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(minValue: Int) {
                        System.out.println("Minimmum Value is $minValue")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Minimmum Value is 3
onComplete
```

### sumDouble
```
fun sumDouble() {
        MathObservable.sumDouble(Observable.just(1.0, 2.0, 3.0))
                .subscribe(object : Observer<Double> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(sum: Double) {
                        System.out.println("Sum of Double Values is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Sum of Double Values is 6.0
onComplete
```

### sumFloat
```
fun sumFloat() {
        MathObservable.sumFloat(Observable.just(1F, 2F, 3F))
                .subscribe(object : Observer<Float> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(sum: Float) {
                        System.out.println("Sum of Float Values is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Sum of Float Values is 6.0
onComplete
```

### sumInt
```
fun sumInt() {
        MathObservable.sumInt(Observable.just(1, 2, 3))
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(sum: Int) {
                        System.out.println("Sum of Integer Values is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Sum of Integer Values is 6
onComplete
```

### sumLong
```
fun sumLong() {
        MathObservable.sumLong(Observable.just(100098989898, 27867878787, 2323344545))
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(sum: Long) {
                        System.out.println("Sum of Long Values is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
Sum of Long Values is 130290213230
onComplete
```

## Aggregate Operators:

Below are the aggregate operators.

* [count](#count)
* [reduce](#reduce)
* [reduceWith](#reducewith)
* [toList](#tolist)

### count
```
fun count() {
        Observable.just(2, 3, 5, 6, 7)
                .count()
                .subscribe(object : SingleObserver<Long> {
                    override fun onSuccess(noOfItems: Long) {
                        System.out.println("No of items emitted $noOfItems")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```
**Output**
```
onSubscribe
No of items emitted 5
```

### reduce
We already discussed about **reduce** operator with multiple examples at [transform operators](../transformoperators/README.md) have a look at it.
Generally this operator reduces the values from source observable to a single value that's emitted when the source completes. 

### reduceWith
`reduceWith` will also has the same functionality implementation as `reduce` but there is a extra feature
added with it. If you see the syntax of `reduce(initialValue, reducer: BiFunction)` it is much clear that
it has a way to send a initial value. Let us take a scenario where we have to do a remote network call or a DB call
fetch the initial value then `reduceWith` is the correct operator to do that job.

**Basic Example**

```
fun reduceWith() {
        Observable.just(1, 2, 3, 4, 5)
                .reduceWith(object : Callable<Int>{
                    override fun call(): Int {
                        return 9
                    }
                }, object : BiFunction<Int, Int, Int> {
                    override fun apply(previousResult: Int, currentValue: Int): Int {
                       return previousResult + currentValue
                    }
                }).subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(totalValue: Int) {
                        System.out.println("onSuccess Total value is $totalValue")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
    }
```

**Output**
```
onSubscribe
onSuccess Total value is 24
```

**Code Analysis**
1. This is the very basic example to show from where we get the initial value.   
2. In the above code sample you can see value **9** is supplied by a call() function.
We can make use of this function to do asynchronous calls using the background thread to get the
initial value.
3. So the BiFunction will be called only after Initial value is ready. 
4. From the above scenario following will processing steps

   1. Initial value 9 + Current Value 1
   2. Previous Result 10 + Current Value 2
   3. Previous Result 12 + Current Value 3
   4. Previous Result 15 + Current Value 4
   5. Previous Result 19 + Current Value 5
   
5. Final value is 5 so it stops here and the final result (19 + 5 = 24) will be emitted to the observer.   
 
Let us see a example with a remote network call to fetch the initial value. 
The below sample is the same one that I have used for `reduce` operator in 
[transform operators](../transformoperators/README.md). So if you have a look at 
that example you can easily understand how `reduceWith` operator is used here?.

```
fun reduceWith() {
        val countries = AppUtils.getWorldCupWinners()
        Observable.fromIterable(countries)
                .reduceWith(object : Callable<WinningCount> {
                    override fun call(): WinningCount {
                        return getInitialWCWinner()
                    }
                }, object : BiFunction<WinningCount, WinningCount, WinningCount> {
                    override fun apply(accumulator: WinningCount, newObject: WinningCount): WinningCount {
                        if (accumulator.winningCounts.containsKey(newObject.countryName)) {
                            val winningCount = accumulator.winningCounts[newObject.countryName] ?: 0
                            accumulator.winningCounts[newObject.countryName] = winningCount.inc()
                        } else {
                            accumulator.winningCounts[newObject.countryName] = 1
                        }
                        return accumulator
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<WinningCount> {
                    override fun onSuccess(accumulator: WinningCount) {
                        accumulator.winningCounts.forEach {
                            System.out.println("onSuccess: No of times ${it.key} had won the Cricket World Cup is ${it.value}")
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        System.out.println("onError $e")
                    }

                })
    }
```

**Output**
```
onSubscribe
onSuccess: No of times Pakistan had won the Cricket World Cup is 1
onSuccess: No of times Sri Lanka had won the Cricket World Cup is 1
onSuccess: No of times West Indies had won the Cricket World Cup is 2
onSuccess: No of times Australia had won the Cricket World Cup is 5
onSuccess: No of times India had won the Cricket World Cup is 2
```

**Code Analysis**

1. This code is already analysed much, the only difference here is introducing the initial value supplier.
2. Consider `getInitialWCWinner()` as a network call that gives us a initial value. This method is placed
inside the `call()` - which is called as initial value supplier function.

### toList

This operator is pretty straight forward and simple to understand. 
Collect all items from an Observable and emit them as a single List.

