package objsets

import common._
import TweetReader._

class Tweet(val user: String, val text: String, val retweets: Int) {
  override def toString: String =
    "User: " + user + "\n" +
    "Text: " + text + " [" + retweets + "]"
}

abstract class TweetSet {
  def filter(p: Tweet => Boolean): TweetSet = filterAcc(p, new Empty)

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet

  def union(that: TweetSet): TweetSet

  def mostRetweeted: Tweet

  def mostRetweetedAcc(current: Tweet): Tweet

  def descendingByRetweet: TweetList = descendingByRetweetAcc(Nil)

  def descendingByRetweetAcc(xs: TweetList): TweetList


  def incl(tweet: Tweet): TweetSet

  def remove(tweet: Tweet): TweetSet

  def contains(tweet: Tweet): Boolean

  def foreach(f: Tweet => Unit): Unit
}

class Empty extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = acc

  def union(that: TweetSet): TweetSet = that

  def mostRetweeted: Nothing = throw new NoSuchElementException("Empty.mostRetweeted")

  def mostRetweetedAcc(current: Tweet): Tweet = current

  def descendingByRetweetAcc(xs: TweetList): TweetList = xs


  def contains(tweet: Tweet): Boolean = false

  def incl(tweet: Tweet): TweetSet = new NonEmpty(tweet, new Empty, new Empty)

  def remove(tweet: Tweet): TweetSet = this

  def foreach(f: Tweet => Unit): Unit = ()
}

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet =
    if (p(elem)) left.filterAcc(p, right.filterAcc(p, acc incl elem)) else left.filterAcc(p, right.filterAcc(p, acc))

  def union(that: TweetSet): TweetSet =
    filterAcc(elem => true, that)

  def mostRetweeted: Tweet = mostRetweetedAcc(elem)

  def mostRetweetedAcc(current: Tweet): Tweet =
    if (elem.retweets > current.retweets) left mostRetweetedAcc (right mostRetweetedAcc elem)
    else left mostRetweetedAcc (right mostRetweetedAcc current)

  def descendingByRetweetAcc(xs: TweetList): TweetList =
    new Cons(this.mostRetweeted, (this remove this.mostRetweeted) descendingByRetweetAcc xs)


  def contains(x: Tweet): Boolean =
    if (x.text < elem.text) left.contains(x)
    else if (elem.text < x.text) right.contains(x)
    else true

  def incl(x: Tweet): TweetSet = {
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this
  }

  def remove(tw: Tweet): TweetSet =
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)

  def foreach(f: Tweet => Unit): Unit = {
    f(elem)
    left.foreach(f)
    right.foreach(f)
  }
}

trait TweetList {
  def head: Tweet
  def tail: TweetList
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit =
    if (!isEmpty) {
      f(head)
      tail.foreach(f)
    }
}

object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")
  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
  def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}


object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  def buildSet(xs: List[String]): TweetSet =
    if (xs.isEmpty) new Empty else TweetReader.allTweets.filter(tweet => tweet.text contains xs.head) union buildSet(xs.tail)

  lazy val googleTweets: TweetSet = buildSet(google)
  lazy val appleTweets: TweetSet = buildSet(apple)

  lazy val trending: TweetList = (googleTweets union appleTweets).descendingByRetweet
}

object Main extends App {
  GoogleVsApple.trending foreach println
}
