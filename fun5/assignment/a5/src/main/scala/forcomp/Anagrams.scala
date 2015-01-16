package forcomp

import common._

object Anagrams {

  type Word = String

  type Sentence = List[Word]

  type Occurrences = List[(Char, Int)]

  val dictionary: List[Word] = loadDictionary


  def wordOccurrences(w: Word): Occurrences = (w.toLowerCase.toList groupBy(x => x)).mapValues(x => x.length).toList.sorted

  def sentenceOccurrences(s: Sentence): Occurrences = if (s == Nil) Nil else wordOccurrences(s.reduceLeft(_ ++ _))

  lazy val dictionaryByOccurrences: Map[Occurrences, List[Word]] = dictionary.groupBy(x => wordOccurrences(x))

  def wordAnagrams(word: Word): List[Word] = dictionaryByOccurrences(wordOccurrences(word))

  def combinations(occurrences: Occurrences): List[Occurrences] = occurrences match {
    case Nil => List(List())
    case (char, n) :: xs => {
      for {
        i <- 0 to n
        next <- combinations(xs)
      } yield if (i > 0) (char, i) :: next else next
    }.toList
  }

  def subtract(x: Occurrences, y: Occurrences): Occurrences = y match {
    case Nil => x
    case yy :: yys => subtract(x updated (x.indexOf(x.find(_._1 == yy._1).get), (yy._1, x.find(_._1 == yy._1).get._2 - yy._2)), yys) filter(x => x._2 > 0)
  }

  def sentenceAnagrams(sentence: Sentence): List[Sentence] = {
    def sentenceAnagramsAcc(occurrences: Occurrences): List[Sentence] = occurrences match {
      case Nil => List(List())
      case occurrence => for {
        combination <- combinations(occurrence)
        x <- dictionaryByOccurrences getOrElse(combination, Nil)
        xs <- sentenceAnagramsAcc(subtract(occurrences, wordOccurrences(x)))
      } yield x :: xs
    }

    sentenceAnagramsAcc(sentenceOccurrences(sentence))
  }
}
