//package example
//
//import java.io.File
//
//import akka.actor._
//
//import org.eligosource.eventsourced.core._
//import org.eligosource.eventsourced.journal.leveldb.LeveldbJournalProps
//import org.specs2.mutable.Specification
//
//class MyActor extends Actor {
//  def receive = {
//    case _ =>
//  }
//}
//
//class MyERActor extends MyActor with Receiver with Eventsourced {
//  val id = 1
//}
//
//// event-sourced processor
//class Processor(destination: ActorRef) extends Actor {
//  var counter = 0
//
//  def receive = {
//    case msg: Message => {
//      // update internal state
//      counter = counter + 1
//      // print received event and number of processed event messages so far
//      println("[processor] event = %s (%d)" format (msg.event, counter))
//      // send modified event message to destination
//      destination ! msg.copy(event = "processed %d event messages so far" format counter)
//    }
//  }
//}
//
//// channel destination
//class Destination extends Actor {
//  def receive = {
//    case msg: Message => {
//      // print event received from processor via channel
//      println("[destination] event = '%s'" format msg.event)
//      // confirm receipt of event message from channel
//      msg.confirm()
//    }
//  }
//}
//
//class FirstSteps extends Specification {
//  implicit val system = ActorSystem("guide")
//
//  // create a journal
//  val journal: ActorRef = LeveldbJournalProps(new File("target/guide-1"), native = false).createJournal
//
//  // create an event-sourcing extension
//  val extension = EventsourcingExtension(system, journal)
//
//  // create channel destination
//  val destination: ActorRef = system.actorOf(Props[Destination])
//
//  // create and register a channel
//  val channel: ActorRef = extension.channelOf(DefaultChannelProps(1, destination))
//
//  // create and register event-sourced processor
//  val processor: ActorRef = extension.processorOf(Props(new Processor(channel) with Eventsourced { val id = 1 } ))
//
//  // recover registered processors by replaying journaled events
//  extension.recover()
//
//  // send event message to processor (will be journaled)
//  processor ! Message("foo")
//
//  // wait for all messages to arrive (graceful shutdown coming soon)
//  Thread.sleep(1000)
//
//  // then shutdown
//  system.shutdown()
//}
//
