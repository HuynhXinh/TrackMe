package com.xinh.domain.interactor

import com.xinh.domain.exception.Failure
import com.xinh.domain.executor.SchedulerProvider
import com.xinh.domain.functional.Either
import com.xinh.domain.model.ItemHistory
import com.xinh.domain.repository.TrackingRepository
import io.reactivex.Observable

class GetHistory(
    private val trackingRepository: TrackingRepository,
    schedulerProvider: SchedulerProvider
) : UseCase<List<ItemHistory>, GetHistory.Params>(schedulerProvider) {

    private val limit = 3

    override fun buildObservable(params: Params): Observable<Either<Failure, List<ItemHistory>>> {
        val offset = (params.page - 1) * limit
        return trackingRepository.getHistory(limit, offset).map {
            Either.Right(it)
        }
    }

    data class Params(val page: Int)
}