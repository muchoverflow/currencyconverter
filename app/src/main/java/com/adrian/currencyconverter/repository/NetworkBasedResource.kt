package com.adrian.currencyconverter.repository

import com.adrian.currencyconverter.utils.applySchedulers
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

abstract class NetworkBasedResource<RequestType, ResultType> {
    private val result: BehaviorSubject<Resource<ResultType>> = BehaviorSubject.createDefault(Resource.loading())

    private fun fetchFromRemote() {
        val apiCall = createCall()
        @Suppress("NoSubs")
        apiCall
            .flatMap { response ->
                val saveToDb = saveCallResult(response)
                saveToDb.andThen(loadFromDb())
            }
            .applySchedulers()
            .subscribe(
                { finalResult ->
                    result.onNext(Resource.success(finalResult))
                    result.onComplete()
                },
                { error ->
                    result.onNext(Resource.error(error.message ?: "Unknown"))
                    result.onComplete()
                }
            )
    }

    protected abstract fun saveCallResult(item: RequestType): Completable

    protected abstract fun shouldFetch(): Single<Boolean>

    protected abstract fun loadFromDb(): Single<ResultType>

    protected abstract fun createCall(): Single<RequestType>

    fun getSubject() = result

    fun request() {
        @Suppress("NoSubs")
        shouldFetch()
            .applySchedulers()
            .subscribe(
                { shouldFetch ->
                    if (shouldFetch)
                        fetchFromRemote()
                    else
                        loadFromDb()
                            .applySchedulers()
                            .subscribe(
                                { dbResult ->
                                    result.onNext(Resource.success(dbResult))
                                    result.onComplete()
                                },
                                { error ->
                                    result.onNext(Resource.error(error.message ?: "Unknown"))
                                    result.onComplete()
                                }
                            )
                },
                { error ->
                    result.onNext(Resource.error(error.message ?: "Unknown"))
                    result.onComplete()
                }
            )
    }
}