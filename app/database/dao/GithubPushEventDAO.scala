package database.dao

import models.github.events.GithubPushEventData
import scala.concurrent.Future
import models.users.UserId
import database.FindResult
import org.joda.time.DateTime

trait GithubPushEventDAO {

  def add(pushEvent: GithubPushEventData): Future[Boolean]

  def retrievePreviousWeek(startDate: DateTime, endDate: DateTime, user: UserId, repoId: Int): Future[FindResult[GithubPushEventData]]
}