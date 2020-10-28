package pro.taskana.common.rest.models;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.hypermedia.LinksSnippet;

import pro.taskana.common.internal.util.Pair;

public class PagingFieldDescriptors {

  public static final List<LinkDescriptor> LINKS;

  public static final LinksSnippet PAGING_LINKS = links(halLinks(),
      linkWithRel("self").description("Canonical self link"),
      linkWithRel("first").optional().description("The first page of results"),
      linkWithRel("last").optional().description("The last page of results"),
      linkWithRel("next").optional().description("The next page of results"),
      linkWithRel("prev").optional().description("The previous page of results"));

  static {
    LINKS =
        Stream.of(
                Pair.of("first", "Link to first page"),
                Pair.of("last", "link to last page"),
                Pair.of("next", "link to next page"),
                Pair.of("prev", "link to previous page"))
            .map(p -> linkWithRel(p.getLeft()).description(p.getRight()).optional())
            .collect(Collectors.toList());
  }
}
