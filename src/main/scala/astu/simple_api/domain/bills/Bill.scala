package astu.simple_api.domain.bills

import java.time.Instant

case class Bill (
  id: Int,
  description: String,
  amount: Int,
  date: Instant,
  userId: Int,
)
