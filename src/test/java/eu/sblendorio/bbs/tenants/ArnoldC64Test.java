package eu.sblendorio.bbs.tenants;

import eu.sblendorio.bbs.tenants.petscii.ArnoldC64;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ImmutableList;

class ArnoldC64Test {

    private static Stream<Arguments> pagePerPageGenerator() {
        return Stream.of(
            Arguments.of(0 ,0),
            Arguments.of(1, 0),
            Arguments.of(0, 1),
            Arguments.of(-2, -2),
            Arguments.of(Integer.MAX_VALUE, 2)
        );
    }

    @DisplayName("get posts from invalid page must return empty map")
    @ParameterizedTest(name = "page {0} and perPage {1} must return null")
    @MethodSource("pagePerPageGenerator")
    void getPosts(int page, int perPage) throws Exception {
        Map<Integer, ArnoldC64.Entry> posts = ArnoldC64.getPosts(ImmutableList.of(new ArnoldC64.Entry("")), page, perPage);
        assertThat(posts.entrySet(), is(empty()));
    }

    @DisplayName("Out of Range page must return empty map")
    @Test
    void getPosts2() throws Exception {
        Map<Integer, ArnoldC64.Entry> posts = ArnoldC64.getPosts(ImmutableList.of(new ArnoldC64.Entry("")), 2, 3);
        assertThat(posts.entrySet(), is(empty()));
    }

    @DisplayName("correct range must return all the values in the range")
    @Test
    void getPosts3() throws Exception {
        Map<Integer, ArnoldC64.Entry> posts = ArnoldC64.getPosts(ImmutableList.of(new ArnoldC64.Entry("")), 1, 10);
        assertThat(posts.entrySet(), not(empty()));
    }
}