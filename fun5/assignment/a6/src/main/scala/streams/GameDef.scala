package streams

import common._

trait GameDef {

  case class Pos(row: Int, col: Int) {
    def drow(d: Int) = copy(row = row + d)

    def dcol(d: Int) = copy(col = col + d)
  }

  val startPos: Pos

  val goal: Pos

  type Terrain = Pos => Boolean

  val terrain: Terrain


  sealed abstract class Move
  case object Left  extends Move
  case object Right extends Move
  case object Up    extends Move
  case object Down  extends Move

  def startBlock: Block = Block(startPos, startPos)

  case class Block(b1: Pos, b2: Pos) {

    require(b1.row <= b2.row && b1.col <= b2.col, "Invalid block position: b1=" + b1 + ", b2=" + b2)

    def drow(d1: Int, d2: Int) = Block(b1.drow(d1), b2.drow(d2))

    def dcol(d1: Int, d2: Int) = Block(b1.dcol(d1), b2.dcol(d2))


    def left = if (isStanding) dcol(-2, -1)
               else if (b1.row == b2.row) dcol(-1, -2)
               else dcol(-1, -1)

    def right = if (isStanding) dcol(1, 2)
                else if (b1.row == b2.row) dcol(2, 1)
                else                   dcol(1, 1)

    def up = if (isStanding) drow(-2, -1)
             else if (b1.row == b2.row) drow(-1, -1)
             else   drow(-1, -2)

    def down = if (isStanding) drow(1, 2)
               else if (b1.row == b2.row) drow(1, 1)
               else drow(2, 1)


    def neighbors: List[(Block, Move)] = List((left, Left), (right, Right), (up, Up), (down, Down))

    def legalNeighbors: List[(Block, Move)] = neighbors filter(_._1.isLegal)

    def isStanding: Boolean = b1 == b2

    def isLegal: Boolean = terrain(b1) && terrain(b2)
  }
}
