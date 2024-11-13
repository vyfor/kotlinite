package io.github.vyfor.kotlinite.analysis

import java.nio.file.Path
import kotlin.io.path.Path
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms

fun buildAnalysisSession(sources: List<Path>, deps: List<Path> = emptyList()) =
    buildStandaloneAnalysisAPISession {
      buildKtModuleProvider {
        platform = JvmPlatforms.defaultJvmPlatform

        val dependencies =
            mutableListOf(
                buildKtLibraryModule {
                  libraryName = "jvm-stdlib"
                  platform = JvmPlatforms.defaultJvmPlatform
                  addBinaryRoot(Path(System.getProperty("java.home")))
                })

        deps.forEach {
          dependencies.add(
              buildKtLibraryModule {
                libraryName = "dep"
                platform = JvmPlatforms.defaultJvmPlatform
                addBinaryRoot(it)
              })
        }

        addModule(
            buildKtSourceModule {
              moduleName = "main"
              platform = JvmPlatforms.defaultJvmPlatform

              addSourceRoots(sources)

              dependencies.forEach(::addRegularDependency)
            })
      }
    }
