package models.activity

import models.{Version, EntityId, Entity}
import org.joda.time.DateTime
import models.activity.verb.{ValidAction, Verb}

case class Activity[S <: EntityId, V <: Verb, O <: EntityId, VN <: Version](
  id: ActivityId,
  actor: S,
  verb: V,
  entity: O,
  version: VN,
  time: DateTime = DateTime.now()
)(implicit evidence: ValidAction[S, V, O, VN])
  extends Entity

case class ActivityId(value: String) extends AnyVal with EntityId