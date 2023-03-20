package camelcase.jdt.spelling.directory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Directory implements IDirectory {
  private static final short MAX_DISTANCE = 2;
  private static final short DISTANCE_COLUMN = 1;
  private static final short RELATIVE_COLUMN = 0;
  private final TreeNode root = new TreeNode(' ');
  private short maxLevels = 0;

  Directory add(final String word) {
    final char[] chars = word.toCharArray();
    TreeNode node = root;
    for (final char character : chars)
      node = node.addSuccessor(character);
    node.setIsWord(true);
    if (word.length() > maxLevels)
      maxLevels = (short) word.length();
    return this;
  }

  @Override
  public boolean contains(final String word) {
    final char[] chars = word.toCharArray();
    TreeNode node = root;
    for (final char character : chars) {
      node = node.getSuccessor(character);
      if (node == null)
        return false;
    }
    return node.isWord();
  }

  short maxLevels() {
    return maxLevels;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getProposals(final String incorrect) {

    final List<String> result = new ArrayList<>();
    final List<TreeNode>[] stack = new List[maxLevels + 1];
    final short[][] distance = new short[maxLevels + 1][2];
    final char[] proposal = new char[incorrect.length() + MAX_DISTANCE];
    final char[] chars = createIncorrectCharArray(incorrect);

    short level = 0;

    distance[level][DISTANCE_COLUMN] = distance[level][RELATIVE_COLUMN] = 0;
    final List<TreeNode> rootNode = new ArrayList<TreeNode>();

    final TreeNode node = root.getSuccessor(chars[level]);
    if (node == null) return Collections.emptyList();
    stack[level] = rootNode;
    rootNode.add(node);

    while (stack[0] != null)
      if (!stack[level].isEmpty()) {
        final TreeNode next = stack[level].remove(stack[level].size() - 1);
        proposal[level] = next.getChar();
        if (proposal[level] != chars[level - distance[level][RELATIVE_COLUMN]]) {
          if (distance[level][DISTANCE_COLUMN] != 0 && proposal[level] == chars[level - 1])
            distance[level][RELATIVE_COLUMN]++; // Deletion
          else {
            if (proposal[level] == chars[level + 1])
              distance[level][RELATIVE_COLUMN]--; // Insertion
            distance[level][DISTANCE_COLUMN]++;
          }
          if (distance[level][DISTANCE_COLUMN] >= MAX_DISTANCE) {
            initializeLevel(distance, level);
            continue;
          }

        }
        level++;
        stack[level] = next.getAllSuccessor();
        initializeLevel(distance, level);

        if (next.isWord()) {
          boolean initialize = false;
          if (stack[level].isEmpty()) {
            stack[level] = null;
            distance[level] = null;
            proposal[level] = ' ';
            level--;
            initialize = true;
          }
          final String candidate = new String(proposal).trim();
          final short offset = (short) (incorrect.length() - candidate.length()
              + distance[level][RELATIVE_COLUMN]);
          if (distance[level][DISTANCE_COLUMN] + offset < MAX_DISTANCE)
            result.add(candidate);
          if (initialize)
            initializeLevel(distance, level);
        }
      } else {
        stack[level] = null;
        distance[level] = null;
        proposal[level] = ' ';
        level--;
        initializeLevel(distance, level);
      }
    return result;

  }

  private char[] createIncorrectCharArray(final String incorrect) {
    char[] result;
    if (incorrect.length() < maxLevels)
      result = new char[maxLevels + 1];
    else
      result = new char[incorrect.length() + 1];
    for (int i = 0; i < incorrect.length(); i++)
      result[i] = incorrect.charAt(i);
    return result;
  }

  private void initializeLevel(final short[][] distance, final short level) {
    if (level > 0) {
      distance[level] = new short[2];
      distance[level][DISTANCE_COLUMN] = distance[level - 1][DISTANCE_COLUMN];
      distance[level][RELATIVE_COLUMN] = distance[level - 1][RELATIVE_COLUMN];
    }
  }

}
