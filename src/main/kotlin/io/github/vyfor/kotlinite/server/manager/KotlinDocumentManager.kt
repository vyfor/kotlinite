package io.github.vyfor.kotlinite.server.manager

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.util.FileContentUtilCore
import io.github.vyfor.kotlinite.io.FileRegistry
import io.github.vyfor.kotlinite.server.KotlinLanguageServer.Companion.logger
import io.github.vyfor.kotlinite.server.KotlinLanguageServer.Companion.scope
import io.github.vyfor.kotlinite.util.toURIPath
import java.net.URI
import kotlin.io.path.toPath
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.eclipse.lsp4j.CompletionList
import org.eclipse.lsp4j.CompletionParams
import org.eclipse.lsp4j.DidChangeTextDocumentParams
import org.eclipse.lsp4j.DidCloseTextDocumentParams
import org.eclipse.lsp4j.DidOpenTextDocumentParams
import org.eclipse.lsp4j.DidSaveTextDocumentParams
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.TextDocumentService
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.api.symbols.name

class KotlinDocumentManager(val fileRegistry: FileRegistry) : TextDocumentService {
  var session: StandaloneAnalysisAPISession? = null

  override fun completion(params: CompletionParams) =
      scope
          .async<Either<List<CompletionItem>, CompletionList>> {
            Either.forLeft(
                session
                    ?.project
                    ?.let { project ->
                      fileRegistry.fileInfos[params.textDocument.uri.toURIPath()]?.second?.let {
                          info ->
                        info.index?.let { index ->
                          index.declarations
                              .filterNot { decl -> decl.name == null }
                              .map { decl ->
                                CompletionItem(decl.name!!.identifier).apply {
                                  kind = CompletionItemKind.Function
                                }
                              }
                        }
                      }
                    }
                    ?.apply { logger.info("Completions: $this") } ?: emptyList())
          }
          .asCompletableFuture()

  override fun didOpen(params: DidOpenTextDocumentParams) {
    logger.info("Did open")
    fileRegistry.openFile(
        params.textDocument.uri.toURIPath(), params.textDocument.text, params.textDocument.version)
  }

  override fun didChange(params: DidChangeTextDocumentParams) {
    logger.info("Did change")
    val path = params.textDocument.uri.toURIPath()
    val file =
        fileRegistry.openFiles[path]
            ?: run {
              fileRegistry.openFile(
                  path, params.contentChanges[0].text, params.textDocument.version)
              return
            }

    FileDocumentManager.getInstance().getDocument(file)?.setText(params.contentChanges[0].text)
        ?: run { logger.warning("Document is null") }

    FileContentUtilCore.reparseFiles(file)
    logger.info("Re-parsed file")

    fileRegistry.indexFile(file)
    logger.info("Re-indexed file")
  }

  override fun didClose(params: DidCloseTextDocumentParams) {
    logger.info("Did close")
    fileRegistry.closeFile(URI(params.textDocument.uri).toPath())
  }

  override fun didSave(params: DidSaveTextDocumentParams) {}
}
