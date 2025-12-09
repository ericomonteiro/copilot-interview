package com.github.ericomonteiro.pirateparrotai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakaztahir.codeeditor.model.CodeLang
import com.wakaztahir.codeeditor.prettify.PrettifyParser
import com.wakaztahir.codeeditor.theme.CodeThemeType
import com.wakaztahir.codeeditor.utils.parseCodeAsAnnotatedString

@Composable
fun CodeDisplay(
    code: String,
    language: String = "kotlin",
    showLineNumbers: Boolean = true,
    modifier: Modifier = Modifier
) {
    val parser = remember { PrettifyParser() }
    val theme = remember { CodeThemeType.Monokai.theme }
    
    val codeLang = remember(language) {
        when (language.lowercase()) {
            "kotlin", "kt" -> CodeLang.Kotlin
            "java" -> CodeLang.Java
            "python", "py" -> CodeLang.Python
            "javascript", "js" -> CodeLang.JavaScript
            "c++", "cpp" -> CodeLang.CPP
            "go" -> CodeLang.Go
            "rust", "rs" -> CodeLang.Rust
            else -> CodeLang.Default
        }
    }
    
    val parsedCode = remember(code, codeLang) {
        parseCodeAsAnnotatedString(
            parser = parser,
            theme = theme,
            lang = codeLang,
            code = code
        )
    }
    
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF272822)) // Monokai background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScrollState)
        ) {
            // Line numbers
            if (showLineNumbers) {
                val lines = code.lines()
                Column(
                    modifier = Modifier
                        .background(Color(0xFF1E1F1C))
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .verticalScroll(verticalScrollState, enabled = false)
                ) {
                    lines.forEachIndexed { index, _ ->
                        Text(
                            text = "${index + 1}",
                            style = LocalTextStyle.current.copy(
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF75715E),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
            
            // Code content with syntax highlighting
            SelectionContainer {
                Text(
                    text = parsedCode,
                    style = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .verticalScroll(verticalScrollState)
                )
            }
        }
    }
}
