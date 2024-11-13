package io.github.vyfor.kotlinite.io

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import io.github.vyfor.kotlinite.server.KotlinLanguageServer.Companion.logger
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.name
import kotlin.to
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.standalone.StandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.api.symbols.KaDeclarationSymbol
import org.jetbrains.kotlin.psi.KtFile

data class FileInfo(
    val version: Int = 0,
    val lastIndexed: Instant = Instant.MIN,
    val index: FileIndex? = null
)

data class FileIndex(val declarations: List<KaDeclarationSymbol>, val imports: List<String>)

class FileRegistry {
  val fileInfos = mutableMapOf<Path, Pair<VirtualFile, FileInfo>>()
  val openFiles = mutableMapOf<Path, LightVirtualFile>()
  var session: StandaloneAnalysisAPISession? = null

  fun registerFiles(session: StandaloneAnalysisAPISession) {
    this.session = session

    session.modulesWithFiles.entries.firstOrNull()?.value?.forEach {
      val file = it.virtualFile
      fileInfos[file.toNioPath()] = file to FileInfo(index = indexFile(file))
    } ?: run { logger.warning("No files found in session") }
  }

  fun openFile(path: Path, content: String, version: Int): FileInfo {
    val file = LightVirtualFile(path.name, content)
    openFiles[path] = file
    val fileInfo =
        FileInfo(
            version = version,
        )

    fileInfos[path] = file to fileInfo
    return fileInfo
  }

  @OptIn(KaExperimentalApi::class)
  fun indexFile(file: VirtualFile): FileIndex? {
    val session = session ?: return null
    val ktFile = PsiManager.getInstance(session.project).findFile(file) as? KtFile

    return ktFile?.let { file ->
      analyze(file) {
        logger.info("Analyzing file ${file.name}")
        FileIndex(
            declarations = file.declarations.map { it.symbol },
            imports = file.importDirectives.mapNotNull { it.importedFqName?.asString() },
        )
      }
    }
  }

  fun closeFile(path: Path) {
    openFiles.remove(path)
  }
}
