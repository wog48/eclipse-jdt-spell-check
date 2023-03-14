package camelcase.jdt.spelling.directory;

import java.util.ArrayList;
import java.util.List;

class TreeNode {

  private final char character;
  private final TreeNode[] successors;
  private boolean isWord;

  TreeNode(final char c) {
    this.character = c;
    this.successors = new TreeNode[26];
  }

  char getChar() {
    return character;
  }

  boolean isWord() {
    return isWord;
  }

  TreeNode addSuccessor(final char c) {
    final int index = c - 'a';
    if (successors[index] == null)
      successors[index] = new TreeNode(c);
    return successors[index];
  }

  TreeNode getSuccessor(final char c) {
    final int index = c - 'a';
    return successors[index];
  }

  List<TreeNode> getAllSuccessor() {
    final List<TreeNode> result = new ArrayList<>();
    for (final TreeNode successor : successors)
      if (successor != null)
        result.add(successor);
    return result;
  }

  void setIsWord(final boolean isWord) {
    this.isWord = isWord;
  }

  @Override
  public String toString() {
    return "TreeNode [character=" + character + "]";
  }

}
