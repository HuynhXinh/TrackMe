package com.xinh.domain.interactor

import com.xinh.domain.exception.Failure
import com.xinh.domain.exception.SaveTrackingInfoFailure
import com.xinh.domain.executor.SchedulerProvider
import com.xinh.domain.functional.Either
import com.xinh.domain.model.TrackingInfo
import com.xinh.domain.repository.TrackingRepository
import io.reactivex.Observable

class SaveTrackingInfo(
    private val trackingRepository: TrackingRepository,
    schedulerProvider: SchedulerProvider
) : UseCase<Long, TrackingInfo>(schedulerProvider) {

    override fun buildObservable(params: TrackingInfo): Observable<Either<Failure, Long>> {
        return trackingRepository.save(params).map {
            if (it >= 0) {
                Either.Right(it)
            } else {
                Either.Left(SaveTrackingInfoFailure())
            }
        }
    }

}