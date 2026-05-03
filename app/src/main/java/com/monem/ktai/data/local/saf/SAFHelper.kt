package com.monem.ktai.data.local.saf

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.monem.ktai.domain.model.FileNode
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SAFHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val contentResolver: ContentResolver get() = context.contentResolver

    private val supportedExtensions = setOf(
        "kt", "kts", "xml", "gradle", "json", "md", "txt",
        "java", "properties", "pro", "cfg", "yaml", "yml", "toml",
    )

    fun persistPermission(uri: Uri) {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, flags)
    }

    fun releasePermission(uri: Uri) {
        try {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.releasePersistableUriPermission(uri, flags)
        } catch (_: Exception) {
            // Permission may already be released
        }
    }

    fun hasPermission(uri: Uri): Boolean {
        return contentResolver.persistedUriPermissions.any {
            it.uri == uri && it.isReadPermission
        }
    }

    fun getPersistedUris(): List<Uri> {
        return contentResolver.persistedUriPermissions
            .filter { it.isReadPermission }
            .map { it.uri }
    }

    fun getFolderName(uri: Uri): String {
        val documentFile = DocumentFile.fromTreeUri(context, uri)
        return documentFile?.name ?: uri.lastPathSegment ?: "Unknown"
    }

    fun buildFileTree(folderUri: Uri): List<FileNode> {
        val rootDocument = DocumentFile.fromTreeUri(context, folderUri) ?: return emptyList()
        return listChildren(rootDocument, "")
    }

    private fun listChildren(parent: DocumentFile, parentPath: String): List<FileNode> {
        val children = parent.listFiles()
        val nodes = mutableListOf<FileNode>()

        // Sort: directories first, then files, both alphabetically
        val sorted = children.sortedWith(
            compareByDescending<DocumentFile> { it.isDirectory }
                .thenBy { it.name?.lowercase() },
        )

        for (child in sorted) {
            val name = child.name ?: continue
            val path = if (parentPath.isEmpty()) name else "$parentPath/$name"

            if (child.isDirectory) {
                // Skip hidden directories and build directories
                if (name.startsWith(".") || name == "build" || name == ".gradle") continue
                nodes.add(
                    FileNode(
                        name = name,
                        path = path,
                        isDirectory = true,
                        uri = child.uri.toString(),
                        size = 0,
                    ),
                )
            } else {
                val ext = name.substringAfterLast('.', "").lowercase()
                // Only include supported file types
                if (ext in supportedExtensions || name in listOf(
                        "gradlew", "gradlew.bat", "Makefile", "Dockerfile",
                        ".gitignore", ".editorconfig",
                    )
                ) {
                    nodes.add(
                        FileNode(
                            name = name,
                            path = path,
                            isDirectory = false,
                            uri = child.uri.toString(),
                            size = child.length(),
                        ),
                    )
                }
            }
        }
        return nodes
    }

    fun getChildrenOf(folderUri: Uri, relativePath: String): List<FileNode> {
        val rootDocument = DocumentFile.fromTreeUri(context, folderUri) ?: return emptyList()
        var current = rootDocument

        if (relativePath.isNotEmpty()) {
            for (segment in relativePath.split("/")) {
                current = current.findFile(segment) ?: return emptyList()
                if (!current.isDirectory) return emptyList()
            }
        }

        return listChildren(current, relativePath)
    }

    fun readFileContent(fileUri: Uri): Result<String> {
        return try {
            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                Result.success(content)
            } ?: Result.failure(Exception("Could not open file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun readFileContent(folderUri: Uri, relativePath: String): Result<String> {
        val rootDocument = DocumentFile.fromTreeUri(context, folderUri) ?: return Result.failure(
            Exception("Invalid folder URI"),
        )

        var current: DocumentFile = rootDocument
        val segments = relativePath.split("/")

        for (segment in segments) {
            current = current.findFile(segment) ?: return Result.failure(
                Exception("File not found: $relativePath"),
            )
        }

        return readFileContent(current.uri)
    }

    fun writeFileContent(fileUri: Uri, content: String): Result<Unit> {
        return try {
            contentResolver.openOutputStream(fileUri, "wt")?.use { outputStream ->
                outputStream.write(content.toByteArray())
                outputStream.flush()
            } ?: return Result.failure(Exception("Could not open file for writing"))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFileSize(fileUri: Uri): Long {
        val documentFile = DocumentFile.fromSingleUri(context, fileUri)
        return documentFile?.length() ?: 0
    }

    fun isSensitiveFile(fileName: String): Boolean {
        val sensitiveFiles = setOf(
            ".env", "keystore", "google-services.json",
            "local.properties", ".jks", ".keystore",
        )
        return sensitiveFiles.any { fileName.equals(it, ignoreCase = true) || fileName.endsWith(it) }
    }
}
