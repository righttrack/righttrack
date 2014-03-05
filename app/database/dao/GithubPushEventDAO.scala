package database.dao

import models.github.events.{GithubPushEvent, GithubPushEventData}
import scala.concurrent.Future
import models.users.UserId
import database.FindResult
import org.joda.time.DateTime
import models.common.EventId
import scala.concurrent.duration.Duration

trait GithubPushEventDAO {

  def add(pushEvent: GithubPushEvent): Future[Boolean]

  def retrievePreviousWeek(user: UserId, repoId: Int, from: Duration): Future[FindResult[GithubPushEventData]]

  def findById(id: EventId): Future[Option[GithubPushEvent]]
}