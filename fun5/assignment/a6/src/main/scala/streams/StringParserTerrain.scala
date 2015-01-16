package streams

import common._

trait StringParserTerrain extends GameDef {

  val level: String

  def terrainFunction(levelVector: Vector[Vector[Char]]): Pos => Boolean = {
    ((position: Pos) => {
      if ((position.row < 0) || (position.col < 0)) false
      else if ((position.row < levelVector.length) && (position.col < levelVector(position.row).length))
        levelVector(position.row)(position.col) != '-'
      else false
    })
  }

  def findChar(c: Char, levelVector: Vector[Vector[Char]]): Pos = {
    val row = levelVector indexWhere(_ contains c)
    lazy val col = levelVector(row) indexWhere(_ == c)
    Pos(row, col)
  }

  private lazy val vector: Vector[Vector[Char]] =
    Vector(level.split("\n").map(str => Vector(str: _*)): _*)

  lazy val terrain: Terrain = terrainFunction(vector)
  lazy val startPos: Pos = findChar('S', vector)
  lazy val goal: Pos = findChar('T', vector)
}
