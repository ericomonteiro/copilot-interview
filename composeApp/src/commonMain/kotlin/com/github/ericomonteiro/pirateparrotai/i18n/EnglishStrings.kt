package com.github.ericomonteiro.pirateparrotai.i18n

object EnglishStrings : StringResources {
    // App General
    override val appName = "Pirate-Parrot"
    override val settings = "Settings"
    override val close = "Close"
    override val save = "Save"
    override val cancel = "Cancel"
    override val copy = "Copy"
    override val clear = "Clear"
    override val reload = "Reload"
    override val tips = "Tips"
    
    // Home Screen
    override val homeWhatIs = "What is Pirate-Parrot?"
    override val homeWhatIsTitle = "What is Pirate-Parrot?"
    override val homeWhatIsContent = """
        Pirate-Parrot is a desktop application designed to help you during coding interviews and certification exams.
        
        It captures your screen, analyzes the content using Google's Gemini AI, and provides:
        
        • Code solutions with explanations for coding challenges
        • Correct answers with detailed explanations for certification questions
        • Time and space complexity analysis
        • Exam tips and related services
        
        The app runs in "stealth mode" - it's invisible to screen sharing and recording software.
    """.trimIndent()
    override val homeConfigTitle = "What You Need to Configure"
    override val homeConfigContent = """
        Before using the app, you need to set up:
        
        1. Gemini API Key (Required)
           • Go to Google AI Studio (aistudio.google.com)
           • Create a free API key
           • Paste it in Settings -> API Key
        
        2. Select AI Model (Optional)
           • Default: gemini-2.5-flash (recommended)
           • For complex problems: gemini-2.5-pro
        
        3. Default Language (Optional)
           • Choose your preferred programming language
        
        4. Stealth Mode (Enabled by default)
           • Hides app from screen capture
    """.trimIndent()
    override val homeOpenSettings = "Open Settings"
    override val homeHowToUseTitle = "How to Use"
    override val homeHowToUseContent = """
        The app has two main modes:
        
        Code Challenges
        • Navigate to a coding problem (LeetCode, HackerRank, etc.)
        • Press Cmd+Shift+Opt+S or click "Capture"
        • The AI will analyze and provide a solution
        
        AWS Certification
        • Open your certification exam question
        • Select the certification type
        • Press Cmd+Shift+Opt+S or click "Capture"
        • Get the correct answer with explanations
        
        Keyboard Shortcuts
        • Cmd+Shift+Opt+S -> Capture & Analyze
        • Cmd+Shift+Opt+B -> Toggle Stealth Mode
        
        Tips
        • Make sure the question is fully visible
        • Check Screenshot History for troubleshooting
    """.trimIndent()
    override val homeGetStarted = "Get Started"
    override val homeCodeChallenge = "Code Challenge"
    override val homeCodeChallengeDesc = "Solve coding problems"
    override val homeCertification = "Certification"
    override val homeCertificationDesc = "AWS exam questions"
    override val homeGenericExam = "Generic Exam"
    override val homeGenericExamDesc = "ENEM, Vestibular, Concursos"
    override val homeYourAiCompanion = "Your AI-powered study companion"
    
    // Settings Screen
    override val settingsGeminiApiKey = "Gemini API Key"
    override val settingsApiKeyPlaceholder = "sk-..."
    override val settingsGetApiKey = "Get your API key from"
    override val settingsAiModel = "AI Model"
    override val settingsDefaultLanguage = "Default Language"
    override val settingsDefaultLanguageHint = "This language will be pre-selected when analyzing challenges"
    override val settingsStealthFeatures = "Stealth Features"
    override val settingsHideFromCapture = "Hide from Screen Capture"
    override val settingsHideFromCaptureDesc = "Makes window invisible in Zoom, Meet, Teams"
    override val settingsCaptureRegion = "Capture Region"
    override val settingsUseCustomRegion = "Use Custom Region"
    override val settingsCaptureEntireScreen = "Capture entire screen"
    override val settingsRegionInfo = "Region: %dx%d at (%d, %d)"
    override val settingsSelectRegion = "Select Region"
    override val settingsDefineRegionHint = "Define a specific screen area to capture instead of the entire screen"
    override val settingsTroubleshooting = "Troubleshooting"
    override val settingsScreenshotHistory = "Screenshot History"
    override val settingsScreenshotHistoryDesc = "View captured screenshots and analysis results for debugging"
    override val settingsTestApiConnection = "Test API Connection"
    override val settingsTipsContent = """
        Global Hotkeys (work even when app is not focused):
        • Cmd+Shift+Opt+S - Capture screenshot & analyze
        • Cmd+Shift+Opt+B - Toggle stealth mode

        • Solution code is auto-copied to clipboard!
        • Gemini is completely FREE (no credit card needed)
    """.trimIndent()
    override val settingsAppLanguage = "App Language"
    override val settingsAppLanguageHint = "Select the language for the application interface"
    override val settingsSystemDefault = "System Default"
    
    // Screenshot Analysis
    override val screenshotCapture = "Capture"
    override val screenshotAnalyzing = "Analyzing..."
    override val screenshotNoImage = "No image captured"
    override val screenshotCaptureHint = "Press Cmd+Shift+Opt+S or click Capture"
    override val screenshotSolution = "Solution"
    override val screenshotCopied = "Copied!"
    override val screenshotCopyCode = "Copy Code"
    override val screenshotSelectLanguage = "Select Language"
    
    // Certification Analysis
    override val certificationTitle = "Certification Analysis"
    override val certificationSelectType = "Select Certification Type"
    override val certificationAnswer = "Answer"
    override val certificationExplanation = "Explanation"
    override val certificationRelatedServices = "Related Services"
    override val certificationExamTips = "Exam Tips"
    
    // Generic Exam
    override val genericExamTitle = "Generic Exam"
    override val genericExamSelectType = "Select Exam Type"
    override val genericExamAnswer = "Answer"
    
    // History
    override val historyTitle = "Screenshot History"
    override val historyEmpty = "No screenshots captured yet"
    override val historyDelete = "Delete"
    override val historyViewDetails = "View Details"
    
    // Errors
    override val errorApiKeyNotSet = "Error: API key is not set"
    override val errorAnalysisFailed = "Analysis failed"
    override val errorCaptureFailed = "Screenshot capture failed"
    override val errorNetworkFailed = "Network error"
    
    // Code Challenge
    override val codeChallengeTitle = "Code Challenge"
    override val codeChallengeProblem = "Problem"
    override val codeChallengeSolution = "Solution"
    override val codeChallengeComplexity = "Complexity"
    override val codeChallengeTimeComplexity = "Time Complexity"
    override val codeChallengeSpaceComplexity = "Space Complexity"
}
