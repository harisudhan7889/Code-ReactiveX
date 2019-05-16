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
If an Observable produces 10000 items per second how a subscriber/operator which can handle only 100 items per second does process the items? 
By applying BackPressure onto a stream, it’ll be possible to handle items as needed, unnecessary items can be discarded 
or even let the producer know when to create and push the new items. 