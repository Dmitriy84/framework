package main.kotlin.io.tzero.aqa.framework.utils

import com.savvasdalkitsis.jsonmerger.JsonMerger
import net.javacrumbs.jsonunit.assertj.JsonAssertions

object JsonUtils {
    fun String.toBe(expected: String, vararg ignore: String) {
        JsonAssertions.assertThatJson(this)
            .whenIgnoringPaths(*ignore)
            .isEqualTo(expected)
    }

    fun String.merge(override: String) = JsonMerger(objectMergeMode = JsonMerger.ObjectMergeMode.MERGE_OBJECT)
        .merge(this, override)

    fun String.merge(override: Map<String, Any>) =
        merge(BaseTest.json.encodeToString(KotlinxGenericMapSerializer, override))
}
