package database

class DatabaseException(message: String, cause: Throwable = null)
  extends Exception(message, cause)
