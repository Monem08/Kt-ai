package com.monem.ktai.presentation.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monem.ktai.presentation.common.theme.Accent
import com.monem.ktai.presentation.common.theme.Primary
import com.monem.ktai.presentation.common.theme.TextPrimary
import com.monem.ktai.presentation.common.theme.TextSecondary

private val CodeBlockBg = Color(0xFF0D1117)
private val CodeBlockBorder = Color(0xFF21262D)
private val InlineCodeBg = Color(0xFF1A1D24)

private val SyntaxKeyword = Color(0xFFFF7B72)
private val SyntaxString = Color(0xFFA5D6FF)
private val SyntaxComment = Color(0xFF8B949E)
private val SyntaxFunction = Color(0xFFD2A8FF)
private val SyntaxNumber = Color(0xFF79C0FF)
private val SyntaxAnnotation = Color(0xFFFFA657)
private val SyntaxType = Color(0xFF7EE787)
private val SyntaxPlain = Color(0xFFE6EDF3)

sealed class MarkdownSegment {
    data class TextBlock(val text: String) : MarkdownSegment()
    data class CodeBlock(val language: String, val code: String) : MarkdownSegment()
    data class Header(val level: Int, val text: String) : MarkdownSegment()
    data class BulletList(val items: List<String>) : MarkdownSegment()
    data class NumberedList(val items: List<String>) : MarkdownSegment()
}

fun parseMarkdown(text: String): List<MarkdownSegment> {
    val segments = mutableListOf<MarkdownSegment>()
    val lines = text.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        if (line.trimStart().startsWith("```")) {
            val language = line.trimStart().removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            if (i < lines.size) i++
            segments.add(MarkdownSegment.CodeBlock(language.ifBlank { "text" }, codeLines.joinToString("\n")))
            continue
        }

        if (line.startsWith("### ")) {
            segments.add(MarkdownSegment.Header(3, line.removePrefix("### ")))
            i++
            continue
        }
        if (line.startsWith("## ")) {
            segments.add(MarkdownSegment.Header(2, line.removePrefix("## ")))
            i++
            continue
        }
        if (line.startsWith("# ")) {
            segments.add(MarkdownSegment.Header(1, line.removePrefix("# ")))
            i++
            continue
        }

        if (line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ")) {
            val items = mutableListOf<String>()
            while (i < lines.size && (lines[i].trimStart().startsWith("- ") || lines[i].trimStart().startsWith("* "))) {
                items.add(lines[i].trimStart().removePrefix("- ").removePrefix("* "))
                i++
            }
            segments.add(MarkdownSegment.BulletList(items))
            continue
        }

        val numberedRegex = Regex("""^\d+\.\s+(.+)""")
        if (numberedRegex.matches(line.trimStart())) {
            val items = mutableListOf<String>()
            while (i < lines.size && numberedRegex.matches(lines[i].trimStart())) {
                items.add(numberedRegex.find(lines[i].trimStart())!!.groupValues[1])
                i++
            }
            segments.add(MarkdownSegment.NumberedList(items))
            continue
        }

        val textLines = mutableListOf<String>()
        while (i < lines.size) {
            val currentLine = lines[i]
            if (currentLine.trimStart().startsWith("```") ||
                currentLine.startsWith("# ") ||
                currentLine.startsWith("## ") ||
                currentLine.startsWith("### ") ||
                currentLine.trimStart().startsWith("- ") ||
                currentLine.trimStart().startsWith("* ") ||
                numberedRegex.matches(currentLine.trimStart())
            ) break
            textLines.add(currentLine)
            i++
        }
        val combined = textLines.joinToString("\n").trim()
        if (combined.isNotEmpty()) {
            segments.add(MarkdownSegment.TextBlock(combined))
        }
    }

    return segments
}

