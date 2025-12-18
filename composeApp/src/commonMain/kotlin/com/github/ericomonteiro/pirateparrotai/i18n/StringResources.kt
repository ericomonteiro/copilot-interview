package com.github.ericomonteiro.pirateparrotai.i18n

interface StringResources {
    // App General
    val appName: String
    val settings: String
    val close: String
    val save: String
    val cancel: String
    val copy: String
    val clear: String
    val reload: String
    val tips: String
    
    // Home Screen
    val homeWhatIs: String
    val homeWhatIsTitle: String
    val homeWhatIsContent: String
    val homeConfigTitle: String
    val homeConfigContent: String
    val homeOpenSettings: String
    val homeHowToUseTitle: String
    val homeHowToUseContent: String
    val homeGetStarted: String
    val homeCodeChallenge: String
    val homeCodeChallengeDesc: String
    val homeCertification: String
    val homeCertificationDesc: String
    val homeGenericExam: String
    val homeGenericExamDesc: String
    val homeYourAiCompanion: String
    
    // Settings Screen
    val settingsGeminiApiKey: String
    val settingsApiKeyPlaceholder: String
    val settingsGetApiKey: String
    val settingsAiModel: String
    val settingsDefaultLanguage: String
    val settingsDefaultLanguageHint: String
    val settingsStealthFeatures: String
    val settingsHideFromCapture: String
    val settingsHideFromCaptureDesc: String
    val settingsCaptureRegion: String
    val settingsUseCustomRegion: String
    val settingsCaptureEntireScreen: String
    val settingsRegionInfo: String
    val settingsSelectRegion: String
    val settingsDefineRegionHint: String
    val settingsTroubleshooting: String
    val settingsScreenshotHistory: String
    val settingsScreenshotHistoryDesc: String
    val settingsTestApiConnection: String
    val settingsTipsContent: String
    val settingsAppLanguage: String
    val settingsAppLanguageHint: String
    val settingsSystemDefault: String
    
    // Screenshot Analysis
    val screenshotCapture: String
    val screenshotAnalyzing: String
    val screenshotNoImage: String
    val screenshotCaptureHint: String
    val screenshotSolution: String
    val screenshotCopied: String
    val screenshotCopyCode: String
    val screenshotSelectLanguage: String
    
    // Certification Analysis
    val certificationTitle: String
    val certificationSelectType: String
    val certificationAnswer: String
    val certificationExplanation: String
    val certificationRelatedServices: String
    val certificationExamTips: String
    
    // Generic Exam
    val genericExamTitle: String
    val genericExamSelectType: String
    val genericExamAnswer: String
    
    // History
    val historyTitle: String
    val historyEmpty: String
    val historyDelete: String
    val historyViewDetails: String
    
    // Errors
    val errorApiKeyNotSet: String
    val errorAnalysisFailed: String
    val errorCaptureFailed: String
    val errorNetworkFailed: String
    
    // Code Challenge
    val codeChallengeTitle: String
    val codeChallengeProblem: String
    val codeChallengeSolution: String
    val codeChallengeComplexity: String
    val codeChallengeTimeComplexity: String
    val codeChallengeSpaceComplexity: String
}
