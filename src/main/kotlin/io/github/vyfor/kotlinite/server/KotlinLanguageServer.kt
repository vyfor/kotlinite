package io.github.vyfor.kotlinite.server

import io.github.vyfor.kotlinite.analysis.buildAnalysisSession
import io.github.vyfor.kotlinite.io.FileRegistry
import io.github.vyfor.kotlinite.server.manager.KotlinDocumentManager
import io.github.vyfor.kotlinite.server.manager.KotlinWorkspaceManager
import io.github.vyfor.kotlinite.util.toURIPath
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger
import kotlin.system.exitProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.eclipse.lsp4j.CompletionOptions
import org.eclipse.lsp4j.InitializeParams
import org.eclipse.lsp4j.InitializeResult
import org.eclipse.lsp4j.ServerCapabilities
import org.eclipse.lsp4j.ServerInfo
import org.eclipse.lsp4j.TextDocumentSyncKind
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.LanguageClient
import org.eclipse.lsp4j.services.LanguageClientAware
import org.eclipse.lsp4j.services.LanguageServer
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession

class KotlinLanguageServer : LanguageServer, LanguageClientAware {
  var client: LanguageClient? = null
  var session: StandaloneAnalysisAPISession? = null
    set(value) {
      field = value
      documentManager.session = value
    }

  val fileRegistry = FileRegistry()
  val documentManager = KotlinDocumentManager(fileRegistry)
  val workspaceManager = KotlinWorkspaceManager()

  override fun initialize(params: InitializeParams): CompletableFuture<InitializeResult> {
    logger.info("Initialize requested")
    scope.launch {
      val workspaces =
          params.workspaceFolders.map { workspace ->
            workspace.uri.toURIPath()
          } // todo: resolve modules

      session = buildAnalysisSession(workspaces) // todo: resolve dependencies
      fileRegistry.registerFiles(session!!)
    }

    return CompletableFuture.completedFuture(InitializeResult(capabilities, information))
  }

  override fun shutdown(): CompletableFuture<Any> {
    scope.cancel("Shutdown requested")
    return CompletableFuture.completedFuture<Any>(Unit)
  }

  override fun exit() {
    logger.info("Exit requested")
    exitProcess(0)
  }

  override fun getTextDocumentService() = documentManager

  override fun getWorkspaceService() = workspaceManager

  override fun connect(client: LanguageClient) {
    this.client = client
    logger.info("Initialized client")
  }

  private val information = ServerInfo("Kotlinite")
  private val capabilities =
      ServerCapabilities().apply {
        textDocumentSync = Either.forLeft(TextDocumentSyncKind.Full)
        completionProvider = CompletionOptions(false, listOf(".", ":", "::"))
      }

  companion object {
    val logger = Logger.getLogger(KotlinLanguageServer::class.java.name)
    val scope = CoroutineScope(Dispatchers.IO)
  }
}
