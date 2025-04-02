package com.bgpark.demo.dkb.processor.domain

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Lock(LockModeType.PESSIMISTIC_WRITE)
annotation class PessimisticWriteLockWithSkipLocked()
