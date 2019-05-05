## Transform Operators:


* [buffer](#buffer-operator)
* [map](#map-operator)
* [flatMap](#flatmap-operator)
* [concatMap](#concatmap-operator)

#### buffer Operator
I have called two dependent 
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

#### map Operator

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
  
#### flatMap Operator

**Actual Definition:**

FlatMap also applies a function on each emitted item but instead of returning the modified item, 
it returns the Observable itself which can emit data again, So it is used to 
map over asynchronous operations.

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val observable = endPoint.getRestaurantsAtLocation(latitude, longitude)
        observable
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

#### concatMap Operator

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
  
#### switchMap Operator  

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

#### Difference between all Map operators


|  |  map | flatMap | concantMap | switchMap |
|---|---|---|---|---|
| Emission Order |The order of insertion is <br/> maintained during emission.| The order of insertion is <br/> not maintained during emission.|The order of insertion is <br/> maintained during emission.| NA|
| Return Type | Modified Item | Observable | Observable | Observable |     
| When can be used?| Consider using map operator where there is an offline operations needs to be done on emitted data. If we got something from server but that doesn’t fulfils our requirement. In that case, Map can be used to alter the emitted data.| Choose flatMap when the order is not important and want to send all the network calls simultaneously. In our case, we have fetched the restaurant details in our location. So in this situation order does not matters.| Choose concatMap when the order is important. If you consider ConcatMap in this scenario, the time takes to fetch the restaurants takes very longer time as the ConcatMap won’t make simultaneous calls in order to maintain item order.|switchMap is best suited when you want to discard the response and consider the latest one. Let’s say you are writing an Instant Search app which sends search query to server each time user types something. In this case multiple requests will be sent to server with multiple queries, but we want to show the result of latest typed query only. For this case, switchMap is best operator to use.|

