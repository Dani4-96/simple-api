package astu.simple_api.domain.salary

import java.time.Instant

case class Salary (
                  id: Int,
                  description: String,
                  amount: Int,
                  date: Instant,
                  userId: Int,
                )
