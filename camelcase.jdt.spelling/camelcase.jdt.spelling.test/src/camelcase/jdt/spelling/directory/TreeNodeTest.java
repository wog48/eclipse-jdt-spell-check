package camelcase.jdt.spelling.directory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TreeNodeTest {

  private TreeNode cut;

  @Test
  void checkGetChar() {
    assertEquals('l', new TreeNode('l').getChar());
  }

  @Test
  void checkAddSuccessor() {
    cut = new TreeNode('l');
    final TreeNode act = cut.addSuccessor('i');
    assertNotNull(act);
    assertEquals('i', act.getChar());
  }

  @Test
  void checkAddSuccessorExist() {
    cut = new TreeNode('l');
    final TreeNode first = cut.addSuccessor('i');
    final TreeNode act = cut.addSuccessor('i');
    assertEquals(first, act);
  }

  @Test
  void checkGetSuccessorExists() {
    cut = new TreeNode('l');
    final TreeNode act = cut.addSuccessor('i');
    assertEquals(act, cut.getSuccessor('i'));
  }

  @Test
  void checkGetSuccessorNotExists() {
    cut = new TreeNode('l');
    cut.addSuccessor('i');
    assertNull(cut.getSuccessor('e'));
  }

  @Test
  void checkGetAllSuccessors() {
    cut = new TreeNode('l');
    cut.addSuccessor('i');
    cut.addSuccessor('e');
    cut.addSuccessor('z');
    final List<TreeNode> act = cut.getAllSuccessor();

    assertEquals(3, act.size());
    assertTrue(act.stream().filter(n -> n.getChar() == 'i').findFirst().isPresent());
    assertTrue(act.stream().filter(n -> n.getChar() == 'e').findFirst().isPresent());
    assertTrue(act.stream().filter(n -> n.getChar() == 'z').findFirst().isPresent());
  }
}
