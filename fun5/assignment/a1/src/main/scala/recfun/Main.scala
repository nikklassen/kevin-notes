package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int =
    if (c == 0 || r == c) 1
    else pascal(c, r - 1) + pascal(c - 1, r - 1)

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    def changeCount(x: Char): Int = x match {
      case '(' => 1
      case ')' => -1
      case _   => 0
    }

    def doBalance(xs: List[Char], count: Int): Boolean = {
      if (xs.isEmpty) count == 0
      else if (count < 0) false
      else doBalance(xs.tail, count + changeCount(xs.head))
    }

    doBalance(chars, 0)
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int =
    if (money == 0) 1
    else if (money < 0 || coins.isEmpty) 0
    else countChange(money - coins.head, coins) +
      countChange(money, coins.tail)
}
