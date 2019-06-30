## Combining Observable:

Following are the combining operators used to combine the Observables.

* [mergeWith](#mergewith)
* [mergeDelayError](#mergedelayerror)
* [zip](#zip)
* [combineLatest](#combinelatest) 


### mergeWith
We can use the merge operator to combine the output of multiple Observables so that they act like one.
We may need to get multiple sets of asynchronous data streams that are independent of each other.
Instead of waiting for the previous stream to complete before requesting 
the next stream, we can call both at the same time and subscribe to the combined streams. This can be
achieved using `mergeWith` operator.

```
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
                .subscribeOn(Schedulers.io())

        val observable2 = endPoint.getRestaurantsAtLocation(secondLocation[0], secondLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable3 = endPoint.getRestaurantsAtLocation(thirdLocation[0], thirdLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        observable1.mergeWith(observable2)
                .mergeWith(observable3)
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
                        System.out.println("onNext ${restaurants.restaurants.size}")
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
onNext 3
onNext 3
onNext 0
onComplete
```

**Code Analysis:**

My requirement is to hit the location api service for multiple location coordinates in
parallel and combine the resultant restaurant details. So if you see the above code example its clearly
evident what I have did. I have used three location coordinates and fetched the restaurant details
and I have just printed the size of the each Restaurants in that respective location.

**Note:** To make the multiple asynchronous task to carried over by different threads in parallel
you have to mention `.subscribeOn(Schedulers.io())` for each observable as I coded in the
above sample.

### mergeDelayError

The mergeDelayError method is the same as merge in that it 
combines multiple Observables into one, but if errors occur during 
the merge, it allows error-free items to continue before propagating the errors.
From the below sample you will understand about this operator more clear.

```
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

        val observable1 = endPoint.getRestaurantsAtLocationWithError(firstLocation[0], firstLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable2 = endPoint.getRestaurantsAtLocation(secondLocation[0], secondLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable3 = endPoint.getRestaurantsAtLocation(thirdLocation[0], thirdLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        Observable.mergeDelayError(observable1, observable2, observable3)
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
                        System.out.println("onNext ${restaurants.restaurants.size}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println ("onError $error")
                    }
                })
```

**Output with single error scenario**
```
onSubscribe
onNext 0 
onNext 3
onError retrofit2.adapter.rxjava2.HttpException: HTTP 404
```

**Output with multiple error scenario**
```
onSubscribe
onNext 0
onError io.reactivex.exceptions.CompositeException: 2 exceptions occurred.
```

I have mentioned two output scenarios where we get single and multiple errors respectively. 
In the multiple error case, `mergeDelayError` operator will compose multiple errors and 
gives as a single `CompositeException` which contains the details of multiple exceptions.

### zip

zip operator gets the output of two observables and provide these outputs as an inputs through
a function for the developers to merge these inputs as single result.

**When can we use this?**

Suppose your remote server did not provide you all related details in a single service and you are forced to call multiple
services do get related details and finally merge to a single response then `zip` operator is the correct choice. 

```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 5)
                .subscribeOn(Schedulers.io())
                .flatMapIterable { it.restaurants }
                .concatMapEagerDelayError(object : Function<RestaurantObject,Observable<RestaurantObject>>{
                    override fun apply(restaurantObject: RestaurantObject): Observable<RestaurantObject> {

                        val reviewObservable = endPoint.getRestaurantReview(restaurantObject.restaurant.id, 0, 3)
                        val menuObservable = endPoint.getRestaurantDailyMenu(restaurantObject.restaurant.id)

                        return Observable.zip(reviewObservable, menuObservable, object : BiFunction<UserReviewsObject, MenuObject, RestaurantObject> {
                            override fun apply(review: UserReviewsObject, menu: MenuObject): RestaurantObject {
                                restaurantObject.restaurant.review = review
                                restaurantObject.restaurant.menu = menu
                                return restaurantObject
                            }
                        })
                    }
                }, true)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(object : Observer<RestaurantObject> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(restaurant: RestaurantObject) {
                        System.out.println("Reviews and Menu of "+restaurant.restaurant.name)
                        System.out.println("No of Reviews "+restaurant.restaurant.review?.userReviews?.size)
                        System.out.println("No of Menu "+restaurant.restaurant.menu?.menuList?.size)
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
Reviews and Menu of Bismi Hotel
No of Reviews 10
No of Menu 5
Reviews and Menu of The Black Pearl
No of Reviews 8
No of Menu 6
Reviews and Menu of Barbeque
No of Reviews 7
No of Menu 7
onComplete
```

**Code Analysis**

1. This is a sample code where I have used 3 different network services to get the following Restaurant Details

 * Service to get restaurants near your location.
 * With the retrieved restaurant Id hit another service to get `Reviews` of the respective restaurant.   
 * With the retrieved restaurant Id hit another service to get `Menu` of the respective restaurant.

2. After getting the Restaurants near your location, I have used `flatMapIterable` to iterate the restaurants
one by one.
3. I have used `concatMapEagerDelayError` to delay the errors to the end. If you are not familiar with `concatMapEagerDelayError` 
 [check it here](../transformoperators/README.md)   
4. You can notice that I have called two more remote services to get **reviews** and **menu** respectively
inside `apply` function of `concatMapEagerDelayError`. 
5. This is the place where I have used `zip` operator that accepts two input observables and provides a `BiFunction`
where we have to implement the logic to merge the responses from the two network calls that we made.
6. Finally the merged result is emitted to the `onNext` for each restaurant.

We can also use zip operator with more than two Observables and till nine Observables. For example if you want to
merge **three** Observables response then you have to use `io.reactivex.functions.Function3<T1,T2,T3,R>` instead of `io.reactivex.functions.BiFunction<T1,T2,R>`. 
`Zip` operator provides support to handle nine observables by changing the function accordingly(Function3 to Function9). 
Below is the sample to show how zip operates with three sources.


```
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 5)
                .subscribeOn(Schedulers.io())
                .flatMapIterable { it.restaurants }
                .concatMapEagerDelayError(object : Function<RestaurantObject, Observable<RestaurantObject>> {
                    override fun apply(restaurantObject: RestaurantObject): Observable<RestaurantObject> {

                        val reviewObservable = endPoint.getRestaurantReview(restaurantObject.restaurant.id, 0, 3)
                                .subscribeOn(Schedulers.io())
                        val menuObservable = endPoint.getRestaurantDailyMenu(restaurantObject.restaurant.id)
                                .subscribeOn(Schedulers.io())
                        val awardsObservable = endPoint.getRestaurantAwards(restaurantObject.restaurant.id)
                                .subscribeOn(Schedulers.io())

                        return Observable.zip(reviewObservable,
                                menuObservable,
                                awardsObservable,
                                object : Function3<UserReviewsObject, MenuObject, AwardObject, RestaurantObject> {
                                    override fun apply(review: UserReviewsObject, menu: MenuObject, award: AwardObject): RestaurantObject {
                                        restaurantObject.restaurant.review = review
                                        restaurantObject.restaurant.menu = menu
                                        restaurantObject.restaurant.award = award
                                        return restaurantObject
                                    }
                                })
                    }
                }, true)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(object : Observer<RestaurantObject> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(restaurant: RestaurantObject) {
                        System.out.println("Reviews, Menu and Awards of "+restaurant.restaurant.name)
                        System.out.println("No of Reviews "+restaurant.restaurant.review?.userReviews?.size)
                        System.out.println("No of Menu "+restaurant.restaurant.menu?.menuList?.size)
                        System.out.println("No of Awards "+restaurant.restaurant.award?.awardList?.size)
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
Reviews, Menu and Awards of  Bismi Hotel
No of Reviews 10
No of Menu 5
No of Awards 5
Reviews, Menu and Awards of  The Black Pearl
No of Reviews 8
No of Menu 6
No of Awards 5
Reviews, Menu and Awards of  Barbeque
No of Reviews 7
No of Menu 7
No of Awards 5
onComplete
```

### combineLatest

In simple, `combineLatest` combines the most recently emitted items from each of the source Observables.
Like `zip`, `combineLatest` will also allows for two to nine observables. First let us see a simple sample to know 
how `combineLatest` works.

```
        val observable1 = Observable.interval(400, TimeUnit.MILLISECONDS)
        val observable2 = Observable.interval(250, TimeUnit.MILLISECONDS)

        Observable.combineLatest(
                observable1,
                observable2,
                object : BiFunction<Long, Long, String> {
                    override fun apply(t1: Long, t2: Long): String {
                        return "Observable1 value: $t1 , Observable2 value: $t2"
                    }
                })
                .take(5)
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("onNext: $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("onError $error")
                    }
                })
                
        TimeUnit.SECONDS.sleep(1)
```

**Output**
```
onSubscribe
onNext: Observable1 value: 0 , Observable2 value: 0
onNext: Observable1 value: 0 , Observable2 value: 1
onNext: Observable1 value: 0 , Observable2 value: 2
onNext: Observable1 value: 1 , Observable2 value: 2
onNext: Observable1 value: 1 , Observable2 value: 3
onComplete
```

**Code Analysis**

1. I have created two observables each emitting values 
after an interval of 400 ms and 250 ms respectively. 
2. When these observables are combined using `combineLatest` operator you get
the above output. 
3. This operator at first wait for the two observables to emit values
and then these two values are emitted and manipulated inside the `apply` function.
4. First, Observable2 will emit 0 after 250ms of interval and `combineLatest` will wait for 
Observable1 to emit, after Observable1 emits `combineLatest` will emits both together.
5. apply function is used to manipulate the two emission.
6. So from now on whenever any one of the observable emits, this operator will
take it as a latest emission and combine this emission with the last emission of the
another observable.

So now I hope you all know how `combineLatest` will work. But this is not enough.
You should know, How this can be used in a real time?

Let us take a simple **Sign up Form** example. Where you have to enable/disable the 
Sign up button based on the validation on **UserName and Password**. This is the scenario 
where `combineLatest` works out.

```
        val userNameObservable = RxTextView.textChanges(userNameEditText)
        val passwordObservable = RxTextView.textChanges(passwordEditText)
        val signUpButtonConsumer = RxView.enabled(signUpButton)
        val errorConsumer = object : Consumer<Throwable> {
            override fun accept(error: Throwable?) {
                System.out.println("onError $error")
            }
        }
        Observable.combineLatest(userNameObservable, passwordObservable, object : BiFunction<CharSequence, CharSequence, Boolean> {
                    @Throws(Exception::class)
                    override fun apply(userName: CharSequence, password: CharSequence): Boolean  {
                        return AppUtils.isValidMail(userName) && AppUtils.isValidPassword(password)
                    }
                }).subscribe(signUpButtonConsumer, errorConsumer)
```

**Output**
```
UserName : har and Password :
UserName : hari and Password :
UserName : hari@ and Password :
UserName : hari@gm and Password :
UserName : hari@gmail and Password :
UserName : hari@gmail.com and Password :
UserName : hari@gmail.com and Password : H
UserName : hari@gmail.com and Password : Hari
UserName : hari@gmail.com and Password : Hari@
UserName : hari@gmail.com and Password : Hari@123
```

**Code Analysis**
1. I have used RxJava binding APIs for Android UI widgets. 
2. Using RxJava binding Complex UI interactions can also be greatly simplified too, 
especially when there are multiple UI events that can trigger. 
3. `RxTextView` is used to create an observable of character sequences for text changes on view. 
4. These created observables for both Username and Password EditText fields are passed to `combineLatest()`. 
5. Now as you all know how `combineLatest` will work, just think yourself how it is useful here.
6. Whenever the user types in any of the two fields, emission will occur 
with a delay as we have used `debounce` operator.   
7. So in this case, you can see the password is empty until I completely typed my UserName. 
8. When I start typing password, `combineLatest` will already kept hold of the last emitted item
from `userNameObservable`, so it combines the latest emission from `passwordObservable` with last emitted
item from `userNameObservable`.
9. Inside the Function of `combineLatest` I have done the validation part and based on the validation the
**Sign up** button is enabled.       
  