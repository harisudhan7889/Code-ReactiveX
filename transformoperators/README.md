## Transform Operators:

* [buffer](#buffer-operator)
* [map](#map-operator)
* [flatMap](#flatmap-operator)
* [concatMap](#concatmap-operator)
* [concatMapDelayError](#concatmapdelayerror)
* [switchMap](#switchmap-operator)
* [Difference between all map operators](#difference-between-all-map-operators)
* [groupBy](#groupby-operator)
* [scan](#scan-operator)
* [reduce](#reduce-operator)
* [flatMapCompletable](#flatmapcompletable)
* [concatMapCompletable](#concatmapcompletable)
* [concatMapCompletableDelayError](#concatmapcompletabledelayerror)
* [flatMapMaybe](#flatmapmaybe)
* [concatMapMaybe](#concatmapmaybe)
* [flatMapIterable](#flatmapiterable) 
* [concatMapIterable](#concatmapiterable)
* [window](#window)
* [cast](#cast)
* [flatMapSingle](#flatmapsingle)
* [concatMapSingle](#concatmapsingle)
* [concatMapSingleDelayError](#concatmapsingledelayerror)
* [concatMapEager](#concatmapeager)
* [concatMapEagerDelayError](#concatmapeagerdelayerror)
* [flattenAsObservable](#flattenasobservable)
* [flattenAsFlowable](#flattenasflowable)
* [flatMapObservable](#flatmapobservable)
* [flatMapSingleElement](#flatmapsingleelement)
* [Availability Table](#availability-table)

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
  
### concatMapDelayError
This is same as `concatMap` but the one extra adds-on is 
any errors from the sources will be delayed until all of the results terminate.  

**Note:** [Click here to see the impotant note](#important-note)   
  
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


Have a look at [Obseravable Types](../observables/README.md) 
to understand the below `Transform Operators`. 
 
### flatMapCompletable

`flatMap + Completable = flatMapCompletable`. This type of `flatMap` also applies a function 
on each emitted item which can only emit either a completion or error signal. Suppose if I want to do
a batch update where the result is not expected, then I can make use of this operator.

```
val progressBar = ProgressDialog(context)
val endPoint = Api.getClient().create(ApiEndPoint::class.java)
val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMap { Observable.fromIterable(it.restaurants) }
                .flatMapCompletable(object : Function<RestaurantObject,CompletableSource>{
                    override fun apply(restaurantObject: RestaurantObject): CompletableSource {
                        return endPoint.updateRestaurantDetail(restaurantObject.id, "New Name")
                    }
                }).subscribe(object : CompletableObserver{
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")  
                    }
                })
```

**Output**

```
onSubscribe
onComplete
```

**Code Analysis**

1. In the above code sample, I have got the list of near by restaurants from a remote service.
2. I am iterating the restaurants and these restaurant details are given to `flatMapCompletable`
one by one.
3. You can see the `apply` function inside the `flatMapCompletable`, where the update operation
is done.
4. Though we don't need any result back, `flatMapCompletable` is used here.

### concatMapCompletable
`concatMap + Completable = concatMapCompletable`. ConcatMapCompletable produces the same output as 
FlatMapCompletable but the sequence the data emitted changes. concatMapCompletable() maintains the order of items 
and waits for the current Observable to complete its job before emitting the next one.

```
val progressBar = ProgressDialog(context)
val endPoint = Api.getClient().create(ApiEndPoint::class.java)
val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMap { Observable.fromIterable(it.restaurants) }
                .concatMapCompletable(object : Function<RestaurantObject,CompletableSource>{
                    override fun apply(restaurantObject: RestaurantObject): CompletableSource {
                        return endPoint.updateRestaurantDetail(restaurantObject.id, "New Name")
                    }
                }).subscribe(object : CompletableObserver{
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")  
                    }
                })
```

### concatMapCompletableDelayError
This is same as `concatMapCompletable` but the one extra adds-on is 
any errors from the sources will be delayed until all of them terminate.  

```
val progressBar = ProgressDialog(context)
val endPoint = Api.getClient().create(ApiEndPoint::class.java)
val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMap { Observable.fromIterable(it.restaurants) }
                .concatMapCompletableDelayError(object : Function<RestaurantObject,CompletableSource>{
                    override fun apply(restaurantObject: RestaurantObject): CompletableSource {
                        return endPoint.updateRestaurantDetail(restaurantObject.id, "New Name")
                    }
                }).subscribe(object : CompletableObserver{
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")  
                    }
                })
```

From the above example, let us assume one of the updates got failed and you want to log the error
at the last after all other updates gets completed then you can use `concatMapCompletableDelayError`. 
Errors will be thrown at the last after all other sources completes its process.

**Note:** [Click here to see the impotant note](#important-note) 

### flatMapMaybe
`FlatMap + Maybe = FlatMapMaybe`. FlatMap which has capability to emit **result, no result 
or error result** for each item emitted by a reactive source is called `FlatMapMaybe`. 

```
val progressBar = ProgressDialog(context)
val endPoint = Api.getClient().create(ApiEndPoint::class.java)
val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMap { Observable.fromIterable(it.restaurants) }
                .flatMapMaybe(object : Function<RestaurantObject, MaybeSource<List<UserReviews>>> {
                    override fun apply(restaurantObject: RestaurantObject): MaybeSource<List<UserReviews>> {
                        val maybe = endPoint.getRestaurantReviewMaybe(restaurantObject.restaurant.id, 0, 3)
                        val userReview = maybe.blockingGet()
                        return if(userReview.reviewsCount > 0) {
                            Maybe.just(userReview.userReviews)
                        } else {
                            Maybe.empty()
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<UserReviews>> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(t: List<UserReviews>) {
                        System.out.println("onNext ${t.count()}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")
                    }
                })
```

**Output**
```
onSubscribe
onNext 1
onNext 1
onComplete
```

**Code Analysis**

1. The objective of the above example is to show the review counts of the restaurants near by your location.
2. `getRestaurantsAtLocation` function will get the restaurants near by.
3. The restaurants are iterated and given to `flatMapMaybe` one by one. 
4. If you see the `apply` function inside `flatMapMaybe`, I have called another network call
to fetch the reviews of the respective restaurant.  
5. If there is no review for a restaurant then no need of passing it to `onNext`. 
6. Reviews count **Maybe** zero, no one knows until we get the response.
7. So I have made a conditional check, if reviews are available then they are returned using `Maybe.just(userReview.userReviews)` 
or `Maybe.empty()` is used to return nothing. 
8. If I used `flatMap` instead of `flatMapMaybe` for the same situation I would get the following output

```
onSubscribe
onNext 1
onNext 0
onNext 1
onComplete
```

### concatMapMaybe
`ConcatMap + Maybe = ConcatMapMaybe`.
concatMapMaybe produces the same output as 
flatMapMaybe but the sequence the data emitted changes. concatMapMaybe() maintains the order of items 
and waits for the current Observable to complete its job before emitting the next one.

```
val progressBar = ProgressDialog(context)
val endPoint = Api.getClient().create(ApiEndPoint::class.java)
val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMap { Observable.fromIterable(it.restaurants) }
                .concatMapMaybe(object : Function<RestaurantObject, MaybeSource<List<UserReviews>>> {
                    override fun apply(restaurantObject: RestaurantObject): MaybeSource<List<UserReviews>> {
                        val maybe = endPoint.getRestaurantReviewMaybe(restaurantObject.restaurant.id, 0, 3)
                        val userReview = maybe.blockingGet()
                        return if(userReview.reviewsCount > 0) {
                            Maybe.just(userReview.userReviews)
                        } else {
                            Maybe.empty()
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<UserReviews>> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(t: List<UserReviews>) {
                        System.out.println("onNext ${t.count()}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")
                    }
                })
```

### flatMapIterable

This operator converts the list of object to a Iterable of streams so that each item in
the list will be emitted one by one to its observer.

Let us take the above example where I have used `.flatMap { Observable.fromIterable(it.restaurants) }`
to convert the list of `RestaurantObject` to a Iterable of streams. Instead of doing
that you can directly use `flatMapIterable{it.restaurants}`. 

**When to use?**

If you have a list already and you want to convert it to a iterable of streams then you can do as follow
```
val list = Arrays.asList("A","B","C","D","E","F")
Observable.fromIterable(list)
```
So this will emit the items one by one.

But suppose if you are getting a list of object in a network response and 
you want to iterate and emit, then you can make use of `flatMapIterable`. 

```
val progressBar = ProgressDialog(context)
val endPoint = Api.getClient().create(ApiEndPoint::class.java)
val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMapIterable { it.restaurants }
                .flatMapMaybe(object : Function<RestaurantObject, MaybeSource<List<UserReviews>>> {
                    override fun apply(restaurantObject: RestaurantObject): MaybeSource<List<UserReviews>> {
                        val maybe = endPoint.getRestaurantReviewMaybe(restaurantObject.restaurant.id, 0, 3)
                        val userReview = maybe.blockingGet()
                        return if(userReview.reviewsCount > 0) {
                            Maybe.just(userReview.userReviews)
                        } else {
                            Maybe.empty()
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<UserReviews>> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("flatMapMaybe onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("flatMapMaybe onSubscribe")
                    }

                    override fun onNext(t: List<UserReviews>) {
                        System.out.println("flatMapMaybe onNext ${t.count()}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("flatMapMaybe onError $error")
                    }
                })
```   

In this above example, I have used `flatMapIterable` to iterate and emit the restaurants
that I got through a network call. In this case I can't use `Observable.fromIterable()` because 
this is a **create operator** used to create a new observable but I already have a observable created 
for a network call. So I must use some **Transform Operator** to convert list to Iterable of streams 
so that each item in the list will be emitted one by one to its observer. 
This can be achieved using `flatMapIterable`. 

### concatMapIterable

Both `flatMapIterable` and `concatMapIterable` does the same functionality. 
This was just aliased for better discoverability in the API.

### window

This operator divides a Observable into multiple Observables, these divided observables
are called as windows. This split is done based on two calculative factors.

1. Size
2. Time  

**Size:**
```
var count = 1
Observable.just("1", "2", "3", "4", "5")
                .window(2)
                .flatMap {
                    System.out.println("Observable $count")
                    count++
                    it
                }
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("onNext $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
```
**Output**
```
onSubscribe
Observable 1
onNext 1
onNext 2
Observable 2
onNext 3
onNext 4
Observable 3
onNext 5
onComplete
```

In the above code example, I have used `window(2)`. As you all know `Observable.just()` 
will emit items one by one. This `window` operator will wait until the mentioned **size** of items
to emit and will collectively emit those items as `Observable<Observable<T>>`. In our case
it is `Observable<Observable<String>>`. So in the output, you can notice the division of observables and their
respective emission. Just to make you understand the split of observables I have added a print statement inside the
`flatMap`. 

**Time:**

```
var count = 1
Observable.just("1", "2", "3", "4", "5", "6", "7", "8")
                .window(2, TimeUnit.MILLISECONDS)
                .flatMap {
                    System.out.println("WindowObservable $count")
                    count++
                    it
                }
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("Window onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("Window onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("Window onNext $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("Window onError $error")
                    }
                })
```
**Output**
```
onSubscribe
Observable 1
onNext 1
onNext 2
onNext 3
onNext 4
onNext 5
onNext 6
onNext 7
Observable 2
onNext 8
onComplete
```

In this example, `window` operator is used with specific time.`window` will wait until the mentioned **time** 
and will collectively emit those items as `Observable<Observable<T>>`. In our case, only two observables are 
created with their respective emission.

**windowWithSkip**

```
var count = 1
Observable.just("1", "2", "3", "4", "5", "6", "7", "8")
                .window(2, 3)
                .flatMap {
                    System.out.println("WindowObservable $count")
                    count++
                    it
                }
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("Window onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("Window onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("Window onNext $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("Window onError $error")
                    }
                })
```
**Ouput**
```
onSubscribe
Observable 1
onNext 1
onNext 2
Observable 2
onNext 4
onNext 5
Observable 3
onNext 7
onNext 8
onComplete
```
This type of `window` operator will work same as `widow(size)` but that one extra parameter `window(size, skip)` will skip
that count item on each divided observable. In the output you can see that in every observable the 3rd item is getting skipped (i.e)
In our case 3 and 6 are skipped.

**Note:** Both `window` and `buffer` will do the same operation but the return type is different. 
`window` will return stream of stream (i.e) `Observable<Observable<T>>` where `buffer` will return stream of
list (i.e) `Observable<List<T>>`. Actual statement official document - `window` operator is similar to `buffer` but collects 
items into separate **Observables** rather than into **Data Structures(List)** before reemitting them.

### cast

Everyone knows what is casting?. Casting is nothing but changing one type to another type.
For example String to Integer and vice versa. So in rxJava casting is done on the flow of data using
`cast` operator. Let see an example.

```
Observable.fromIterable(getManufacturedVehicles())
                .filter {
                    it is Car
                }.cast(Car::class.java)
                .subscribe(object : Observer<Car> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(result: Car) {
                        System.out.println("onNext ${result.name}")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
```

**Output**
```
onSubscribe
onNext Ferrari
onNext Lamborghini
onNext Rolls Royce
onNext Porsche
onComplete
```

**Code Analysis**
1. I have inherited two data class `Bike` and `Car` from class `Vehicle`. 
2. Data classes are below.

```
    open class Vehicle (var type: String, var vehicleName: String? = null)

    data class Bike(val isFootPathAvailable: Boolean, val name: String): Vehicle("Bike", name)

    data class Car (var isFourSeated: Boolean, val name: String): Vehicle("Car", name)
```
3. Assume that you want to retrieve vehicle list from remote server or DB and to filter only 
`Car` type. You can do like below

```
Observable.fromIterable(getManufacturedVehicles())
                .filter {
                    it is Car
                }
                .subscribe(object : Observer<Vehicle> {
                    override fun onComplete() {
                        System.out.println("  onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("cast onSubscribe")
                    }

                    override fun onNext(result: Vehicle) {
                        result as Car
                        System.out.println("cast onNext ${result.name}")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("cast onError $error")
                    }
                })
``` 
4. But using the above code snippet, you can cast only after you get the response in the `onNext()`. 
5. If you want `cast` on the flow of data then you can make use of the `cast()` operator.

### flatMapSingle

`FlatMap` that returns a `SingleSource` type is `flatMapSingle`. 

**When to use?**

Assume we already have a endpoint that returns 
`Single<Restaurants>` (i.e) `getRestaurantsAtLocationSingle(lat, long): Single<Restaurants>`. 
Suppose if we have multiple location coordinates and need to find the restaurant details nearby
those locations then we have to use `flatMap` to retrieve the restaurant details. Before `flatMapSingle`
was introduced we can achieve this like below example.

```
val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.073132,78.780151")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .flatMap(object : Function<String, Observable<Restaurants>> {
                    override fun apply(locCoordinates: String): Observable<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        val single = endPoint.getRestaurantsAtLocationSingle(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                        return single.toObservable()
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(t: Restaurants) {
                        System.out.println("onNext ${t.restaurants}")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $e")
                    }

                })
```

It does the job however the Single always needs to be converted to an Observable if we are using the `flatMap`. To avoid
this `flatMapSingle` was introduced. See the below example to understand it more clear.

```
val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.073132,78.780151")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .flatMapSingle(object : Function<String, Single<Restaurants>> {
                    override fun apply(locCoordinates: String): Single<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        return endPoint.getRestaurantsAtLocationSingle(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("flatMapSingle  onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("flatMapSingle  onSubscribe")
                    }

                    override fun onNext(t: Restaurants) {
                        System.out.println("flatMapSingle  onNext ${t.restaurants}")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("flatMapSingle  onNext $e")
                    }

                })
```

**Output**
```
onSubscribe
onNext [RestaurantObject(restaurant=Restaurant(id=18693573, name=Bismi Hotel))]
onNext [RestaurantObject(restaurant=Restaurant(id=18800810, name=The Black Pearl))]
onNext [RestaurantObject(restaurant=Restaurant(id=18800811, name=Barbeque)]
onComplete
```

### concatMapSingle 

`concatMapSingle` produces the same output as `flatMapSingle` but the sequence the data emitted changes. 
`concatMapSingle()` maintains the order of items and waits for the current Observable to complete its job before emitting the next one.

### concatMapSingleDelayError
This is same as `concatMapSingle` but the one extra adds-on is 
any errors from the sources will be delayed until all of them terminate.

**Note:** [Click here to see the impotant note](#important-note) 

### concatMapEager
This operator is different from `concatMap`. As you all know `concatMap` will make the calls sequentially.
`concatMap` will waits for a call to complete to start the next call. But `concatMapEager` is direct opposite to 
`concatMap`.`concatMapEager` will subscribes to all substreams at the same time, concurrently. Eventhough it makes
the concurrent calls, it maintains the order of items. See the below example and its output to get clear picture.

```
        val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.800820,78.689919")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .concatMapEager(object : Function<String, Observable<Restaurants>> {
                    override fun apply(locCoordinates: String): Observable<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        return endPoint.getRestaurantsAtLocation(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        val locationDetails = restaurants.restaurants[0].restaurant.location
                        System.out.println("onNext $locationDetails")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $e")
                    }
                })
```

**Output**
```
GET https://developers.zomato.com/api/v2.1/search?lat=9.925201&lon=78.119774&start=0&count=3
GET https://developers.zomato.com/api/v2.1/search?lat=13.08268&lon=80.270721&start=0&count=3
GET https://developers.zomato.com/api/v2.1/search?lat=10.80082&lon=78.689919&start=0&count=3

200 https://developers.zomato.com/api/v2.1/search?lat=9.925201&lon=78.119774&start=0&count=3
200 https://developers.zomato.com/api/v2.1/search?lat=10.80082&lon=78.689919&start=0&count=3
200 https://developers.zomato.com/api/v2.1/search?lat=13.08268&lon=80.270721&start=0&count=3

onSubscribe
onNext Location(latitude=9.925201, longitude=78.119774)
onNext Location(latitude=13.08268, longitude=78.689919)
onNext Location(latitude=10.80082, longitude=80.270721)
onComplete
```

In the above code example, I initialized a array of location coordinates. 
Using `concatMapEager`, I initiate HTTP requests instantly to get the restaurant details
in that location. In the output I have mentioned Http logs too to understand how `concatMapEager`
maintains the order. So three calls are made concurrently. We get the first call's reponse and it is 
passed downstream, but before the second network call's response arrives, the third call's response arrived. 
Unfortunately the third call's response must wait even more because we need a second call's response.
Once second call's response completes, the third call's result is passed downstream.
  
### concatMapEagerDelayError

This operator works same as `concatMapEager` but the one extra adds-on is 
it provide a way to specify when to throw the errors. It can be specified using the boolean.
See the below example for more clarity.

```
        val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.800820,78.689919")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .concatMapEagerDelayError(object : Function<String, Observable<Restaurants>> {
                    override fun apply(locCoordinates: String): Observable<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        return if (locCoordArray[0] == "13.082680") {
                            endPoint.getRestaurantsAtLocationWithError(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                        } else {
                            endPoint.getRestaurantsAtLocation(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                        }
                    }
                }, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        val locationDetails = restaurants.restaurants[0].restaurant.location
                        System.out.println("onNext $locationDetails")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $e")
                    }
                })
```

In the above example, I have used a network service with error (**getRestaurantsAtLocationWithError**) for
one location coordinate to check how `concatMapEagerDelayError` actually works. You have to pass
a boolean value as one of the argument to `concatMapEagerDelayError(mapper: Function, tillTheEnd: Boolean)`. 

If you pass `true` all errors are delayed until the end. So from the below output you can understand this more 
clear. As per the logic of the above sample, when the second location is processed, it should throw a error but 
it throws the error at the end.    

**Output when true is passed** 
```
onSubscribe
onNext Location(latitude=9.9322565385, longitude=78.1437617540)
onNext Location(latitude=10.8262220000, longitude=78.6847500000)
onError retrofit2.adapter.rxjava2.HttpException: HTTP 404
```  

If you pass `false`, an error from the main source is signalled when the current ObservableSource source terminates.
Which means it waits for all the calls to get complete and finally 
emits the **Exception(If one error occurred) or CompositeException(Collection of multiple errors)**.
Suppose there are no errors then finally the results are emitted one by one. 

**Output when false is passed** 
```
onSubscribe
onError retrofit2.adapter.rxjava2.HttpException: HTTP 404
```
**Output while multiple errors occurred when false is passed** 
```
onSubscribe
onError io.reactivex.exceptions.CompositeException: 2 exceptions occurred.
```

#### Important Note 

When you are using these kind of operators with `delayError` your main thread
handling should also be different. Main thread should be capable of handling these
delay errors. So you should specify this while observer getting subscribed.

```
.subscribeOn(Schedulers.io())
.observeOn(AndroidSchedulers.mainThread(), true)
```

```
observeOn(AndroidSchedulers.mainThread(), delayError)
```

`delayError` – Indicates if the onError notification may not cut ahead of onNext notification on the other side of the scheduling boundary. 
Which means if this boolean flag is not mentioned then it will not skip the errors.

This note suits for the below operators 

1. `concatMapDelayError`
2. `concatMapSingleDelayError` 
3. `concatMapCompletableDelayError`
4. `concatMapEagerDelayError`

### flattenAsObservable

`flattenAsObservable` operator is just like a `flatMapIterable` which converts the list of object to a Iterable of streams so that each item in
the list will be emitted one by one to its observer. But `flatMapIterable` is available for `Flowable` and `Observable` types and 
`flattenAsObservable` is available for `Single` and `Maybe` types. For better understand compare `flattenAsObservable` with `flatMapIterable`. 

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val single = endPoint.getRestaurantsAtLocationSingle(latitude, longitude, 0, 3)
        single.flattenAsObservable(object : Function<Restaurants, Iterable<RestaurantObject>> {
            override fun apply(restaurants: Restaurants): Iterable<RestaurantObject> {
                return restaurants.restaurants
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<RestaurantObject> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(s: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(restaurantObject: RestaurantObject) {
                        System.out.println("onNext ${restaurantObject.restaurant.name}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")
                    }
                })
```

**Output**
```
onSubscribe
onNext The Black Pearl
onNext Coal Barbecues
onNext National Barbecues
onComplete
```

### flattenAsFlowable

`flattenAsFlowable` will also works same as `flattenAsObservable` which is available for `Single`
and `Maybe` types. But there is a difference between these two. `flattenAsFlowable` will
returns a `Flowable` that emits the elements from the Iterable but `flattenAsObservable` will
returns an `Observable` that emits the elements from the Iterable. 

As you all know `Flowable` type is used handle the `Backpressure`. 
You can also make use of this operator if you decide to handle backpressure.

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val single = endPoint.getRestaurantsAtLocationSingle(latitude, longitude, 0, 3)
        single.flattenAsFlowable(object : Function<Restaurants, Iterable<RestaurantObject>> {
            override fun apply(restaurants: Restaurants): Iterable<RestaurantObject> {
                return restaurants.restaurants
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSubscriber<RestaurantObject>() {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onStart() {
                        progressBar.show()
                        request(1)
                        System.out.println("onStart")
                    }

                    override fun onNext(restaurantObject: RestaurantObject) {
                        System.out.println("onNext ${restaurantObject.restaurant.name}")
                        request(1)
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")
                    }
                })
```

**Output**
```
onSubscribe
onNext The Black Pearl
onNext Coal Barbecues
onNext National Barbecues
onComplete
```

### flatMapObservable

`flatMapObservable` will work same as `flattenAsObservable` 
but the difference is `flattenAsObservable` will return only Observable<Iterator>
but `flatMapObservable` will return Observable<Any>. And this 
operator is available only for `Single` and `Maybe` types.

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val single = endPoint.getRestaurantsAtLocationSingle(latitude, longitude, 0, 3)
        single.flatMapObservable(object : Function<Restaurants, Observable<List<RestaurantObject>>>{
                           override fun apply(restaurants: Restaurants): Observable<List<RestaurantObject>> {
                               return Observable.just(restaurants.restaurants)
                           }
              }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<RestaurantObject>>{
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(restaurants: List<RestaurantObject>) {
                        System.out.println("onNext ${restaurants.size}")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
```

**Output**
```
onSubscribe
onNext 3
onComplete
```
In the above code example I have used `Observable.just()` to return entire Restaurants list.
You can also return a Iterator using `Observable.fromIterable(list)` instead of `Observable.just()` 
based on your usecase.

### flatMapSingleElement

This operator is available only in `Maybe` type. You should know how `Maybe` observable type works to understand
this operator. To know about `Maybe` observable type [Click here](../observables/README.md).  

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val maybe = endPoint.getRestaurantsAtLocationMaybe(0.0, 0.0, 0, 3)
        maybe.filter { it.restaurants.isNotEmpty() }
                .flatMapSingleElement(object : Function<Restaurants,Single<List<RestaurantObject>>>{
                    override fun apply(restaurants: Restaurants): Single<List<RestaurantObject>> {
                        System.out.println("apply function inside flatMapSingleElement")
                        return Single.just(restaurants.restaurants)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MaybeObserver<List<RestaurantObject>>{
                    override fun onSuccess(restaurants: List<RestaurantObject>) {
                        progressBar.dismiss()
                        System.out.println("onSuccess ${restaurants.size}")
                    }

                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $error")
                    }
                })
```

**Output**
```
onSubscribe
onComplete
```

In the above sample, you might noticed that I am passing latitude and longitude as 0.
This is to show how the code reacts If the result is empty. The Restaurant network api will
return a empty result if the location coordinates are not valid. If the result is empty then
`Maybe` will directly call `onComplete` because nothing to do with empty result so `onSuccess(result)` and 
`flatMapSingleElement{}` will not be called.

Lets take a valid scenario where I am passing the valid location coordinates **(9.925201,78.119774)**.
In this case I will get the result and if I want to transform this result to another then I can make
use of `flatMapSingleElement` operator which in turns return `SingleSource<Object>`. 
In this example I have just taken the inner `restaurants` list object from the root object and returned
it as `SingleSource<List<RestaurantObject>>` by using `Single.just(restaurants.restaurants)`. 


Below output is when I pass the valid location coordinates **(9.925201,78.119774)**

**Output**
```
onSubscribe
apply function inside flatMapSingleElement
onSuccess 3
```


### Availability Table

A - Available, NA - Not Available

| Operators                         |Observable|Flowable|Single|Maybe|Completable|
|-----------------------------------|----------|--------|------|-----|-----------|
| buffer                            |   A      |A       |  NA  |  NA |  NA       |
| map                               |   A      |A       |  NA  |  NA |  NA       |
| flatMap                           |   A      |A       |  A   |  A  |  NA       |
| concatMap                         |   A      |A       |  NA  |  A  |  NA       |
| concatMap                         |   A      |A       |  NA  |  A  |  NA       |
| concatMapDelayError               |   A      |A       |  NA  |  NA |  NA       |
| switchMap                         |   A      |A       |  NA  |  NA |  NA       |
| groupBy                           |   A      |A       |  NA  |  NA |  NA       |
| scan                              |   A      |A       |  NA  |  NA |  NA       |
| reduce                            |   A      |A       |  NA  |  NA |  NA       |
| flatMapCompletable                |   A      |A       |  NA  |  NA |  NA       |
| concatMapCompletable              |   A      |A       |  NA  |  NA |  NA       |
| concatMapCompletableDelayError    |   A      |A       |  NA  |  NA |  NA       |
| flatMapMaybe                      |   A      |A       |  NA  |  NA |  NA       |
| concatMapMaybe                    |   A      |A       |  NA  |  NA |  NA       |
| flatMapIterable                   |   A      |A       |  NA  |  NA |  NA       |
| concatMapIterable                 |   A      |A       |  NA  |  NA |  NA       |
| window                            |   A      |A       |  NA  |  NA |  NA       |
| cast                              |   A      |A       |  A   |  A  |  NA       |
| flatMapSingle                     |   A      |A       |  NA  |  A  |  NA       |
| concatMapSingle                   |   A      |A       |  NA  |  NA |  NA       |
| concatMapSingleDelayError         |   A      |A       |  NA  |  NA |  NA       |
| concatMapEager                    |   A      |A       |  NA  |  NA |  NA       |
| concatMapEagerDelayError          |   A      |A       |  NA  |  NA |  NA       |
| flattenAsObservable               |   NA     |NA      |  A   |  A  |  NA       |
| flattenAsFlowable                 |   NA     |NA      |  A   |  A  |  NA       |
| flatMapObservable                 |   NA     |NA      |  A   |  A  |  NA       |
| flatMapSingleElement              |   NA     |NA      |  NA  |  A  |  NA       |

