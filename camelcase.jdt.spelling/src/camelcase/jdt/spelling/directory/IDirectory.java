package camelcase.jdt.spelling.directory;

import java.util.List;

public interface IDirectory {

  boolean contains(String word);

  List<String> getProposals(String incorrect);

}