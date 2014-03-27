package database.dao

import models.activity.Activity

trait ActivityDAO extends BaseDAO {

  def record(activity: Activity): Creates[Activity]

  def findAll(limit: Int = 100): Finds[Activity]
}
