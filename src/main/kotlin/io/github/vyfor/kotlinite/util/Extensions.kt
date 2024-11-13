@file:Suppress("NOTHING_TO_INLINE")

package io.github.vyfor.kotlinite.util

import java.net.URI
import kotlin.io.path.toPath

inline fun String.toURIPath() = URI(this).toPath()