package database

private[database] trait DatabaseError

class DatabaseException(message: String, cause: Throwable = null)
  extends Exception(message, cause) with DatabaseError
