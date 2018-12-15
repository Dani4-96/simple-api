package astu.simple_api.domain.shopping

import java.time.Instant

case class Shopping (
                    id: Int,
                    description: String,
                    amount: Int,
                    date: Instant,
                    userId: Int,
                  )

