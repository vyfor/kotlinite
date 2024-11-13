package io.github.vyfor.kotlinite

import io.github.vyfor.kotlinite.server.KotlinLanguageServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.eclipse.lsp4j.launch.LSPLauncher

fun main(): Unit = runBlocking {
  val server = KotlinLanguageServer()
  val launcher = LSPLauncher.createServerLauncher(server, System.`in`, System.out)

  launch { server.connect(launcher.remoteProxy) }.invokeOnCompletion { launcher.startListening() }
}
