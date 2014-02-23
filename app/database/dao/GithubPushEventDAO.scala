package database.dao

import models.github.events.GithubPushEvent
import scala.concurrent.Future
import models.users.UserId
import database.FindResult

trait GithubPushEventDAO {

  def add(pushEvent: GithubPushEvent): Future[Boolean]

  def retrievePreviousWeek(startDate: , endDate: , user: UserId, repo: GithubRepository): Future[FindResult[GithubPushEvent]]
}