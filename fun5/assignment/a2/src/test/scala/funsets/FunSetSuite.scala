package funsets

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FunSetSuite extends FunSuite {
  import FunSets._

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
    val s12 = union(s1, s2)
    val s13 = union(s1, s3)
  }

  test("singletonSet(1) contains 1") {
    new TestSets {
      assert(contains(s1, 1), "singleton contains 1")
    }
  }

  test("union contains all elements") {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "union contains 1")
      assert(contains(s, 2), "union contains 2")
      assert(!contains(s, 3), "union does not contain 3")
    }
  }

  test("intersect contains all common elements") {
    new TestSets {
      val s = intersect(s12, s13)
      assert(contains(s, 1), "intersect contains 1")
      assert(!contains(s, 2), "intersect does not contain 2")
      assert(!contains(s, 3), "intersect does not contain 3")
    }
  }

  test("diff contains all from first set not in second") {
    new TestSets {
      val s = diff(s12, s1)
      assert(!contains(s, 1), "diff does not contain 1")
      assert(contains(s, 2), "diff contains 2")
    }
  }

  test("filter contains elements matching predicate") {
    new TestSets {
      val s = filter(s13, x => x % 3 != 0)
      assert(contains(s, 1), "filter contains 1")
      assert(!contains(s, 3), "filter does not contain 3")
    }
  }

  test("forall checks all bounded integers satisfy predicate") {
    new TestSets {
      assert(forall(s12, x => x >= 1), "forall s13 >= 1")
      assert(!forall(s12, x => x < 2), "forall s12 < 2")
    }
  }

  test("exists checks at least one element satisfies predicate") {
    new TestSets {
      assert(exists(s13, x => x > 2), "exists s13 > 2")
      assert(!exists(s1, x => x < 0), "exists s1 < 0")
    }
  }

  test("map applies a function to all items") {
    new TestSets {
      val s = map(s12, x => x * 2)
      assert(!contains(s, 1), "mapping does not contain 1")
      assert(contains(s, 2), "mapping contains 2")
      assert(contains(s, 4), "mapping contains 4")
    }
  }

  test("map works at high ranges") {
    new TestSets {
      val s1000 = union(s1, singletonSet(1000))
      val s = map(s1000, x => x - 1)
      assert(contains(s, 0), "mapping contains 0")
      assert(!contains(s, 1), "mapping does not contain 1")
      assert(contains(s, 999), "mapping contains 999")
      assert(!contains(s, 1000), "mapping does not contain 1000")
    }
  }
}
