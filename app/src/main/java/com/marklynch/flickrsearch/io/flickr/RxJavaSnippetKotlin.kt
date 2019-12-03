package com.marklynch.flickrsearch.io.flickr

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RxJavaSnippetKotlin {

    var observable: Observable<String> = object : Observable<String>() {
        override fun subscribeActual(observer: Observer<in String>) {

        }
    }

    var observer: Observer<String> = object : Observer<String> {
        override fun onSubscribe(d: Disposable) {

        }

        override fun onNext(s: String) {

        }

        override fun onError(e: Throwable) {

        }

        override fun onComplete() {

        }
    }

    fun doThings() {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) //thread you need to handle the result on
                .subscribe(observer)
    }


}
