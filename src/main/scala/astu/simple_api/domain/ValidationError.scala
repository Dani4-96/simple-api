package astu.simple_api.domain

  sealed trait ValidationError extends Product with Serializable
  case object BillNotFoundError extends ValidationError
