## Combining Observable:

Following are the combining operators.

* [mergeWith](#mergewith)
* [mergeDelayError](#mergedelayerror)


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
                        System.out.println("onError $error")
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







