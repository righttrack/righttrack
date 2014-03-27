package cake

import akka.actor.ActorSystem

trait HasActorSystem {

  def actorSystem: ActorSystem
}

trait GlobalActorSystem extends HasActorSystem {

  override def actorSystem: ActorSystem = GlobalActorSystem.actorSystem
}

object GlobalActorSystem {

  lazy val actorSystem: ActorSystem = ActorSystem("righttrack")
}