fun formatInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val chars = text.toCharArray()

        while (i < chars.size) {
            when {
                i + 1 < chars.size && chars[i] == '*' && chars[i + 1] == '*' -> {
                    val end = text.indexOf("**", i + 2)
                    if (end != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(i + 2, end))
                        }
                        i = end + 2
                    } else {
                        append(chars[i])
                        i++
                    }
                }
                chars[i] == '*' && (i == 0 || chars[i - 1] != '*') -> {
                    val end = text.indexOf('*', i + 1)
                    if (end != -1 && (end + 1 >= chars.size || chars[end + 1] != '*')) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(text.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        append(chars[i])
                        i++
                    }
                }
                chars[i] == '`' -> {
                    val end = text.indexOf('`', i + 1)
                    if (end != -1) {
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = InlineCodeBg,
                                color = Accent,
                                fontSize = 13.sp,
                            )
                        ) {
                            append(" ${text.substring(i + 1, end)} ")
                        }
                        i = end + 1
                    } else {
                        append(chars[i])
                        i++
                    }
                }
                else -> {
                    append(chars[i])
                    i++
                }
            }
        }
    }
}

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val segments = parseMarkdown(text)

    Column(
        modifier = modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        segments.forEach { segment ->
            when (segment) {
                is MarkdownSegment.TextBlock -> {
                    Text(
                        text = formatInlineMarkdown(segment.text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        lineHeight = 22.sp,
                    )
                }
                is MarkdownSegment.CodeBlock -> {
                    CodeBlockView(
                        code = segment.code,
                        language = segment.language,
                    )
                }
                is MarkdownSegment.Header -> {
                    val style = when (segment.level) {
                        1 -> MaterialTheme.typography.titleLarge
                        2 -> MaterialTheme.typography.titleMedium
                        else -> MaterialTheme.typography.titleSmall
                    }
                    Text(
                        text = formatInlineMarkdown(segment.text),
                        style = style,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                is MarkdownSegment.BulletList -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        segment.items.forEach { item ->
                            Row {
                                Text("  •  ", color = Primary, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    text = formatInlineMarkdown(item),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 22.sp,
                                )
                            }
                        }
                    }
                }
                is MarkdownSegment.NumberedList -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        segment.items.forEachIndexed { index, item ->
                            Row {
                                Text(
                                    "  ${index + 1}.  ",
                                    color = Primary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = formatInlineMarkdown(item),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    lineHeight = 22.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlockView(
    code: String,
    language: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CodeBlockBg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CodeBlockBorder)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = language,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
            )
            IconButton(
                onClick = { copyToClipboard(context, code) },
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(14.dp),
        ) {
            Text(
                text = highlightSyntax(code, language),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                ),
            )
        }
    }
}

fun highlightSyntax(code: String, language: String): AnnotatedString {
    val kotlinKeywords = setOf(
        "fun", "val", "var", "class", "object", "interface", "enum", "data", "sealed",
        "abstract", "open", "override", "private", "public", "protected", "internal",
        "if", "else", "when", "for", "while", "do", "return", "break", "continue",
        "try", "catch", "finally", "throw", "import", "package", "is", "as", "in",
        "null", "true", "false", "this", "super", "it", "by", "companion", "init",
        "suspend", "inline", "crossinline", "noinline", "reified", "typealias",
        "const", "lateinit", "lazy", "constructor", "annotation",
    )
    val xmlKeywords = setOf(
        "xmlns", "android", "app", "tools", "layout_width", "layout_height",
        "match_parent", "wrap_content", "id", "text", "src", "name",
    )
    val gradleKeywords = setOf(
        "implementation", "api", "plugins", "dependencies", "repositories",
        "apply", "id", "version", "classpath", "buildscript", "allprojects",
        "task", "group", "description", "dependsOn",
    )

    val keywords = when {
        language.contains("kotlin", ignoreCase = true) || language == "kt" -> kotlinKeywords
        language.contains("xml", ignoreCase = true) -> xmlKeywords
        language.contains("gradle", ignoreCase = true) || language == "kts" -> kotlinKeywords + gradleKeywords
        language.contains("java", ignoreCase = true) -> kotlinKeywords
        else -> kotlinKeywords
    }

    return buildAnnotatedString {
        var i = 0
        val chars = code.toCharArray()

        while (i < chars.size) {
            when {
                code.startsWith("//", i) -> {
                    val end = code.indexOf('\n', i).let { if (it == -1) code.length else it }
                    withStyle(SpanStyle(color = SyntaxComment, fontStyle = FontStyle.Italic)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                code.startsWith("/*", i) -> {
                    val end = (code.indexOf("*/", i).let { if (it == -1) code.length else it + 2 })
                    withStyle(SpanStyle(color = SyntaxComment, fontStyle = FontStyle.Italic)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                code.startsWith("<!--", i) -> {
                    val end = (code.indexOf("-->", i).let { if (it == -1) code.length else it + 3 })
                    withStyle(SpanStyle(color = SyntaxComment)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                chars[i] == '"' -> {
                    val end = findStringEnd(code, i, '"')
                    withStyle(SpanStyle(color = SyntaxString)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                chars[i] == '\'' -> {
                    val end = findStringEnd(code, i, '\'')
                    withStyle(SpanStyle(color = SyntaxString)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                chars[i] == '@' -> {
                    val end = findWordEnd(code, i + 1)
                    withStyle(SpanStyle(color = SyntaxAnnotation)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                chars[i].isDigit() && (i == 0 || !chars[i - 1].isLetterOrDigit()) -> {
                    val end = findNumberEnd(code, i)
                    withStyle(SpanStyle(color = SyntaxNumber)) {
                        append(code.substring(i, end))
                    }
                    i = end
                }
                chars[i].isLetter() || chars[i] == '_' -> {
                    val end = findWordEnd(code, i)
                    val word = code.substring(i, end)
                    when {
                        word in keywords -> {
                            withStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Medium)) {
                                append(word)
                            }
                        }
                        word.first().isUpperCase() -> {
                            withStyle(SpanStyle(color = SyntaxType)) {
                                append(word)
                            }
                        }
                        end < code.length && code[end] == '(' -> {
                            withStyle(SpanStyle(color = SyntaxFunction)) {
                                append(word)
                            }
                        }
                        else -> {
                            withStyle(SpanStyle(color = SyntaxPlain)) {
                                append(word)
                            }
                        }
                    }
                    i = end
                }
                else -> {
                    withStyle(SpanStyle(color = SyntaxPlain)) {
                        append(chars[i])
                    }
                    i++
                }
            }
        }
    }
}

private fun findStringEnd(code: String, start: Int, quote: Char): Int {
    var i = start + 1
    while (i < code.length) {
        if (code[i] == '\\') {
            i += 2
            continue
        }
        if (code[i] == quote) return i + 1
        if (code[i] == '\n') return i
        i++
    }
    return code.length
}

private fun findWordEnd(code: String, start: Int): Int {
    var i = start
    while (i < code.length && (code[i].isLetterOrDigit() || code[i] == '_')) i++
    return i
}

private fun findNumberEnd(code: String, start: Int): Int {
    var i = start
    while (i < code.length && (code[i].isDigit() || code[i] == '.' || code[i] == '_' ||
                code[i] == 'x' || code[i] == 'X' || code[i] == 'L' || code[i] == 'f' ||
                (code[i] in 'a'..'f') || (code[i] in 'A'..'F'))
    ) i++
    return i
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("code", text))
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}
