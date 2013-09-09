package database.slick.h2.connection

sealed class CoreDatabase extends H2InMemoryDBProvider("righttrack")

object CoreDatabase extends CoreDatabase
