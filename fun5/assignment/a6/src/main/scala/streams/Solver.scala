package streams

import common._

trait Solver extends GameDef {

  def done(b: Block): Boolean = b.b1 == goal && b.b2 == goal

  type NeighboursAndHistory = Stream[(Block, List[Move])]

  def neighborsWithHistory(b: Block, history: List[Move]): NeighboursAndHistory = (
    for {
      move <- b.legalNeighbors
    } yield (move._1, move._2 :: history)
  ).toStream

  def newNeighborsOnly(neighbors: NeighboursAndHistory, explored: Set[Block]): NeighboursAndHistory =
    neighbors filterNot (explored contains _._1)

  def from(initial: NeighboursAndHistory, explored: Set[Block]): NeighboursAndHistory = initial match {
    case (block, moves) #:: xs => {
      val ys = newNeighborsOnly(neighborsWithHistory(block, moves), explored)
      (block, moves) #:: from(xs ++ ys, explored + block)
    }
    case _ => Stream.empty
  }

  lazy val pathsFromStart: NeighboursAndHistory = from(Stream((startBlock, Nil)), Set.empty)

  lazy val pathsToGoal: NeighboursAndHistory = pathsFromStart filter(x => done(x._1))

  lazy val solution: List[Move] = pathsToGoal match {
    case x #:: _ => x._2.reverse
    case _ => List()
  }
}
