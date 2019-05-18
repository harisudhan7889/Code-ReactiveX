## Transform Operators:


* [buffer](#buffer-operator)
* [map](#map-operator)
* [flatMap](#flatmap-operator)
* [concatMap](#concatmap-operator)
* [switchMap](#switchmap-operator)
* [Difference between all map operators](#difference-between-all-map-operators)
* [groupBy](#groupby-operator)
* [scan](#scan-operator)
* [reduce](#reduce-operator)

### buffer Operator

**Actual Definition:**

This operator periodically gather items from an Observable into bundles and emit 
these bundles rather than emitting the items one at a time.

```
Observable.just("1", "2", "3", "4", "5", "6")
            .buffer(2)
            .subscribe(object : Observer<List<String>>{
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(t: List<String>) {
                    System.out.println("onNext: ")
                    t.forEach {
                        System.out.println(it)
                    }
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }

            })
```

**Output**
```
onSubscribe
onNext: 
1
2
onNext: 
3
4
onNext: 
5
6
onComplete
```

**Code snippet analysis:**

If you see the above sample implementation, **Observerable.buffer()** is specified with count - 2 which means buffer
will gathers 2 items and emit those 2 items at a time as a `List<T>` 

**Note:** buffer count should be always greater than 0, otherwise exception will be thrown like below

`java.lang.IllegalArgumentException: count > 0 required but it was 0`

### map Operator

**Actual Definition:**

Transform the items emitted by an Observable by applying a function to each item and returns the modified items.

```
val valueArray: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6)
        Observable.fromArray(*valueArray)
            .map(object : Function<Int, String>{
                override fun apply(value: Int): String {
                    return if (value % 2 == 0) {
                        "$value is a Even Number"
                    } else {
                        "$value is a Odd Number"
                    }
                }
            })
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(value: String) {
                    System.out.println("onNext: $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }
            })
```
**Output**
```
onSubscribe
onNext: 1 is a Odd Number
onNext: 2 is a Even Number
onNext: 3 is a Odd Number
onNext: 4 is a Even Number
onNext: 5 is a Odd Number
onNext: 6 is a Even Number
onComplete
```
**Code snippet analysis:**

From the above implementation you can easily understand what map operator does?
`Observable.fromArray(arrayOf(Integer, Integer,..))` will emit Integer item one by one.
`map()` operator receives those items one by one and apply a function 
(here i have applied a sample function to check whether numbers are even or odd) to it and transform the item to another form/type.
These transformed items are received by the observer.

**In realtime when to use this operator?**

  When you have to get a remote response and provide an input to a view. 
  But the response object structure is different from the view's expected input structure, at this 
  situation you can use `map()` operator to convert response object model to the expected model.
  
  ```
  getUserDetail()
      .map(new Function<ApiUser, User>() {
          @Override
          public User apply(ApiUser apiUser) throws Exception {
              // here we get the ApiUser from the server
              User user = new User(apiUser);
              // then by converting it into the user, we are returning
              return user;
          }
      })
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(object : Observer<User> {
                      override fun onComplete() {
                          System.out.println("onComplete")
                      }
      
                      override fun onSubscribe(d: Disposable) {
                          System.out.println("onSubscribe")
                      }
      
                      override fun onNext(user: User) {
                          view.setValue(user)
                      }
      
                      override fun onError(error: Throwable) {
                          System.out.println("onError $error")
                      }
                  })
  ```
  
  Here, the observable gives us ApiUser object which we are converting into User object by using the map operator.
  
  Now you can notice the `apply(ApiUser apiUser)` function of the `map()` 
  operator returning the modified item `User` instead of an `Observable<User>`.
  
### flatMap Operator

**Actual Definition:**

FlatMap also applies a function on each emitted item but instead of returning the modified item, 
it returns the Observable itself which can emit data again, So it is used to 
map over asynchronous operations.

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val observable = endPoint.getRestaurantsAtLocation(latitude, longitude)
        observable
            .flatMap { Observable.fromIterable(it.restaurants) }
            .flatMap(object : Function<RestaurantObject, Observable<UserReviewsObject>>{
            override fun apply(restaurantObject: RestaurantObject): Observable<UserReviewsObject> {
               return endPoint.getRestaurantReview(restaurantObject.restaurant.id)
            }})
            .map(object : Function<UserReviewsObject, List<UserReviews>>{
            override fun apply(userReviewsObject: UserReviewsObject): List<UserReviews>? {
                return userReviewsObject.userReviews
            }})
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<UserReviews>?> {
                override fun onComplete() {
                    System.out.println("onComplete")
                    progressBar.dismiss()
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                    progressBar.show()
                }

                override fun onNext(userReviews: List<UserReviews>) {
                    System.out.println("onNext No of Reviews: ${userReviews.size}")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }
            })                
```
**Output**
```
onSubscribe
onNext No of Reviews: 3
onNext No of Reviews: 5
onNext No of Reviews: 7
.
.
onNext No of Reviews: 8
onComplete
```
**Code snippet analysis:**

In the above implementation, I have called two dependent network services. 
First network call `getRestaurantsAtLocation(latitude, longitude)` is to get the restaurants details in your location and with 
these restaurants details an another network api `getRestaurantReview(restaurant.id)` to fetch the user reviews
for the respective restaurant is called. Will explain it more clear.

1. `getRestaurantsAtLocation(latitude, longitude)` will fetch the List of Restaurants.
2. Each Restaurants are inputed one by one to the `flatMap`.
3. If you notice the above `flatMap` implementation, the `Funtion` 
inside the `flatMap` accepts a Single `RestaurantObject` as an input and
`Observable<UserReviewsObject>` as an output.

   `flatMap(Function<RestaurantObject, Observable<UserReviewsObject>>)` 
   
4. Inside the flatMap an another service to fetch the user reviews is called.
5. Input for the second network call is the Restaurant Id and 
its output is `Observable<UserReviewsObject>`. 
6. I have done one more extra step by adding a `map()` operator at the last. 
This is due to the object structure of the `UserReviewsObject` which contains
`List<UserReviews>` inside it. 

### concatMap Operator

ConcatMap produces the same output as FlatMap but the sequence the data emitted changes.
`concatMap()` maintains the order of items and waits for the current Observable to 
complete its job before emitting the next one.

From the previous implementation. Let us assume the network call 
`getRestaurantsAtLocation(latitude, longitude)` fetch 
us three restaurant details Restaurant1, Restaurant2, Restaurant3. 
With this restaurant details again one more network service `getRestaurantReview(Restaurant.id)` is called.
ConcatMap won’t make simultaneous calls in order to maintain item order. So after Restaurant1 completes its
execution, network call to fetch reviews for Restaurant 2 will be started.

**In realtime when to use this operator?**

  It can be used when you want to maintain the order of execution.
  
### switchMap Operator  

SwitchMap is a bit different from FlatMap and ConcatMap. 
SwitchMap unsubscribe from previous source Observable whenever new item started emitting, 
thus always emitting the items from current Observable. 

After analysing the below code snippet you will understand `switchMap()` more clearly.

```
val valueArray: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6)
        Observable.fromArray(*valueArray)
            .switchMap(object : Function<Int, Observable<String>> {
                override fun apply(input: Int): Observable<String> {
                    val value = if (input % 2 == 0) {
                        "$input is a Even Number"
                    } else {
                        "$input is a Odd Number"
                    }
                    return Observable.just(value).delay(1, TimeUnit.SECONDS)
                }
            }).subscribe(object : Observer<String> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(value: String) {
                    System.out.println("onNext: $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }
            })
```
**Output**
```
onSubscribe
onNext: 6 is a Even Number
onComplete
```

**Code Analysis**

 1. `fromArray()` emits array of integer values.
 2. These integer values are the input for the switchMap.
 3. I will explain what will happen when `fromArray()` emits the value one by one to `switchMap()`.
    ```
    fromArray -emits-> 1 ->switchMap --- Execution 1 in progress 
    fromArray -emits-> 2 ->switchMap --- Unsubscribe Execution 1 and Execution 2 in progress
    fromArray -emits-> 3 ->switchMap --- Unsubscribe Execution 2 and Execution 3 in progress
    fromArray -emits-> 4 ->switchMap --- Unsubscribe Execution 3 and Execution 4 in progress
    fromArray -emits-> 5 ->switchMap --- Unsubscribe Execution 4 and Execution 5 in progress
    fromArray -emits-> 6 ->switchMap --- Unsubscribe Execution 5 and Execution 6 in progress
    
    finally switchMap emits the final executed Observable.
    ```

### Difference between all Map operators


|  |  map | flatMap | concantMap | switchMap |
|---|---|---|---|---|
| Emission Order |The order of insertion is <br/> maintained during emission.| The order of insertion is <br/> not maintained during emission.|The order of insertion is <br/> maintained during emission.| NA|
| Return Type | Modified Item | Observable | Observable | Observable |     
| When can be used?| Consider using map operator where there is an offline operations needs to be done on emitted data. If we got something from server but that doesn’t fulfils our requirement. In that case, Map can be used to alter the emitted data.| Choose flatMap when the order is not important and want to send all the network calls simultaneously. In our case, we have fetched the restaurant details in our location. So in this situation order does not matters.| Choose concatMap when the order is important. If you consider ConcatMap in this scenario, the time takes to fetch the restaurants takes very longer time as the ConcatMap won’t make simultaneous calls in order to maintain item order.|switchMap is best suited when you want to discard the response and consider the latest one. Let’s say you are writing an Instant Search app which sends search query to server each time user types something. In this case multiple requests will be sent to server with multiple queries, but we want to show the result of latest typed query only. For this case, switchMap is best operator to use.|

### groupBy Operator

**Actual Definition:**
Divide an Observable into a set of Observables that each emit a different subset 
of items from the original Observable.

**My Understandings with sample:**

This operator divides an Observable into a set of Observables that 
each emit a different group of items from the original Observable, organised by key.

Let's assume you are creating a Restaurant application, where you have 
to display the Restaurants by grouping them by their major food type. 
For example if Restaurant1 serves Asian foods then it should be under Asian.
This is the situation where you can choose `groupBy()` operator.  

Let us see a sample implementation to understand the `groupBy()` operator clearly.

```
        val progressBar = ProgressDialog(context)
        val cuisineAsian = "Asian"
        val cuisineAmerican = "American"
        val cuisineItalian = "Italian"
        val cuisineOthers = "Other"
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 20)
        val subscribe = observable
            .flatMap { Observable.fromIterable(it.restaurants) }
            .groupBy(object : Function<RestaurantObject, String> {
                override fun apply(restaurantObject: RestaurantObject): String {
                    return when {
                        restaurantObject.restaurant.cuisines.contains("Asian", true) -> cuisineAsian
                        restaurantObject.restaurant.cuisines.contains("American", true) -> cuisineAmerican
                        restaurantObject.restaurant.cuisines.contains("Italian", true) -> cuisineItalian
                        else -> cuisineOthers
                    }
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { 
               System.out.println("OnSubscribe")
               progressBar.show() 
              }
            .doOnComplete { 
               System.out.println("OnComplete")
               progressBar.dismiss() 
              }
            .doOnError { progressBar.dismiss() }
            .subscribe {
                when (it.key) {
                    cuisineAsian -> {
                        it.subscribe {
                            System.out.println("Asian Food available at ${it.restaurant.name}")
                        }
                    }
                    cuisineAmerican -> {
                        it.subscribe {
                            System.out.println("American Food available at ${it.restaurant.name}")
                        }
                    }
                    cuisineItalian -> {
                        it.subscribe {
                            System.out.println("Italian Food available at ${it.restaurant.name}")
                        }
                    }
                }
            }
```
**Output**
```
OnSubscribe
Asian Food available at Coal Barbecues
Asian Food available at VB Signature - Progressive Veg Restaurant
Italian Food available at Onesta
Italian Food available at Fromage
American Food available at Chili's American Grill & Bar
OnComplete
```
**Code Analysis**
1. `getRestaurantsAtLocation()` will fetch the restaurants near your location.
2. My need here is to group the Restaurants under Asian, American and Italian food types.
With the help of `groupBy()` operator I can achieve my need.
3. Fetched restaurants are emitted one by one to `groupBy()`.   
4. The `groupBy()` function will check whether the restaurant 
serving food type is of Asian/American/Italian and returns 
the key under which the restaurants are stored in a Observable.
5. `groupBy()` will divide the main observable to observables for each food type like below.
<br/>

   | Observable | Key | Object |
   |---|---|---|
   | GroupedObservable1 | Asian | RestaurantObject1(Coal Barbecues) |
   | | | RestaurantObject3(VB Signature) |
   |</br> | | |
   | GroupedObservable2 | American | RestaurantObject2(Chili's American Grill & Bar) |
   |</br> | | |
   | GroupedObservable3 | Italian | RestaurantObject6(Onesta) |
   | | | RestaurantObject7(Fromage) |
   
 6. Each observable will emit the group of restaurant items one by one after subscribing.
 
### scan Operator 
**Actual Definition:**
Applies the given `io.reactivex.functions.BiFunction` to a seed value and 
the first item emitted by a reactive source, then feeds the result of that 
function application along with the second item emitted by the reactive source 
into the same function, and so on until all items have been emitted by the 
reactive source, emitting each intermediate result.

**My Definition:**
Operator that transforms each item emitted to it with the help of previous processed result.

**My Understandings with sample:**

```
Observable.just(1,2,3,4,5)
            .scan(object : BiFunction<Int, Int, Int>{
                override fun apply(previousResult: Int, currentValue: Int): Int {
                    return previousResult + currentValue
                }
            })
            .subscribe(object : Observer<Int>{
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(result: Int) {
                    System.out.println("onNext $result")
                }

                override fun onError(e: Throwable) {
                    System.out.println("onError $e")
                }
            })
```
**Output**
```
onSubscribe
onNext 1
onNext 3
onNext 6
onNext 10
onNext 15
onComplete
```
**Code Analysis**
1. This operator uses a function that computes a value based on multiple input values, 
this function is named as `BiFunction`-`BiFunction<Int, Int, Int>`- first two `Int` are the input types
and last one is the return type.
2. In the above code example, the apply function inside `BiFunction` has two parameters.
3. `Observable.just()` will emit the items one by one.
4. First item will not be processed and will emitted to `onNext()`. 
5. Now this emitted result will be the first argument for `BiFunction` 
and the new item emitted from `Observable.just()` will be the second argument.
6. These two input values are computed inside the `apply()`, which returns the processed result to `onNext()`. 
7. Again step 5 and 6 will be repeated until the `Observable.just()` emits the last item.

   
   just()---1-->No computation required ---> onNext(1)
            
   just()---2-->1(previous result)+ 2 ---> onNext(3)

   just()---3-->3(previous result)+ 3 ---> onNext(6)       

   just()---4-->6(previous result)+ 4 ---> onNext(10)

   just()---5-->10(previous result)+ 5 ---> onNext(15)  
   
This is a basic example to understand how `scan()` operator works. But still many of 
them might not be clear at what situation this `scan()` operator can be used in real time. 
Let us see it more clearly.

In general while coding we try to avoid creating any side effects. 
A function is considered to have a side effect if it paved way for any 
unexpected output instead of expected result. 
Generally the side effect will happen due to modification of a state/variable with a wider scope
(i.e. global or static)

Let us see a example to understand this more clear.

The problem statement is to display the winners of cricket 
world cup with their winning count.

**Without using scan()**

```
private var accumulator = WinningCount()

fun getWorldCupWinCounts() {
        val winnersList = getWorldCupWinners()
        val winnersListObservable = Observable.create(object : ObservableOnSubscribe<WinningCount>{
            override fun subscribe(emitter: ObservableEmitter<WinningCount>) {
                        winnersList.forEach {
                            if (accumulator.winningCounts.containsKey(it.countryName)) {
                                val winningCount = accumulator.winningCounts[it.countryName] ?: 0
                                accumulator.winningCounts[it.countryName] = winningCount.inc()
                            } else {
                                accumulator.winningCounts[it.countryName] = 1
                            }
                        }
                        emitter.onNext(accumulator)
                        emitter.onComplete()
                    }
                })
        winnersListObservable.subscribe(observer)
}
``` 

**Output**
```
No of times West Indies had won the Cricket World Cup is 2
No of times India had won the Cricket World Cup is 2
No of times Australia had won the Cricket World Cup is 5
No of times Pakistan had won the Cricket World Cup is 1
No of times Sri Lanka had won the Cricket World Cup is 1
```
While this seems harmless enough until another thread try to access the same method.
In such case, the output will be wrong like below

**Wrong Output**
```
No of times West Indies had won the Cricket World Cup is 4
No of times India had won the Cricket World Cup is 4
No of times Australia had won the Cricket World Cup is 10
No of times Pakistan had won the Cricket World Cup is 2
No of times Sri Lanka had won the Cricket World Cup is 2
```
 This is called side effects. 
 This is because of the global variable `accumulator` that already accumulates the previous results  
 and not yet cleared.
 
 To avoid this, the variable `accumulator` can be moved inside the function. 
 But imagine if by mistake if the Observable is subscribed multiple times like below, will
 results in wrong output.
 
 ```
 fun getWorldCupWinCounts() {
         val winnersList = getWorldCupWinners()
         val accumulator = WinningCount()
         val winnersListObservable = Observable.create(object : ObservableOnSubscribe<WinningCount>{
             override fun subscribe(emitter: ObservableEmitter<WinningCount>) {
                         winnersList.forEach {
                             if (accumulator.winningCounts.containsKey(it.countryName)) {
                                 val winningCount = accumulator.winningCounts[it.countryName] ?: 0
                                 accumulator.winningCounts[it.countryName] = winningCount.inc()
                             } else {
                                 accumulator.winningCounts[it.countryName] = 1
                             }
                         }
                         emitter.onNext(accumulator)
                         emitter.onComplete()
                     }
                 })
         winnersListObservable.subscribe(observer)
         winnersListObservable.subscribe(observer)
 }
 ```
 To avoid these kind of side effects the **state object** should be not shareable.
 We can achieve this by `scan()` operator like below.
 
 ```
 fun getWorldCupWinCounts() {
         val countries = getWorldCupWinners()
         Observable.fromIterable(countries)
             .scan(WinningCount(""), object : BiFunction<WinningCount, WinningCount, WinningCount> {
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
             .subscribe(object : Observer<WinningCount> {
                 override fun onComplete() {
                     System.out.println("onComplete")
                 }
 
                 override fun onSubscribe(d: Disposable) {
                     System.out.println("onSubscribe")
                 }
 
                 override fun onNext(t: WinningCount) {
                     t.winningCounts.forEach {
                         System.out.println("onNext: No of times ${it.key} had won the Cricket World Cup is ${it.value}")
                     }
                 }
 
                 override fun onError(e: Throwable) {
                     System.out.println("onError $e")
                 }
             })
 }
 ```  
 
 In the above code I have not initialized any global 
 or local shareable state object as I did in the previous examples. 
 So by using scan operator we can maintain the state within the scope of the `scan()` 
 by doing this we reduce or remove any side effects like mutating state.
 
 **Code Analysis:**
 
 1. `Observable.fromIterable(countries)` will emit the worldcup winners from list one by one.
 2. This example differs from the previous example with `scan()` operator because in this example
 I have passed a initial value to `scan()`, which I did not in the previous example. 
 3. If we did not pass any initial value, scan() operator will consider the first emitted item as
 the initial value. 
 4. The initial item `WinningCount("")` will not be processed and will emitted directly to `onNext()`.
 This initial value will be working as a accumulator which contains a `HashMap<Country,WinningCount>` where every processed results will stored.
 5. Now this emitted result will be the first argument for `BiFunction` 
 and the new item emitted from `Observable.fromIterable()` will be the second argument. 
 6. If you see inside the `apply()`, you can understand how the computation actually works.
 7. The second argument `newObject` of the `apply` function is the emitted world cup winner.
 8. Check whether the country name is already stored in the `HashMap` inside accumulator, 
 if not add a new entry to the `HashMap` or if already exist, increment the value by one. 
 9. This processed accumulator result will be sent to `onNext()`. 
 10. Again step 5 to 9 will be repeated until the `Observable.fromIterable()` emits the last item.
 
 So now you should be familiar with `scan()` operator and its usage.
 The main advantage of `scan()` is to reduce or remove any side effects like mutating state.
 
        
### reduce Operator 

This operator works same as the `scan()` operator but only difference is `reduce()` 
apply a function to each item emitted by an Observable, sequentially, and emit the final value.

```
Observable.just(1,2,3,4,5)
            .reduce(object : BiFunction<Int, Int, Int>{
                override fun apply(previousResult: Int, currentValue: Int): Int {
                    return previousResult + currentValue
                }
            })
            .subscribe(object : SingleObserver<Int>{
                override fun onSuccess(result: Int) {
                  System.out.println("onSuccess $result")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(result: Int) {
                    System.out.println("onNext $result")
                }

                override fun onError(e: Throwable) {
                    System.out.println("onError $e")
                }
            })
```   

**Output**
```
onSubscribe
onSuccess 15
```
By seeing the outputs of both `scan()` and `reduce()` you can easily understand the
difference between both.
