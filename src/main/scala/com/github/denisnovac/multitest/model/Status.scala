package com.github.denisnovac.multitest.model

import java.time.Instant

case class Status(
    uId: Int,
    uStatus: String,
    updatedAt: Instant
)
