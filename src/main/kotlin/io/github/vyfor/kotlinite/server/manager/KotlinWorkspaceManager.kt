package io.github.vyfor.kotlinite.server.manager

import org.eclipse.lsp4j.DidChangeConfigurationParams
import org.eclipse.lsp4j.DidChangeWatchedFilesParams
import org.eclipse.lsp4j.services.WorkspaceService

class KotlinWorkspaceManager : WorkspaceService {
  override fun didChangeConfiguration(params: DidChangeConfigurationParams) {}

  override fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams) {}
}
