/* author: Kevin Carruthers */
import org.junit.*;
import static org.junit.Assert.*;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class TestM {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

  @Before
  public void setUp() {
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void tearDown() {
    System.setOut(null);
  }


  @Test
  public void nodeCoverage() {
    M testClass = new M();

    testClass.m("", 0);
    assertEquals(outContent.toString(), "zero\n");
    outContent.reset();

    testClass.m("a", 1);
    assertEquals(outContent.toString(), "a\n");
    outContent.reset();

    testClass.m("bb", 2);
    assertEquals(outContent.toString(), "b\n");
    outContent.reset();
  }

  @Test
  public void edgeCoverage() {
    nodeCoverage();

    M testClass = new M();

    testClass.m("ccc", 3);
    assertEquals(outContent.toString(), "b\n");
    outContent.reset();
  }

  @Test
  public void edgePairCoverage() {
    edgeCoverage();
  }

  @Test
  public void primePathCoverage() {
    edgeCoverage();
  }
}

class M {
  public static void main(String [] argv){
    M obj = new M();
    if (argv.length > 0)
      obj.m(argv[0], argv.length);
  }

  public void m(String arg, int i) {
    int q = 1;
    A o = null;
    Impossible nothing = new Impossible();
    if (i == 0)
      q = 4;
    q++;
    switch (arg.length()) {
      case 0: q /= 2; break;
      case 1: o = new A(); new B(); q = 25; break;
      case 2: o = new A(); q = q * 100;
      default: o = new B(); break; 
    }
    if (arg.length() > 0) {
      o.m();
    } else {
      System.out.println("zero");
    }
    nothing.happened();
  }
}

class A {
  public void m() { 
    System.out.println("a");
  }
}

class B extends A {
  public void m() { 
    System.out.println("b");
  }
}

class Impossible{
  public void happened() {
    // "2b||!2b?", whatever the answer nothing happens here
  }
}
