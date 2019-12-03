package com.marklynch.flickrsearch.io.flickr;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxJavaSnippet {

    public void doThings()
    {
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()) //thread you need to handle the result on
            .subscribe(observer);
    }

    Observable<String> observable = new Observable<String>() {
        @Override
        protected void subscribeActual(Observer<? super String> observer) {

        }
    };

    Observer<String> observer = new Observer<String>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(String s) {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };



}
