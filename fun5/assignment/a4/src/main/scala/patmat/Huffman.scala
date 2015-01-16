package patmat

import common._

object Huffman {
  abstract class CodeTree
  case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
  case class Leaf(char: Char, weight: Int) extends CodeTree


  def weight(tree: CodeTree): Int = tree match {
    case Leaf(_ ,w) => w
    case Fork(_, _, _, w) => w
  }

  def chars(tree: CodeTree): List[Char] = tree match {
    case Leaf(c, _) => c :: Nil
    case Fork(_, _, c, _) => c
  }

  def makeCodeTree(left: CodeTree, right: CodeTree) =
    Fork(left, right, chars(left) ::: chars(right), weight(left) + weight(right))


  def string2Chars(str: String): List[Char] = str.toList


  def times(chars: List[Char]): List[(Char, Int)] = {
    def timesAcc(chars: List[Char], pairs: List[(Char, Int)]): List[(Char, Int)] = chars match {
      case x :: xs => timesAcc(chars.filter(c => c != x), (x, chars.count(c => c == x)) :: pairs)
      case Nil => pairs
    }

    timesAcc(chars, Nil)
  }

  def makeOrderedLeafList(freqs: List[(Char, Int)]): List[Leaf] = freqs.sortBy(_._2) match {
    case x :: xs => Leaf(x._1, x._2) :: makeOrderedLeafList(xs)
    case Nil => Nil
  }

  def singleton(trees: List[CodeTree]): Boolean = trees match {
    case x :: Nil => true
    case _ => false
  }

  def combine(trees: List[CodeTree]): List[CodeTree] = trees match {
    case left :: right :: xs => (makeCodeTree(left, right) :: xs).sortBy(elem => weight(elem))
    case _ => trees
  }

  def until(singleton: List[CodeTree] => Boolean, combine: List[CodeTree] => List[CodeTree])(trees: List[CodeTree]): CodeTree =
    if (singleton(trees)) trees.head else until(singleton, combine)(combine(trees))

  def createCodeTree(chars: List[Char]): CodeTree = until(singleton, combine)(makeOrderedLeafList(times(chars)))


  type Bit = Int

  def decode(tree: CodeTree, bits: List[Bit]): List[Char] = {
    def decodeAcc(treeAcc: CodeTree, bits: List[Bit], acc: List[Char]): List[Char] = (treeAcc, bits) match {
      case (Leaf(char, _), Nil) => acc ::: List(char)
      case (Leaf(char, _), bits) => decodeAcc(tree, bits, acc ::: List(char))
      case (Fork(left, _, _, _), 0 :: xs) => decodeAcc(left, xs, acc)
      case (Fork(_, right, _, _), 1 :: xs) => decodeAcc(right, xs, acc)
      case (_, _) => acc
    }

    decodeAcc(tree, bits, Nil)
  }

  val frenchCode: CodeTree = Fork(Fork(Fork(Leaf('s',121895),Fork(Leaf('d',56269),Fork(Fork(Fork(Leaf('x',5928),Leaf('j',8351),List('x','j'),14279),Leaf('f',16351),List('x','j','f'),30630),Fork(Fork(Fork(Fork(Leaf('z',2093),Fork(Leaf('k',745),Leaf('w',1747),List('k','w'),2492),List('z','k','w'),4585),Leaf('y',4725),List('z','k','w','y'),9310),Leaf('h',11298),List('z','k','w','y','h'),20608),Leaf('q',20889),List('z','k','w','y','h','q'),41497),List('x','j','f','z','k','w','y','h','q'),72127),List('d','x','j','f','z','k','w','y','h','q'),128396),List('s','d','x','j','f','z','k','w','y','h','q'),250291),Fork(Fork(Leaf('o',82762),Leaf('l',83668),List('o','l'),166430),Fork(Fork(Leaf('m',45521),Leaf('p',46335),List('m','p'),91856),Leaf('u',96785),List('m','p','u'),188641),List('o','l','m','p','u'),355071),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u'),605362),Fork(Fork(Fork(Leaf('r',100500),Fork(Leaf('c',50003),Fork(Leaf('v',24975),Fork(Leaf('g',13288),Leaf('b',13822),List('g','b'),27110),List('v','g','b'),52085),List('c','v','g','b'),102088),List('r','c','v','g','b'),202588),Fork(Leaf('n',108812),Leaf('t',111103),List('n','t'),219915),List('r','c','v','g','b','n','t'),422503),Fork(Leaf('e',225947),Fork(Leaf('i',115465),Leaf('a',117110),List('i','a'),232575),List('e','i','a'),458522),List('r','c','v','g','b','n','t','e','i','a'),881025),List('s','d','x','j','f','z','k','w','y','h','q','o','l','m','p','u','r','c','v','g','b','n','t','e','i','a'),1486387)

  val secret: List[Bit] = List(0,0,1,1,1,0,1,0,1,1,1,0,0,1,1,0,1,0,0,1,1,0,1,0,1,1,0,0,1,1,1,1,1,0,1,0,1,1,0,0,0,0,1,0,1,1,1,0,0,1,0,0,1,0,0,0,1,0,0,0,1,0,1)

  def decodedSecret: List[Char] = decode(frenchCode, secret)


  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    def encodeAcc(treeAcc: CodeTree, textAcc: List[Char], acc: List[Bit]): List[Bit] = (treeAcc, textAcc) match {
      case (Fork(left, right, char, _), x :: xs) => chars(left).contains(x) match {
        case true => left match {
          case Leaf(_, _) => encodeAcc(tree, xs, acc ::: List(0))
          case Fork(_, _, _, _) => encodeAcc(left, textAcc, acc ::: List(0))
        }
        case false => right match {
          case Leaf(_, _) => encodeAcc(tree, xs, acc ::: List(1))
          case Fork(_, _, _, _) => encodeAcc(right, textAcc, acc ::: List(1))
        }
      }
      case (_, _) => acc
    }

    encodeAcc(tree, text, Nil)
  }


  type CodeTable = List[(Char, List[Bit])]

  def codeBits(table: CodeTable)(char: Char): List[Bit] = table match {
    case (xchar, xbits) :: xs => if (char == xchar) xbits else codeBits(xs)(char)
    case _ => Nil
  }

  def convert(tree: CodeTree): CodeTable = {
    def convertAcc(tree: CodeTree, bits: List[Bit]): CodeTable = tree match {
      case Leaf(char, _) => (char, bits) :: Nil
      case Fork(left, right, _, _) => mergeCodeTables(convertAcc(left, bits ::: List(0)), convertAcc(right, bits ::: List(1)))
    }

    convertAcc(tree, Nil)
  }

  def mergeCodeTables(a: CodeTable, b: CodeTable): CodeTable = a ::: b

  def quickEncode(tree: CodeTree)(text: List[Char]): List[Bit] = {
    def encodeAcc(table: CodeTable, text: List[Char], acc: List[Bit]): List[Bit] = text match {
      case x :: xs => encodeAcc(table, xs, acc ::: codeBits(table)(x))
      case Nil => acc
    }

    encodeAcc(convert(tree), text, Nil)
  }
}
