package camelcase.jdt.spelling.quickfix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.ui.text.correction.AssistContext;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.checker.SpellingEvent;
import camelcase.jdt.spelling.directory.IDirectory;
import camelcase.jdt.spelling.parser.Fragment;
import camelcase.jdt.spelling.parser.Token;

@SuppressWarnings("restriction")
public class SpellingFixProcessor implements IQuickFixProcessor {

  private static final int MAX_ERRORS_AT_ONCE = 2;

  @Override
  public boolean hasCorrections(final ICompilationUnit unit, final int problemId) {
    return true;
  }

  @Override
  public IJavaCompletionProposal[] getCorrections(final IInvocationContext context, final IProblemLocation[] locations)
      throws CoreException {

    if (context instanceof AssistContext
        && context.getCoveringNode() instanceof SimpleName) {

      final SimpleName term = (SimpleName) context.getCoveringNode();
      if (term.resolveBinding() != null) {
        final Token token = new Token(term.resolveBinding().getJavaElement());
        final int position = context.getSelectionOffset() - term.getStartPosition();
        final ISpellChecker checker = SpellingPlugin.getInstance().getSpellChecker();
        final List<IDirectory> directories = SpellingPlugin.getInstance().getDictionaryFactory().getDirectories();

        if (checker != null && directories != null) {
          final List<SpellingEvent> spellEvents = determineSpellEvents(token, position, checker);
          final Map<Fragment, List<String>> r = buildReplacements(directories, spellEvents);
          final List<Map<Fragment, String>> preProposals = buildPreProposal(r);
          final List<IJavaCompletionProposal> proposals = asProposal(context, term, token, preProposals);
          createAddWordProposal(position, spellEvents).ifPresent(proposals::add);
          return proposals.toArray(new IJavaCompletionProposal[proposals.size()]);
        }
      }
    }
    return null;
  }

  private Optional<AddWordProposal> createAddWordProposal(final int position, final List<SpellingEvent> spellEvents) {

    return spellEvents.stream()
        .filter(f -> f.getFragment().getFragmentStart() <= position && f.getFragment().getFragmentEnd() >= position)
        .findFirst()
        .map(SpellingEvent::getFragment)
        .map(Fragment::getOriginalFragment)
        .map(AddWordProposal::new);
  }

  private List<IJavaCompletionProposal> asProposal(final IInvocationContext context, final SimpleName term,
      final Token token, final List<Map<Fragment, String>> preProposals) {

    final List<IJavaCompletionProposal> proposals = new ArrayList<>();
    for (final Map<Fragment, String> replace : preProposals) {
      final Token newToken = token.replaceFragments(replace);
      proposals.add(new CorrectionProposal((AssistContext) context, newToken));
    }
    return proposals;
  }

  private List<Map<Fragment, String>> buildPreProposal(final Map<Fragment, List<String>> r) {
    List<Map<Fragment, String>> preProposals = new ArrayList<>();
    for (final Fragment f : r.keySet()) {
      final List<Map<Fragment, String>> newProposals = new ArrayList<>();
      for (final String replaces : r.get(f))
        if (preProposals.isEmpty()) {
          final Map<Fragment, String> newProposal = new HashMap<>();
          newProposal.put(f, replaces);
          newProposals.add(newProposal);
        } else
          for (final Map<Fragment, String> pre : preProposals) {
            final Map<Fragment, String> newProposal = new HashMap<>(pre);
            newProposal.put(f, replaces);
            newProposals.add(newProposal);
          }
      preProposals = newProposals;
    }
    return preProposals;
  }

  private Map<Fragment, List<String>> buildReplacements(final List<IDirectory> directories,
      final List<SpellingEvent> spellEvents) {

    final Map<Fragment, List<String>> replacements = new HashMap<>();
    for (final SpellingEvent event : spellEvents) {
      final Fragment f = event.getFragment();
      final String error = f.getOriginalFragmentLower();

      replacements.put(f, directories.stream()
          .map(d -> d.getProposals(error))
          .flatMap(List::stream)
          .collect(Collectors.toList()));

    }
    return replacements;
  }

  private List<SpellingEvent> determineSpellEvents(final Token token, final int position,
      final ISpellChecker checker) {

    final List<SpellingEvent> events = checker.checkElement(token);

    final Optional<SpellingEvent> spellEvent = events.stream()
        .filter(e -> selected(e.getFragment(), position))
        .findFirst();

    if (events.size() > MAX_ERRORS_AT_ONCE && spellEvent.isPresent())
      return Collections.singletonList(spellEvent.get());
    else if (spellEvent.isPresent())
      return events;
    return Collections.emptyList();
  }

  private boolean selected(final Fragment token, final int pos) {
    if (token.getFragmentStart() <= pos && token.getFragmentEnd() >= pos)
      return true;
    return false;
  }
}
