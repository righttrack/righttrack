package util

trait RandomHelpers {

  implicit class RandomOps(random: scala.util.Random) {

    def randomString(length: Int): String = random.alphanumeric.take(length).mkString("")
  }
}
