package com.robintegg.web.theme.pages;

import com.robintegg.web.engine.Page;
import com.robintegg.web.engine.RenderModel;
import com.robintegg.web.tags.TagPlugin;
import com.robintegg.web.utils.Utils;
import j2html.tags.DomContent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class TagsPage {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Page create() {

        return Page.builder()
                .path("/tags/index.html")
                .includeMenu(true)
                .data(Map.of(
                        "layout", List.of("default"),
                        "title", List.of("Tags"),
                        "permalink", List.of("/tags")
                ))
                .renderFunction(TagsPage::render)
                .build();
    }

    public static DomContent render(RenderModel renderModel) {
        // Group tags alphabetically
        Map<Character, List<String>> tagsByLetter = TagPlugin.INSTANCE.getTags().stream()
                .filter(tag -> tag != null && !tag.isEmpty())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.groupingBy(
                        tag -> Character.toUpperCase(tag.charAt(0)),
                        TreeMap::new,
                        Collectors.toList()
                ));

        // Get all letters that have tags
        Set<Character> availableLetters = tagsByLetter.keySet();

        return div()
                .withClass("home")
                .with(
                        iff(
                                renderModel.getPage().getTitle() != null,
                                h1()
                                        .withClass("page-heading")
                                        .withText(renderModel.getPage().getTitle())
                        ),
                        // Alphabet navigation
                        nav()
                                .withClass("tags-alphabet-nav")
                                .with(
                                        each(ALPHABET.chars()
                                                .mapToObj(c -> (char) c)
                                                .toList(), letter -> {
                                            if (availableLetters.contains(letter)) {
                                                return a()
                                                        .withClass("alphabet-link")
                                                        .withHref("#" + letter)
                                                        .withText(String.valueOf(letter));
                                            } else {
                                                return span()
                                                        .withClass("alphabet-link disabled")
                                                        .withText(String.valueOf(letter));
                                            }
                                        })
                                ),
                        // Tag groups in columns
                        div()
                                .withClass("tags-columns")
                                .with(
                                        each(tagsByLetter.entrySet(), entry -> {
                                            Character letter = entry.getKey();
                                            List<String> tags = entry.getValue();
                                            return div()
                                                    .withClass("tag-group")
                                                    .with(
                                                            h2()
                                                                    .withClass("tag-group-heading")
                                                                    .withId(String.valueOf(letter))
                                                                    .withText(String.valueOf(letter)),
                                                            ul()
                                                                    .withClass("tag-list")
                                                                    .with(
                                                                            each(tags, tag ->
                                                                                    li().with(
                                                                                            a()
                                                                                                    .withClass("tag-link")
                                                                                                    .withHref(Utils.relativeUrl("/tags/" + tag))
                                                                                                    .withText(Utils.capitalize(tag))
                                                                                    )
                                                                            )
                                                                    )
                                                    );
                                        })
                                )
                );
    }


}
