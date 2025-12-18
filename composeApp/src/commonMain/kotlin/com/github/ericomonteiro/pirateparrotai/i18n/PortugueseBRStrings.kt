package com.github.ericomonteiro.pirateparrotai.i18n

object PortugueseBRStrings : StringResources {
    // App General
    override val appName = "Pirate-Parrot"
    override val settings = "Configurações"
    override val close = "Fechar"
    override val save = "Salvar"
    override val cancel = "Cancelar"
    override val copy = "Copiar"
    override val clear = "Limpar"
    override val reload = "Recarregar"
    override val tips = "Dicas"
    
    // Home Screen
    override val homeWhatIs = "O que é o Pirate-Parrot?"
    override val homeWhatIsTitle = "O que é o Pirate-Parrot?"
    override val homeWhatIsContent = """
        Pirate-Parrot é um aplicativo desktop projetado para ajudá-lo durante entrevistas de código e exames de certificação.
        
        Ele captura sua tela, analisa o conteúdo usando a IA Gemini do Google e fornece:
        
        • Soluções de código com explicações para desafios de programação
        • Respostas corretas com explicações detalhadas para questões de certificação
        • Análise de complexidade de tempo e espaço
        • Dicas de exame e serviços relacionados
        
        O app roda em "modo stealth" - é invisível para softwares de compartilhamento e gravação de tela.
    """.trimIndent()
    override val homeConfigTitle = "O Que Você Precisa Configurar"
    override val homeConfigContent = """
        Antes de usar o app, você precisa configurar:
        
        1. Chave da API Gemini (Obrigatório)
           • Acesse o Google AI Studio (aistudio.google.com)
           • Crie uma chave de API gratuita
           • Cole em Configurações -> Chave da API
        
        2. Selecionar Modelo de IA (Opcional)
           • Padrão: gemini-2.5-flash (recomendado)
           • Para problemas complexos: gemini-2.5-pro
        
        3. Linguagem Padrão (Opcional)
           • Escolha sua linguagem de programação preferida
        
        4. Modo Stealth (Ativado por padrão)
           • Esconde o app da captura de tela
    """.trimIndent()
    override val homeOpenSettings = "Abrir Configurações"
    override val homeHowToUseTitle = "Como Usar"
    override val homeHowToUseContent = """
        O app tem dois modos principais:
        
        Desafios de Código
        • Navegue até um problema de código (LeetCode, HackerRank, etc.)
        • Pressione Cmd+Shift+Opt+S ou clique em "Capturar"
        • A IA irá analisar e fornecer uma solução
        
        Certificação AWS
        • Abra sua questão do exame de certificação
        • Selecione o tipo de certificação
        • Pressione Cmd+Shift+Opt+S ou clique em "Capturar"
        • Obtenha a resposta correta com explicações
        
        Atalhos de Teclado
        • Cmd+Shift+Opt+S -> Capturar & Analisar
        • Cmd+Shift+Opt+B -> Alternar Modo Stealth
        
        Dicas
        • Certifique-se de que a questão está totalmente visível
        • Verifique o Histórico de Capturas para solução de problemas
    """.trimIndent()
    override val homeGetStarted = "Começar"
    override val homeCodeChallenge = "Desafio de Código"
    override val homeCodeChallengeDesc = "Resolver problemas de programação"
    override val homeCertification = "Certificação"
    override val homeCertificationDesc = "Questões de exames AWS"
    override val homeGenericExam = "Prova Genérica"
    override val homeGenericExamDesc = "ENEM, Vestibular, Concursos"
    override val homeYourAiCompanion = "Seu companheiro de estudos com IA"
    
    // Settings Screen
    override val settingsGeminiApiKey = "Chave da API Gemini"
    override val settingsApiKeyPlaceholder = "sk-..."
    override val settingsGetApiKey = "Obtenha sua chave da API em"
    override val settingsAiModel = "Modelo de IA"
    override val settingsDefaultLanguage = "Linguagem Padrão"
    override val settingsDefaultLanguageHint = "Esta linguagem será pré-selecionada ao analisar desafios"
    override val settingsStealthFeatures = "Recursos Stealth"
    override val settingsHideFromCapture = "Esconder da Captura de Tela"
    override val settingsHideFromCaptureDesc = "Torna a janela invisível no Zoom, Meet, Teams"
    override val settingsCaptureRegion = "Região de Captura"
    override val settingsUseCustomRegion = "Usar Região Personalizada"
    override val settingsCaptureEntireScreen = "Capturar tela inteira"
    override val settingsRegionInfo = "Região: %dx%d em (%d, %d)"
    override val settingsSelectRegion = "Selecionar Região"
    override val settingsDefineRegionHint = "Defina uma área específica da tela para capturar em vez da tela inteira"
    override val settingsTroubleshooting = "Solução de Problemas"
    override val settingsScreenshotHistory = "Histórico de Capturas"
    override val settingsScreenshotHistoryDesc = "Visualize capturas de tela e resultados de análise para depuração"
    override val settingsTestApiConnection = "Testar Conexão da API"
    override val settingsTipsContent = """
        Atalhos Globais (funcionam mesmo quando o app não está em foco):
        • Cmd+Shift+Opt+S - Capturar tela & analisar
        • Cmd+Shift+Opt+B - Alternar modo stealth

        • O código da solução é copiado automaticamente!
        • Gemini é completamente GRATUITO (não precisa de cartão de crédito)
    """.trimIndent()
    override val settingsAppLanguage = "Idioma do App"
    override val settingsAppLanguageHint = "Selecione o idioma da interface do aplicativo"
    override val settingsSystemDefault = "Padrão do Sistema"
    
    // Screenshot Analysis
    override val screenshotCapture = "Capturar"
    override val screenshotAnalyzing = "Analisando..."
    override val screenshotNoImage = "Nenhuma imagem capturada"
    override val screenshotCaptureHint = "Pressione Cmd+Shift+Opt+S ou clique em Capturar"
    override val screenshotSolution = "Solução"
    override val screenshotCopied = "Copiado!"
    override val screenshotCopyCode = "Copiar Código"
    override val screenshotSelectLanguage = "Selecionar Linguagem"
    
    // Certification Analysis
    override val certificationTitle = "Análise de Certificação"
    override val certificationSelectType = "Selecionar Tipo de Certificação"
    override val certificationAnswer = "Resposta"
    override val certificationExplanation = "Explicação"
    override val certificationRelatedServices = "Serviços Relacionados"
    override val certificationExamTips = "Dicas do Exame"
    
    // Generic Exam
    override val genericExamTitle = "Prova Genérica"
    override val genericExamSelectType = "Selecionar Tipo de Prova"
    override val genericExamAnswer = "Resposta"
    
    // History
    override val historyTitle = "Histórico de Capturas"
    override val historyEmpty = "Nenhuma captura de tela ainda"
    override val historyDelete = "Excluir"
    override val historyViewDetails = "Ver Detalhes"
    
    // Errors
    override val errorApiKeyNotSet = "Erro: Chave da API não configurada"
    override val errorAnalysisFailed = "Falha na análise"
    override val errorCaptureFailed = "Falha na captura de tela"
    override val errorNetworkFailed = "Erro de rede"
    
    // Code Challenge
    override val codeChallengeTitle = "Desafio de Código"
    override val codeChallengeProblem = "Problema"
    override val codeChallengeSolution = "Solução"
    override val codeChallengeComplexity = "Complexidade"
    override val codeChallengeTimeComplexity = "Complexidade de Tempo"
    override val codeChallengeSpaceComplexity = "Complexidade de Espaço"
}
