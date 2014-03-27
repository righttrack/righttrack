package database.dao

import models.common.EventId
import models.github.events.{GithubPushEvent, GithubPushEventData}
import models.users.UserId
import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait GithubPushEventDAO extends BaseDAO {

  // TODO: Move to Creates[GithubPushEvent]
  def add(pushEvent: GithubPushEvent): Future[Boolean]

  def retrievePreviousWeek(user: UserId, repoId: Int, from: Duration): Finds[GithubPushEventData]

  def findById(id: EventId): FindsOne[GithubPushEvent]
}