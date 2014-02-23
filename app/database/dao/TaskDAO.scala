package database.dao

import models.tasks.{Task, TaskId}
import scala.concurrent.Future
import models.github.events.{GithubPushEventId, GithubPushEvent}
import database.FindResult
import models.users.{UserId, User}

// TODO: Update this to work with CreateResults

trait TaskDAO {

  def add(pushEvent: GithubPushEvent): Future[Boolean]

  def retrievePreviousWeek(startDate: , endDate: , user: UserId, repo: GithubRepository): Future[FindResult[GithubPushEvent]]
}
