# Changed files manifest

Comparison: SHA-256 inventory captured at the beginning of this continuous session.
Initial branch: master; initial HEAD: 502bd53. Pre-existing user edits are not classified as session changes unless their file bytes changed during implementation.

## Implementation and verification

See `docs/SESSION_REPORT.md` for the current handover and verification results,
`docs/IMPLEMENTATION_STATUS.md` for implementation history,
`docs/RELEASE_CHECKLIST.md` for release gates, and
`docs/ai-build-pack/17_DECISION_REGISTER.md` for unresolved product decisions.

Changes include Kotlin/Compose feature layers, server-authoritative SQL/RLS,
typed realtime recovery, XML content and validation, localized UI, test coverage,
CI/release configuration and safe public configuration examples.
Authorized backend deployment is documented in the session report. Credentials and signing material are excluded.

## Added files

- `.github/workflows/android.yml`
- `app/proguard-rules.pro`
- `app/src/androidTest/java/com/brainybrawl/app/AvatarRepositoryTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/LocalAccountNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/ScreenArtworkUiTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt`
- `app/src/main/assets/licenses/Poppins-OFL.txt`
- `app/src/main/assets/licenses/Tajawal-OFL.txt`
- `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt`
- `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt`
- `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/AccountComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/AccountConnectionPanel.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ModeBento.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ProviderMark.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ScreenArtwork.kt`
- `app/src/main/java/com/brainybrawl/app/core/diagnostics/Diagnostics.kt`
- `app/src/main/java/com/brainybrawl/app/core/localization/CatalogLabels.kt`
- `app/src/main/java/com/brainybrawl/app/core/localization/LocalizedContent.kt`
- `app/src/main/java/com/brainybrawl/app/core/localization/NamedStrings.kt`
- `app/src/main/java/com/brainybrawl/app/core/navigation/BrawlApp.kt`
- `app/src/main/java/com/brainybrawl/app/core/navigation/LegalLinks.kt`
- `app/src/main/java/com/brainybrawl/app/core/network/BackendConfig.kt`
- `app/src/main/java/com/brainybrawl/app/core/network/ConnectivityMonitor.kt`
- `app/src/main/java/com/brainybrawl/app/core/network/ServerClock.kt`
- `app/src/main/java/com/brainybrawl/app/core/security/EncryptedAuthStore.kt`
- `app/src/main/java/com/brainybrawl/app/core/security/PackagedSvg.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/AuthRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/AuthScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/AuthViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/HybridAuthRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/LocalAccounts.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/SupabaseAuthRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/friends/FriendsScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/friends/LocalFriendsScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/friends/LocalSocialCoordinator.kt`
- `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/lobby/LobbyScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/lobby/ReactionPanel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomModels.kt`
- `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/BoardModels.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/MatchModels.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/MatchRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/MatchScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/MatchViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/MiniGameComponents.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/PuzzleGame.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/RouletteWheel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/match/RoundReveal.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AccountPrivacy.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AvatarRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/LocalProfileScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerCard.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/ProfilePhoto.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/StoreRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/StoreScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/StoreViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/game/content/ContentModels.kt`
- `app/src/main/java/com/brainybrawl/app/game/content/ContentRepository.kt`
- `app/src/main/java/com/brainybrawl/app/game/content/XmlContentParser.kt`
- `app/src/main/java/com/brainybrawl/app/game/engine/GameRules.kt`
- `app/src/main/java/com/brainybrawl/app/game/engine/OfflineImages.kt`
- `app/src/main/java/com/brainybrawl/app/game/engine/OfflinePuzzle.kt`
- `app/src/main/java/com/brainybrawl/app/game/engine/OfflineQuestions.kt`
- `app/src/main/res/drawable-nodpi/bb_badge_duel.png`
- `app/src/main/res/drawable-nodpi/bb_badge_duo.png`
- `app/src/main/res/drawable-nodpi/bb_badge_solo.png`
- `app/src/main/res/drawable-nodpi/bb_badge_squad.png`
- `app/src/main/res/drawable-nodpi/bb_currency_flames.png`
- `app/src/main/res/drawable-nodpi/bb_currency_gems.png`
- `app/src/main/res/drawable-nodpi/bb_currency_gold.png`
- `app/src/main/res/drawable-nodpi/bb_state_empty_store.png`
- `app/src/main/res/drawable-nodpi/bb_state_no_rankings.png`
- `app/src/main/res/drawable-nodpi/brand_icon.png`
- `app/src/main/res/drawable-nodpi/brand_logo.png`
- `app/src/main/res/drawable/launcher_brand.xml`
- `app/src/main/res/font/poppins_bold.ttf`
- `app/src/main/res/font/poppins_extrabold.ttf`
- `app/src/main/res/font/poppins_regular.ttf`
- `app/src/main/res/font/tajawal_bold.ttf`
- `app/src/main/res/font/tajawal_extrabold.ttf`
- `app/src/main/res/font/tajawal_regular.ttf`
- `app/src/main/res/values-ar/strings.xml`
- `app/src/main/res/values-fr/strings.xml`
- `app/src/main/res/values-v31/themes.xml`
- `app/src/test/java/com/brainybrawl/app/AuthValidationTest.kt`
- `app/src/test/java/com/brainybrawl/app/BackendConfigTest.kt`
- `app/src/test/java/com/brainybrawl/app/ContentTest.kt`
- `app/src/test/java/com/brainybrawl/app/DiagnosticsTest.kt`
- `app/src/test/java/com/brainybrawl/app/GameRulesTest.kt`
- `app/src/test/java/com/brainybrawl/app/HybridAuthRepositoryTest.kt`
- `app/src/test/java/com/brainybrawl/app/LocalAccountsTest.kt`
- `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt`
- `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt`
- `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt`
- `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt`
- `app/src/test/java/com/brainybrawl/app/OfflineImagesTest.kt`
- `app/src/test/java/com/brainybrawl/app/OfflinePuzzleTest.kt`
- `app/src/test/java/com/brainybrawl/app/OfflineQuestionsTest.kt`
- `app/src/test/java/com/brainybrawl/app/PackagedSvgTest.kt`
- `app/src/test/java/com/brainybrawl/app/PlayerStateTest.kt`
- `app/src/test/java/com/brainybrawl/app/RealtimeStateTest.kt`
- `app/src/test/java/com/brainybrawl/app/ScreenArtSelectionTest.kt`
- `app/src/test/java/com/brainybrawl/app/TeamSnapshotContractTest.kt`
- `app/src/test/resources/server/duo_draft.json`
- `app/src/test/resources/server/duo_initial.json`
- `app/src/test/resources/server/duo_results.json`
- `app/src/test/resources/server/duo_scramble.json`
- `app/src/test/resources/server/solo_precision.json`
- `app/src/test/resources/server/solo_question.json`
- `app/src/test/resources/server/solo_results.json`
- `app/src/test/resources/server/solo_sort.json`
- `app/src/test/resources/server/squad_draft.json`
- `app/src/test/resources/server/squad_initial.json`
- `app/src/test/resources/server/squad_results.json`
- `app/src/test/resources/server/squad_sort.json`
- `assets/SELECTED_MOCKUP_ASSETS.json`
- `assets/images/asset_desert_001.svg`
- `assets/images/asset_history_ship_001.svg`
- `assets/images/asset_lighthouse_001.svg`
- `assets/images/asset_puzzle_lighthouse_001.svg`
- `assets/images/asset_puzzle_whale_001.svg`
- `assets/images/asset_space_001.svg`
- `assets/images/asset_whale_001.svg`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_1.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_10.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_11.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_12.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_13.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_14.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_15.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_16.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_17.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_18.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_19.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_2.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_20.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_21.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_22.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_23.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_24.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_25.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_26.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_27.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_28.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_29.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_3.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_30.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_31.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_32.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_33.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_34.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_35.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_36.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_37.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_38.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_39.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_4.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_40.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_41.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_42.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_43.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_44.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_45.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_46.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_47.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_48.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_49.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_5.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_50.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_51.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_52.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_53.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_54.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_55.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_56.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_57.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_58.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_59.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_6.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_60.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_61.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_62.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_63.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_64.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_65.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_66.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_67.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_68.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_69.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_7.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_70.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_71.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_72.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_73.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_74.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_75.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_76.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_77.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_78.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_79.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_8.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_80.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_81.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_82.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_83.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_84.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_85.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_86.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_87.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_88.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_89.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_9.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_90.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_91.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_92.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_93.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_94.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_95.png`
- `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_96.png`
- `assets/puzzles/orbital_garden.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_1.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_10.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_11.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_12.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_13.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_14.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_15.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_16.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_17.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_18.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_19.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_2.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_20.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_21.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_22.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_23.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_24.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_25.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_26.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_27.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_28.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_29.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_3.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_30.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_31.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_32.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_33.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_34.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_35.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_36.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_37.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_38.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_39.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_4.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_40.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_41.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_42.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_43.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_44.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_45.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_46.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_47.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_48.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_49.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_5.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_50.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_51.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_52.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_53.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_54.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_55.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_56.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_57.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_58.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_59.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_6.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_60.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_61.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_62.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_63.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_64.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_65.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_66.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_67.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_68.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_69.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_7.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_70.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_71.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_72.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_73.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_74.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_75.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_76.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_77.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_78.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_79.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_8.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_80.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_81.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_82.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_83.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_84.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_85.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_86.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_87.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_88.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_89.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_9.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_90.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_91.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_92.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_93.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_94.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_95.png`
- `assets/puzzles/orbital_garden_parts/orbital_garden_Part_96.png`
- `assets/screen/home/home_01.png`
- `assets/screen/splash/splash_01.png`
- `config.example.properties`
- `content/collaborative_puzzle.xml`
- `content/image_guess.xml`
- `content/precision_tap.xml`
- `content/question_round.xml`
- `content/reactions.xml`
- `content/roll_the_dice.xml`
- `content/speed_sort.xml`
- `content/word_scramble.xml`
- `deliverables/README.md`
- `docs/ACCOUNT_MODES_UPDATE.md`
- `docs/ASSET_CREDITS.md`
- `docs/IMPLEMENTATION_STATUS.md`
- `docs/ONLINE_SETUP.md`
- `docs/PRODUCTION_DEPLOYMENT_REVIEW.md`
- `docs/PUZZLE_BACKGROUND_UPDATE.md`
- `docs/RELEASE_CHECKLIST.md`
- `docs/SESSION_REPORT.md`
- `docs/UI_REDESIGN.md`
- `docs/VISUAL_QA_RESULTS.md`
- `supabase/README.md`
- `supabase/config.toml`
- `supabase/functions/game-action/body.mjs`
- `supabase/functions/game-action/index.ts`
- `supabase/migrations/202609190001_core.sql`
- `supabase/migrations/202609190002_operations.sql`
- `supabase/migrations/202609190003_duel_engine.sql`
- `supabase/migrations/202609190004_player_snapshots.sql`
- `supabase/migrations/202609190005_store_snapshot.sql`
- `supabase/migrations/202609190006_room_recovery.sql`
- `supabase/migrations/202609190007_match_visibility.sql`
- `supabase/migrations/202609190008_leaderboards.sql`
- `supabase/migrations/202609190009_squad_draft_draws.sql`
- `supabase/migrations/202609190010_team_actions.sql`
- `supabase/migrations/202609190011_reactions.sql`
- `supabase/migrations/202609190012_snapshot_read_stability.sql`
- `supabase/migrations/202609190013_relay_actions.sql`
- `supabase/migrations/202609190014_team_orchestration.sql`
- `supabase/migrations/202609190015_team_start_draft.sql`
- `supabase/migrations/202609190016_team_question_scoring.sql`
- `supabase/migrations/202609190017_team_snapshots.sql`
- `supabase/migrations/202609190018_action_hardening.sql`
- `supabase/migrations/202609190019_profile_details.sql`
- `supabase/migrations/202609190020_friend_preview.sql`
- `supabase/migrations/202609190021_catalog_labels.sql`
- `supabase/migrations/202609190022_duel_timing_hardening.sql`
- `supabase/migrations/202609190023_public_room_join.sql`
- `supabase/migrations/202609190024_levels_admin.sql`
- `supabase/migrations/202609190025_multilingual_answers.sql`
- `supabase/migrations/202609190026_profile_photos.sql`
- `supabase/migrations/202609190027_account_requests.sql`
- `supabase/migrations/202609190028_optional_launch_boosts.sql`
- `supabase/migrations/202609190029_solo_online.sql`
- `supabase/migrations/202609190030_grid_puzzle_180.sql`
- `tools/asset-requirements.txt`
- `tools/backend-requirements.txt`
- `tools/backend-tests/edge-body.test.mjs`
- `tools/backend-tests/package-lock.json`
- `tools/backend-tests/package.json`
- `tools/backend-tests/security.test.mjs`
- `tools/bootstrap_admin.py`
- `tools/content_pipeline.py`
- `tools/database_connection.py`
- `tools/deploy_backend.py`
- `tools/enrich_content.py`
- `tools/local_config.py`
- `tools/localize_game_content.py`
- `tools/localize_seed_content.py`
- `tools/package_changed_files.py`
- `tools/prepare_backend.py`
- `tools/puzzle_image_cutter.py`
- `tools/release_preflight.py`
- `tools/review_launch_content.py`
- `tools/test_content_pipeline.py`
- `tools/test_multilingual_bank.py`
- `tools/test_puzzle_assets.py`
- `tools/verify_backend.py`

## Modified files

- `.gitignore`
- `app/build.gradle.kts`
- `app/src/androidTest/java/com/brainybrawl/app/ExampleInstrumentedTest.kt`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/brainybrawl/app/MainActivity.kt`
- `app/src/main/java/com/brainybrawl/app/ui/theme/Color.kt`
- `app/src/main/java/com/brainybrawl/app/ui/theme/Theme.kt`
- `app/src/main/java/com/brainybrawl/app/ui/theme/Type.kt`
- `app/src/main/res/drawable/ic_launcher_foreground.xml`
- `app/src/main/res/mipmap-anydpi/ic_launcher.xml`
- `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
- `build.gradle.kts`
- `docs/ai-build-pack/10_MODES_DUO.md`
- `docs/ai-build-pack/11_MODES_SQUAD_SOLO.md`
- `docs/ai-build-pack/12_MINIGAMES.md`
- `docs/ai-build-pack/17_DECISION_REGISTER.md`
- `gradle/libs.versions.toml`

## Deleted files

None during this session. The previously deleted build-pack README was already absent at baseline.

## Archive rules

The archive contains only added/modified files listed above, plus this manifest.
The manifest itself is an added session artifact. Deleted files are listed only.
Git history, build outputs, caches, node_modules, IDE settings, local.properties,
keystores, credential files, emulator screenshots and temporary files are excluded.

## SHA-256 of archived source files

| File | SHA-256 |
| --- | --- |
| `.github/workflows/android.yml` | `BA9BDAB2C3D103B661D45418611F9F352B084D9E127CB34B0BCFD931A36C595F` |
| `.gitignore` | `1E6D76F27E475CD6507D20B34A41C3BE973FAFF9AAD13C6A14D76B4248F1F558` |
| `app/build.gradle.kts` | `6D8834F310E0E016A267AF346B5E9604CDED0E0F55DE5682AA4D1DB52BC90A9A` |
| `app/proguard-rules.pro` | `FE1AB388082D4E267293CA2CA9DD0A6D43CDBA89ED2751F72B923C72156B45BE` |
| `app/src/androidTest/java/com/brainybrawl/app/AvatarRepositoryTest.kt` | `A8FE400361029BC0BF8689BBCFDFF2F1B6D79B92313880FF6F3FA4C8EA4B2CF2` |
| `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt` | `B939E75E13266D46659ACC7676B8CEB21DC5B6C6A3E1B9B471A93E98EC1F29A4` |
| `app/src/androidTest/java/com/brainybrawl/app/ExampleInstrumentedTest.kt` | `595493E8EEFAEB410B5B727BEE0E6750F8141CC8C47FF401AADE09B7206FCEF8` |
| `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt` | `5869010F89E7494A1EE5C4B598A3540127DBF89CF2CDDD12B2E7A890A1DB07AB` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalAccountNavigationTest.kt` | `D175CEC064DC843C4DC6A39194C0ED1F0221777795C1D95339E5CF9D24C4252B` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt` | `91133145B05F87E599DC5B0A98A26FA8B39C67F83F1097E63CF0748752DE93C4` |
| `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt` | `A13357F7CD6851DF16D4B29F5F4C4B4469FC0F40E3E863020691EE4D4BD57322` |
| `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt` | `201D0C6BEEBF77AACE541FB95BDAA9A8504591467035BF5F8043E5A6EB7503ED` |
| `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt` | `E3DF3ED07D7269E43DCF1FD906125F8C8E5907C56F5D0DEFB62513685FDED67A` |
| `app/src/androidTest/java/com/brainybrawl/app/ScreenArtworkUiTest.kt` | `13BD23EBCEE899C341F6A561F825167C2C948565C4A33E9D3CD8B13D27B67A90` |
| `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt` | `80A07AD53C6F062AEED106B69474F6B8AB03F1B0063617ADFED52CC440EDC305` |
| `app/src/main/AndroidManifest.xml` | `277EC70B7CC7069BD70805F6536A3F7628989D2FE5F907E3ED131F74B96ED6EA` |
| `app/src/main/assets/licenses/Poppins-OFL.txt` | `B741A4319A716FA488D77425010697A0990798F7647E07C606F46CF0B55957C7` |
| `app/src/main/assets/licenses/Tajawal-OFL.txt` | `352A1811DC77A6270BFEF7C1BB302C9D5CEFC1234AA23718EC0F2A77F9BBDC5D` |
| `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt` | `148F92C567B4E8EB6D4D5723D8690CFA66392FA0B57596C303359E8BAC2DD269` |
| `app/src/main/java/com/brainybrawl/app/MainActivity.kt` | `73D303BF073B247BA3EB15BE47BBDE55EA9847F7B34672825AF17E216139ED8A` |
| `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt` | `B95E404EBB640A379B26BD6894BA444281CD25B5E916D78108CA24D748834AE2` |
| `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt` | `0556A343B3A7FBD991097893B4A8EA23986FC9BBA9F832099C0978C4BABC61F6` |
| `app/src/main/java/com/brainybrawl/app/core/design/AccountComponents.kt` | `5B50DB6E891E90B1CDFF8264F775BFE27EB581F01630D24B5EF5E3EEC1DACCBD` |
| `app/src/main/java/com/brainybrawl/app/core/design/AccountConnectionPanel.kt` | `18EB16C60BC48480118834454D87B0D2CCA93D362285636ACFEC84E1205755D4` |
| `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt` | `B41A4EF1F4BD3D776DDBCEFEA5DF107D32E05CC340CB6ADA8300729BCD664C50` |
| `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt` | `071B375729E44EFC122EF8879C492FB179B4460942B73D26BC8976E73CE748BC` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt` | `68FE201D7DA5E5E50329BC5DED1335BE511CC30D060C0A97A09C73268A81E90C` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt` | `D119FFE12F04EC01987A4976C5FF1F788C322B84096D17462ED421F25663DB39` |
| `app/src/main/java/com/brainybrawl/app/core/design/ModeBento.kt` | `7D83D0133AD589CE0D7B29F31A022C88693D9DB0A321F2048BB3888F2D305C49` |
| `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt` | `40A38A2B51EFFDE1F5139E544AC62F155D3C74CA90AC1DDEB60E1B12F45C1398` |
| `app/src/main/java/com/brainybrawl/app/core/design/ProviderMark.kt` | `8450EDB1C5E3052EE56717FDC7C12C9A4FD91998A3261CF1DBF7A8E28B341FDC` |
| `app/src/main/java/com/brainybrawl/app/core/design/ScreenArtwork.kt` | `FC413903EC5FF0D889467496F01772A1A7755226FBB4C1B1B0C7A152F9ADE0DA` |
| `app/src/main/java/com/brainybrawl/app/core/diagnostics/Diagnostics.kt` | `104444D2CE72092AC45983829673DB836BBF93A4655EB531F4B220BD82DE8E51` |
| `app/src/main/java/com/brainybrawl/app/core/localization/CatalogLabels.kt` | `710369AF6CD41EA1E3507EC0DA44EFF97F40AA0059F403C4295FE8156738A913` |
| `app/src/main/java/com/brainybrawl/app/core/localization/LocalizedContent.kt` | `7D610FE326E0A9F207F3F9C085F8F62EB4B8493648F63034130C127670C25727` |
| `app/src/main/java/com/brainybrawl/app/core/localization/NamedStrings.kt` | `5E897474202C6695F0C841EF1B820847EC924A86C539D8B035E2B216DF87BDA2` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/BrawlApp.kt` | `2078094A5EDFF363210C22E3B4875E438CF6A53E91EA66647CF2EB1F0387CE58` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/LegalLinks.kt` | `8C1FD64219666E66FDAF9D6D3153FEA4376B5A8BFE158851BF09FB57C2BD4242` |
| `app/src/main/java/com/brainybrawl/app/core/network/BackendConfig.kt` | `BC9974E0D2936DF516E2F04EBAF55C28C3201C109F059C8A320E2BE986622FA6` |
| `app/src/main/java/com/brainybrawl/app/core/network/ConnectivityMonitor.kt` | `58E7B0F3E01C07A7414C3C40FCC2B36C6F9F24592B2DBDF3534BC23AA31377AC` |
| `app/src/main/java/com/brainybrawl/app/core/network/ServerClock.kt` | `4381897A4729519788A98B15BAF31137D032E6F441F4384BC4572280524A0588` |
| `app/src/main/java/com/brainybrawl/app/core/security/EncryptedAuthStore.kt` | `2064FE1C49670DECCA95A5FF45A475F4EC08228186979DA91248864DB603940E` |
| `app/src/main/java/com/brainybrawl/app/core/security/PackagedSvg.kt` | `CA6D9FE0BD821A5552BF9A3C2A22D84C5B735C5D2CB81658EEDDB5EFACB017EB` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthRepository.kt` | `580AC695B043522E8A5C2E5276DC772110B9DEDDB2811EC9D07CFD265A3FF870` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthScreen.kt` | `5ECC589276E2D47F9122C379A49422598FFDECD59C0ADD199D04ACE828BB1633` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthViewModel.kt` | `99F129202ED8B1DE22E0ED2BE4809845C7A369C627EB2D2B3AC10F2B42504A7D` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/HybridAuthRepository.kt` | `E9A8BF63E5FB42F2DF95F25E33EE541538ED8638C43676C859EF9BFBCC1FBA9D` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/LocalAccounts.kt` | `FC2889694DAC56B5A8B64EB7AECB848152B1B50837899CAA95394ECD98F0C6BD` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/SupabaseAuthRepository.kt` | `D192A766AE984AFA6F6625935ED35F8BF5813B3D64B53111FEFBB42C42D24675` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/FriendsScreen.kt` | `3B70F33191EE4D0F0F5AE3173D475D0B5358DE4C3594ADA8CCF5E7C3A8BFBE00` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/LocalFriendsScreen.kt` | `9857CA801CA5BCD768BE9089F8587FCBC89E2AD29C7080509A92212008EAEAE0` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/LocalSocialCoordinator.kt` | `1EC26D4FB03D9ADC4ADA3512E456446D28486C3F1FEA709D2EED7F85FD7104D0` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardRepository.kt` | `0D56A53E6308E23934BFD43BAD344C29C159B180B668DE8A6CBAA5C4AD5BC852` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardScreen.kt` | `E75623BD785892FA1C54DD3CBACCC97DFD6D89DD5747DEDA628E7BA934EF219D` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardViewModel.kt` | `6D8DCBE76486AD698A100C082A9E5894099A1BE298740B5F9E1F63C24137B8DB` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/LobbyScreen.kt` | `F0A14AC4B2CA799625832C73EAEF97DFC2C631C2455C4867892DF179A5F57BAB` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/ReactionPanel.kt` | `C3A039F1E51DD7545957EC0F53F754A114DD9D0F68308ADBDAF0D99580B17354` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomModels.kt` | `E9AB5CEC635E5E488F585F9CB8A1A3FCD49DDAA6B9D346B3D1E98C699F158BDE` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomRepository.kt` | `00C2FE1E6DF36251165BB5A7C7FDA3F6E94A4875D5D4DFFC90BB6A46161E42B7` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomViewModel.kt` | `EBC988455BB42C2617C9A89AB2E547100A6F9CB2387A5D2491FFA6F1D9B4DE42` |
| `app/src/main/java/com/brainybrawl/app/feature/match/BoardModels.kt` | `ABBADBEF464E0F222578714656FA739254AF579867611FAC2C9C5CE9C4069A17` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchModels.kt` | `405CC1495D3CF07AEBD27952CFCF04E9791DC3AD4065D1C59A4F114AAA399721` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchRepository.kt` | `018F87A53A49C7BCFFA53BA714C26FBAC2671B5179D1F0DFB50840ACD8C97338` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchScreen.kt` | `E484B902E52EB3979CF5B16473B1E31E501344A8F999729ED8D241AC5DF341E9` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchViewModel.kt` | `11A886942DD789B85FA3FD282132D0DF6907670E3838BA94FA989EF5141927F1` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MiniGameComponents.kt` | `A580FF8A5822CEF58367F7A3E2690B253DC6F6CB3E2541EF0E7C1A8A47EB784C` |
| `app/src/main/java/com/brainybrawl/app/feature/match/PuzzleGame.kt` | `05EE268A487ED35E2FD5608CA9B442D86B406338CE23CC368A20F1B44478F340` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RouletteWheel.kt` | `0EA9DD955E92043A0FA44832A194A3C8E9D18CBC52C1BF032C178EAEE73A3736` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RoundReveal.kt` | `346782B495FC85C97F6A8096729C2A8AA1B0E2E2A41075544C37CA5AAC90D33A` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageScreen.kt` | `077BDB4231287C2D6DAE7B542BF5270BEE40093BA656A338D41660A57B2220FB` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageViewModel.kt` | `7135D6BC0CACDC59B6B9C8CDBB8A136BC82FDE86219B9884C8548EBA2E5C6DE6` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleScreen.kt` | `0ABB027656112E67F6269455C0019B39ECE108984757CA03D2B175BDD86E948E` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleViewModel.kt` | `2BD26D68DA932C2AF8D94ED297E256EB9E02D703F29519C4BA39ABA312A4C869` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt` | `92F688824D920BD657D3115CBE3388A6F802F36135B60A77C60C6C787D2E322D` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt` | `422308837EF1598CFBD3577AE7996FB74D6625D51D6B7F290B363C8A14027CD0` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AccountPrivacy.kt` | `624ADB332B4C11820E4236872AA5E21F20F73C2958E229F10A52DC5D2BFE3ECF` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AvatarRepository.kt` | `DB9BF41A1E8B2E0F5F88524C5D5AA980C64086EB24EAB1F64A242DA84FF36C40` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/LocalProfileScreen.kt` | `BF5E0276D71D54870EB1DE071F03883B4CA205C4F14700512B0B90ED1F16C541` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerCard.kt` | `CBE4165E83E6079C72AF3E9510AE0D5A3C5BD4AE2E452072C34FE4070EB3DB2C` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt` | `BC997BC64EA8EE8F7D4CF3C683E240F142EDFAF3706B236B380238826FAF2F2C` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt` | `79D5C38FE4EA2351B9E2A95FB826C782EDB1224E618841EAB5F75FD4153164A0` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfilePhoto.kt` | `F81FB2D765AB26C35938E25AA5828C2A68E295D5B118FAF299C8C91C4036BC0C` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt` | `AFB5642B86D516DC2F453A18DE7E49B2C59D72CE2733FCD9323CD6EF333F1E24` |
| `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt` | `7AC85BA540168DAE9DAC2D4ACFED8139E345601B7DA13F9D77BAE7BFF6B49727` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreRepository.kt` | `6EF39199C992AB15E26253535578832577F53492F8A09C5176EB7A80D1DAF566` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreScreen.kt` | `52F8CCB620EDF0B54F7CFD55E2092AA771893CDAE1D2BB1320364832519AD63A` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreViewModel.kt` | `BC6B837A23B40A3F2C85196C72C7EAC2A25AD7542353AE8440BD71E0781E0609` |
| `app/src/main/java/com/brainybrawl/app/game/content/ContentModels.kt` | `7D6D946222409221671832E0376306421559E44DF87B054FB388ECF9D4D14E8D` |
| `app/src/main/java/com/brainybrawl/app/game/content/ContentRepository.kt` | `73DA6AF65EC4554043DCDFFA0B396ABA8AF1FBCF9C6A5E6456F959A0CB4184F1` |
| `app/src/main/java/com/brainybrawl/app/game/content/XmlContentParser.kt` | `3D1A8B4CF519C5C94DD89BE9404821DCBA6B15AE6C19F372198009A8DC6B7129` |
| `app/src/main/java/com/brainybrawl/app/game/engine/GameRules.kt` | `DB1254E1129050BD9C448BAE719DBCB6CA2949B6EBE960E44CB43988C9410275` |
| `app/src/main/java/com/brainybrawl/app/game/engine/OfflineImages.kt` | `7E9D3957E6290BF2FC0185F8996FD131E8C6242A6A1561540317C86C7A622FD5` |
| `app/src/main/java/com/brainybrawl/app/game/engine/OfflinePuzzle.kt` | `74322FBCC6CF33CA3427148608825837ECF485E2C062DBCE3250AA274BEC6994` |
| `app/src/main/java/com/brainybrawl/app/game/engine/OfflineQuestions.kt` | `E8B7A7BAD2AFE95A22E3E7A58864E94D7F7201FDE64755AE8F61D94A9FA02B61` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Color.kt` | `98C8059B00783FCAB7015B114C534C0B5A28EB720EE2302D7310A0A040F86755` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Theme.kt` | `892AD055D442F3A7B06AA02B84096230EB3E9B20CE1412A3616EEC74208DD728` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Type.kt` | `AD32AECDAC25E3DA283776834DAFF04D4E6F2265004975C4CDD57F1CF29399FF` |
| `app/src/main/res/drawable-nodpi/bb_badge_duel.png` | `E5A903BEAFE1D8CA3313FDD333FF72FD3AAE7AF497B352D0AF73064B6D74A59B` |
| `app/src/main/res/drawable-nodpi/bb_badge_duo.png` | `34287BA0965F149A450F3D56E62F26DFF8E5AEC63424DCE529AC743C46DD23FD` |
| `app/src/main/res/drawable-nodpi/bb_badge_solo.png` | `7145033BCD52B02E8A833FA523A09B91A3ABCEC303620D1045BB30ED45B4C99B` |
| `app/src/main/res/drawable-nodpi/bb_badge_squad.png` | `A35B8CBD47BB80B391BA005E83046E19B2E69B4084F647A01FE926B759995CD8` |
| `app/src/main/res/drawable-nodpi/bb_currency_flames.png` | `48C8F3EFFB4D656A952B29D83DD244E9189017BA7BCE5A39B2CEB08F86EB8B13` |
| `app/src/main/res/drawable-nodpi/bb_currency_gems.png` | `97A2D3349578F0024750461EC3CA2DC8542B170936A191A39DF0638B45312502` |
| `app/src/main/res/drawable-nodpi/bb_currency_gold.png` | `7683226EC0E75B3A4D03C42DF0340EE2B8D736FC46440FEFA2B7D32273E6B601` |
| `app/src/main/res/drawable-nodpi/bb_state_empty_store.png` | `C1A3860E19FCE3D2D873E298D7B68B2F80815814174ADA19420669325C0B3D8E` |
| `app/src/main/res/drawable-nodpi/bb_state_no_rankings.png` | `A09E4BFBB1E0EFF26031A0BBA39C9A22771BA0586E06290636F906C6C39E3FF9` |
| `app/src/main/res/drawable-nodpi/brand_icon.png` | `DEA61A1596978DB5FD84357893864C24465758382328F73F424FE63078380269` |
| `app/src/main/res/drawable-nodpi/brand_logo.png` | `3DC848112B22ADD871DF2851F715C9E50F2602572191C185063AC746EE7CD380` |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | `3BD90936981FA09C99DA8A4A897ACF894F1CF5A9121DF2BF7D4CE17A9A2AF629` |
| `app/src/main/res/drawable/launcher_brand.xml` | `D976C61A48351FF680DC099346E5234C183C08100812D8AE79081F6C1D710D0A` |
| `app/src/main/res/font/poppins_bold.ttf` | `983676516167748B74DE6F4771FB384C664FD913ACB8B471122ECACF5DA5EA6C` |
| `app/src/main/res/font/poppins_extrabold.ttf` | `F2AB17C1A63A0ECC12C2461848FC8A469395E3CD2D641803E889C643D9F958E1` |
| `app/src/main/res/font/poppins_regular.ttf` | `7E65201E9B79159E2300267CC885E16C8DCEF2424CDFA09A29BFB0980A94A7BA` |
| `app/src/main/res/font/tajawal_bold.ttf` | `0342AB6B74B6BD1B4B5BF7BEDA45A9734A2B3147A31E8A3A45B6A3417A52D9D7` |
| `app/src/main/res/font/tajawal_extrabold.ttf` | `3D1298B747C22579F9E1BDEB0D4165064CB17F7E66145E2F43F9DEB12AD7A2CF` |
| `app/src/main/res/font/tajawal_regular.ttf` | `6882892DA3E03527D5DB2BBAB3B48BDE6EF2E878A43F522D1A4EEBDA90010A19` |
| `app/src/main/res/mipmap-anydpi/ic_launcher.xml` | `CADB3BAC61CC5ACDA7B7D811FF9C1292A8FD06D697A9CBFC7063E335B3966202` |
| `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml` | `CADB3BAC61CC5ACDA7B7D811FF9C1292A8FD06D697A9CBFC7063E335B3966202` |
| `app/src/main/res/values-ar/strings.xml` | `BE967CCC7F5211DDE9FB7B0C82EC09A985ECFA9C8B155C2FCDEF970FA607977D` |
| `app/src/main/res/values-fr/strings.xml` | `539BB97FFE4D391B2C1D6244F500320436C7F4FC609DB56116CAA5FC6B5CB40F` |
| `app/src/main/res/values-v31/themes.xml` | `264008CEEFC2859C615DB958CC87DE19D4ACB40B98DEE34CE677E208AB84F9A6` |
| `app/src/main/res/values/strings.xml` | `410A0567CF32152E2E3AC944A79BC39B806C018B15D8561E0A2115D8F9CF9C01` |
| `app/src/main/res/values/themes.xml` | `25DE0C97304ADD8E091A5B3EE115F69C93F5783065C185B5E92E45996F61EE15` |
| `app/src/main/res/xml/backup_rules.xml` | `8323B76B990361057A98B9C6B6D74E746CF130D2D6F7A83EB0472B8897E233F9` |
| `app/src/main/res/xml/data_extraction_rules.xml` | `ECEDC62FADC831524352C9FD9B77400375AAA762AD16A4991C5B083CCB82AA01` |
| `app/src/test/java/com/brainybrawl/app/AuthValidationTest.kt` | `8F9B30E1AECBA120D95F2AD21AD7DFBFBFD9740382B3C13E8CB18A88AE2433C4` |
| `app/src/test/java/com/brainybrawl/app/BackendConfigTest.kt` | `A380405187046B6687F4BD34F23C80D6125F1D3BA1790187908824B8E2FFCD4F` |
| `app/src/test/java/com/brainybrawl/app/ContentTest.kt` | `76FBBAE4557853DADAE166474F3DC448B9A3EE8D3AD99FB0BD0D35F31603390C` |
| `app/src/test/java/com/brainybrawl/app/DiagnosticsTest.kt` | `4B01661B5CA891899386461DB3E2F657825A95807D63C8DA46D4AAAB7534A632` |
| `app/src/test/java/com/brainybrawl/app/GameRulesTest.kt` | `10922C63A9ECA28ED4B90692C85E142A0F099CD98B5AB1A24D21C3B87111B6D6` |
| `app/src/test/java/com/brainybrawl/app/HybridAuthRepositoryTest.kt` | `842022A171338FD99B667E50AB2939EE0CB334DE8358C4E9F7D7A36707061EB1` |
| `app/src/test/java/com/brainybrawl/app/LocalAccountsTest.kt` | `B2A7FE467D202238CCECC6C4BCE9E9939B4FB686CE3F25A641B3E3CAB8755DCB` |
| `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt` | `7570992AA79B8995ED49872C566E6D2884CF29BB1D52AFA9A625A15DC13F4792` |
| `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt` | `46F0F9888CEC32976351AD6893982A8384173E66141C7BD914DF273FD61DCF5D` |
| `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt` | `C6094B4C190E25B20955BC8233985FF3AD13934E58F602121C463061DFAE948C` |
| `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt` | `74BED84D096CEF658836B53145F058A7066360484FA5450901A0DDC9421F2891` |
| `app/src/test/java/com/brainybrawl/app/OfflineImagesTest.kt` | `B7AECB11A1C86BE2F0258A70A284CCE679EA5A0DBD0040A235F6F455F4C06CC6` |
| `app/src/test/java/com/brainybrawl/app/OfflinePuzzleTest.kt` | `D113998E3FF400DBE01B76CB970CD172B97AAA4FD7A81C57E8BAF52DF5BA3745` |
| `app/src/test/java/com/brainybrawl/app/OfflineQuestionsTest.kt` | `784968C6FE99AD8F0152B4B1A118B4352D559BE77B33D614E91F630CF4B75B44` |
| `app/src/test/java/com/brainybrawl/app/PackagedSvgTest.kt` | `CC675F11BBABEAE2E743EB942FFF87375EBC30FC3C67CAB6996B43EFE4E4D332` |
| `app/src/test/java/com/brainybrawl/app/PlayerStateTest.kt` | `FB48FC0EBCD47CB2E50F657564FFCF0633024CB437BC0160D7B81F3E712618D7` |
| `app/src/test/java/com/brainybrawl/app/RealtimeStateTest.kt` | `6B3CF9609A9F566E124DF50C5889245146913CEA01D51394080EE317101CCB04` |
| `app/src/test/java/com/brainybrawl/app/ScreenArtSelectionTest.kt` | `F5B4A1F62AD9D26A7C5A8E21A18A768A8269AEBB1FD5A5D4C0CCD448AC05B9B9` |
| `app/src/test/java/com/brainybrawl/app/TeamSnapshotContractTest.kt` | `7C50FC559FFFC91FDCD91405E4DEDEFBF299C14298221DDE498149A4374B0CB5` |
| `app/src/test/resources/server/duo_draft.json` | `159DA645D83A97A3CE9B2DA00527D2A9C2A9BBFDA92E54F4B0098E9F4F5A1585` |
| `app/src/test/resources/server/duo_initial.json` | `B6FE8B464CEA31DE8B06183267BA4B934733BF962BBF9E89210D039484D096CF` |
| `app/src/test/resources/server/duo_results.json` | `5EC268776F96797DE3550AE477E3405DD0DA88694A12EFF3EA0A0E6618440284` |
| `app/src/test/resources/server/duo_scramble.json` | `B893291DD559D44EE679C8BE8D2A70C7E80A4714C2D60E40CEEF91E6DC7BAF41` |
| `app/src/test/resources/server/solo_precision.json` | `EF221076914AE1F1640BE84511195D459F578F24F3CEB11CD5E980C6FD17C9EC` |
| `app/src/test/resources/server/solo_question.json` | `928E404676E25086C05880867C1017B55344B6919DEF45577B1DE44C541CC9A5` |
| `app/src/test/resources/server/solo_results.json` | `759B8C1E611296F6711C81E885ED0408AD761237D373B50CE3C11B7613DB7E8C` |
| `app/src/test/resources/server/solo_sort.json` | `984C9AD71E9EBC5F97C13895D7DAA3CD06D81F9B5E3AA9C6EA9B24B61E5C977C` |
| `app/src/test/resources/server/squad_draft.json` | `3FDCF5005C83F9420088C8B9F099045E9EACB95E1374F49E6CDA4CC340767CDA` |
| `app/src/test/resources/server/squad_initial.json` | `BEEA8CCEFAA7CDAD29EB7C937B21DCD9D4555DD597F2971D22001E6BF9BAFB85` |
| `app/src/test/resources/server/squad_results.json` | `06F7FA66084975B9AC11B09BA367DADCA8FEF99990B166044BE1B7FB2B538579` |
| `app/src/test/resources/server/squad_sort.json` | `F8D1337CAD2975C1EB0A4F843FC8C00F09256CD56701CC649956460B94146421` |
| `assets/SELECTED_MOCKUP_ASSETS.json` | `E95B30045FE7C9E185D5BBF2DCD6B5EBB3928EA8B9B81919EEEFBFB86F5764B5` |
| `assets/images/asset_desert_001.svg` | `E5D247F3E2156C34355B05E7EB2F4B97CD3EE392EC200ADC564339B1DEA584CA` |
| `assets/images/asset_history_ship_001.svg` | `24E9992DB129891A5EE6FE13DA6DC15EB03A0975152E2FFE5E9A5E44ADB571C9` |
| `assets/images/asset_lighthouse_001.svg` | `733DDC6107DF4D675B4BB4B114B19A7E67EF43A435CE03C4165D2DD74D8D8781` |
| `assets/images/asset_puzzle_lighthouse_001.svg` | `733DDC6107DF4D675B4BB4B114B19A7E67EF43A435CE03C4165D2DD74D8D8781` |
| `assets/images/asset_puzzle_whale_001.svg` | `AE34770DA493B0E4E0908D75D9ECF7C6C8DF3EF017DA7DE09626461C01F99FA2` |
| `assets/images/asset_space_001.svg` | `46001FC615EF59BB71A6CC36AECE509446306D6FB9BAD89E4AFDA0EFF3727138` |
| `assets/images/asset_whale_001.svg` | `AE34770DA493B0E4E0908D75D9ECF7C6C8DF3EF017DA7DE09626461C01F99FA2` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419.png` | `38B2CD4B7F8FED6A5E1C8A2CE01608D91B07098C8F41CD827FD494F0C9B37AEE` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_1.png` | `7F3DD495035C78C7D951ABFC4E56654B2394F91CFFC2F2A706E5137186998D8D` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_10.png` | `1FFA1EFAB3A0F20B27E9282AC6CE26EED16DC956DB4BD3AA12661E3AD41D31AE` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_11.png` | `10E6D0B52D1EBC8C491B7ADE67D3F4A3E8372D963B3BA70AEF0053C8CB3B2677` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_12.png` | `51A489884B177404EDD5773DAB6782640109CCEF26E8A9EDBC0B7B450C8AE6C6` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_13.png` | `D9912A0F7AA994D766472B88D0816539F89E39D34FEEC28AC60F77F2E78591D6` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_14.png` | `528149CFD74132D5AC1BB99AEB83008A772B80314FBCDECE56AED3F372AAE3F2` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_15.png` | `922EFFEEE7830D1CCEDC0843FF3C25A978AF14544BA9BC5E15ED767C24EDFC7F` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_16.png` | `4E5CBDB99CC73E16B1CBD99D61FA133228F26134EBDD757148C2B01EDF70273A` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_17.png` | `91264273419C7ECBEA113EA251311B1BEB968D3C0B179D29D6352C3E2AE201E5` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_18.png` | `0691F014DCE7197CA43C599468629F48820FA3ACE66F755E0A180F79DA4ECC7B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_19.png` | `E529611CC514290B17A2707FD9DD2567CC5ACD81406BBCB6FD2C046FB31CE9CC` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_2.png` | `E27B40C642455BB4BCD8675064D9B6EA1655A3D736A8C3DB8ECEAC2079D81CFD` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_20.png` | `14C71BA82299C7735E8038A874D2CDE79CDEC0FDAFB0CDD6066A70104097A7AB` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_21.png` | `3D90C50DC60A579027FBF48E09D7CE447830387C637DBEEFC5D07E998DBB98B7` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_22.png` | `54489BA6E6DB76C91999508E9DD8D07056A1341BF7E9AD52CB17AD05E622935B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_23.png` | `02A633749BD25040CA9B7EA9C17CBF9D846CD9CDD0E7785C2C4E41F0B6FFD9B2` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_24.png` | `2A506887AAF3F112636288D573E07467AFC30F6AA5BCFAB5C9BADC57301D2EAE` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_25.png` | `F1DB23848E6BAA5F3B28B2F8BE8B9F160EFFB684D21DB9454884B3CB6AE67912` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_26.png` | `28C28ADFEB2FAED9520F9BC4D1CDBE658BEE5F8513555D77C936D0CBB3387B70` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_27.png` | `2249B58B2F77704CD0FD322D7433A2E6FDE9E7FC04EA32C7A7048FBB8695B7E4` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_28.png` | `A393EA64E9F27AD4A00D9644AB858901B0E43FDE603A4510854454986407B493` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_29.png` | `3D371DD11D92C74930F7A6E41C35EC9E60BD4E4E03BE133BED1AD3DCBA344DB0` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_3.png` | `E26B1F87A92FC610140B7284E84B5158B62CC48778C968A65C9F8B658DEB340F` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_30.png` | `B497B1F3B571052F573BDB883ED1BF7BAFC035B9ED6F1F4326057A190856F1A8` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_31.png` | `B84DB042CD9ABE6F8843CFF931152297F7889E24BCFEB08E8B0963459F401720` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_32.png` | `1AA6A539BAA3D8613F6D198BEF4DD54EF59DD69849F4597BE515E7BF0B789FC3` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_33.png` | `6DF219E1459F238F24371E6CDE79647BC5999EA4E233FDBFFCFFCDA4C7F12C71` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_34.png` | `751625D65F5B9786F272ED385D8CDDBC4AC21054399A9A7F46232252C8556F66` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_35.png` | `A4051558CE855146F698D848895102B243B890F4A6D76364858E06E4FD54A317` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_36.png` | `F13628D66D303BDD6BC0B47F9B741D801902F682B20F42CD2222D7A925B85987` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_37.png` | `1A738AEA65294EA577DFA2B83997B8476F5777F9BA855709852DF7C2C630F8F0` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_38.png` | `61D171DB3ADEBF9AB36DC03E5A1DAFFB76E3CC6A05B59F879F12E3F92A2EED7A` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_39.png` | `C69B8777B47B9DD21D672FD626D00AB56DEA0EE5CF74B618E8E59FE8B79B1AC0` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_4.png` | `48DF95799AB4A96EF154272EEFF8607776DF280D8754BA0488901B985F4E8E4B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_40.png` | `3462DBF8CE42B922651C38B48FA11325DAF1D969DA929A3AC6A494B0F5D7243A` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_41.png` | `1680CBDFB509AA1AC5A547A01FE86FEED52D1FF6C1F51EB18E7E9041914BA813` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_42.png` | `5589A9308A2B34B633BC68F93BC9AB4E95926559584D934FF59F10DA707D730C` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_43.png` | `0B5E67647DBFC72357B72669E7E60A97B4B38814BCC6B4099CC24040A4B9A805` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_44.png` | `82BD5EDE3BE3EB5405214DC6E83DBFD8F622E6C75849F26710BF2B37C7133B53` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_45.png` | `F46EDB3319B0CDE0FEA4CD1EB69261DD4CD6D86CD6D0FA34A0932088AB07C2BF` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_46.png` | `23EF29F2D7698F60E10EFCCD5BE614DE3827D1EDA9E7F67960FD1051A470D32F` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_47.png` | `341302B9C7C859DC4CAAD123FE21B63A20EDA058816BFAB93BC64EEA95AB7623` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_48.png` | `F3E00BD1284106C8860F69270A7381452396E34740E42BFEBFAEB673EB734370` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_49.png` | `85C9650E666A9CEB673722AECD236DEE90EFB46E8B18483676ABB982FAA9FFB4` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_5.png` | `2B316DFF2DE5CF0D835B478A3C2343248D99F89678EAEC2EB8496B1C5539D15C` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_50.png` | `99C591C26DDCFC293C1930E7FD9D3CED294B8133749410A24E7DEB00CFFB7695` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_51.png` | `5B73B3F4BA4FEE363856A8A66A5A29831EE8BF672C12A0D658F1BF272170A362` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_52.png` | `6E93206B38A2D6569B79C92E4AF45E7CA0CD10B0D907DC7750E9E6DF99A7B713` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_53.png` | `FBAD9840CF27AA07AC5CC393A845683F1B1B0E1B5F7E2CC7BC7CD11141F3A16B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_54.png` | `D7579CA1B4B1B1F4E620BD2D3D0B8FD3D074BD1623C9A118171222AA28A517EF` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_55.png` | `CA00FB30881010E465A8C5C8F0CE55373BF9FD154E7AD62CCEBC85003CBAAD55` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_56.png` | `B8D56545C503AF026DD6C557DE009F212D73BE658869E5B38317BAB37A334735` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_57.png` | `437745A2EEC1F4F9F8A1AB94ED620845A9FF58075C8E1539240F33215BE70831` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_58.png` | `2113538056AD713E81F51F9CC0621D34089E435BF5CCAAC68644C1B7243B6165` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_59.png` | `3CE8BD97756D48B17646394DD758DBFB9231C43EA20706CE022F80A536467E4C` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_6.png` | `D2C92461804C9F7DC2437C52EB53C4526565986B94A32335BA4A0A5F4C11CC1B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_60.png` | `757659385B7D65124BAEB525A9EE146521FA41EB95ADC821EBF8C709FDA5E364` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_61.png` | `B163330F83C5448A518CE99ACFB04C4165E404A004D236E3A1EC35A66A6E4219` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_62.png` | `CF2F4391D6177762012849A288C1F5543306040B5EBC2B666B39E6C52873F4F6` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_63.png` | `3ACF96EFD17E3A6F80EDE4A99452AD92F0054BE657113E64EA812420679924C2` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_64.png` | `CBF226863A77D5E64A7D625FB90806596EEE56C06E8616EAE2359D90F3E14829` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_65.png` | `9038597102BD1454B06C4C50466D83DBCD0A21C2B9EFBBC1203AB43429E1A859` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_66.png` | `59E1C8E5A6265673570E753D76DCD35FBF266AD1ADBEA11C4867B7018285E69B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_67.png` | `129B7025C92C741CA4E2E84B66E44E9FEAB506497CFD9602CB5971FAC70B5F44` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_68.png` | `76FFC3193D625535041EF5A075531D48E4489D27BFEC531369AD0522E4C15319` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_69.png` | `BD5B9ED67F87A949E595972DAB064798BF2A8BC5891AC037493F5BD2A1E53D85` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_7.png` | `EBA1D8EEF3F20F520316D99540D962E7C3A6BBA6C45B8F3E6371AF48CAE3921D` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_70.png` | `949953523EAA93E7E30AC8B3F52104BDFB6DC93FABFB3F5840636A6EA5F47662` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_71.png` | `DAB27258D688560410146A00A62159E2273DF3285D4CBC4A24FCAEBE0C671E3B` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_72.png` | `7BB7A139E851CE1EF67EDB18ECB2370C725D74D260354A38AD1685947E336F00` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_73.png` | `D05CE06FDE5EED492F03E5072B82AFB4C07EA1EB177B82EBB34F5E33E0845510` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_74.png` | `3FD86C05C160B3DEDE1864C26FA44BA1489D0456448A7B75CB2F90A79755236C` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_75.png` | `0BF0CB260D9AF3196424F085D28E6CE95EA104480F33D86E5E142236F13D90B7` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_76.png` | `4742998BC9B82A23EA23C1BF4EC23765C57A4CD5024D8BC284637B2A390E67C8` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_77.png` | `C1F53F1283A597D48C0FB57F94E578CC75788BD9BD5930AE2D50FB73083E1670` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_78.png` | `2BE496FC0FB8A5DBEB200FC11E0C9B3D941ABADA28B38100DEF00241EB40211F` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_79.png` | `43DDEC94028DD11667ECF212D5A8D143809D4547A91CE7F46BEFDE47BC2A9776` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_8.png` | `C7E71B8C0E8939C2B52DB364B59D71AA1BF16D47EB1B03B868C26E29767BA84D` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_80.png` | `95559C3F2FB768084B6B9DA5A90439A86337AD78C300DDC2263DC9E4A5D88429` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_81.png` | `75AC7CCC8712D32DD75AEBC3D4B44AFB603830FD82C5C7414EAB5BBD501190A7` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_82.png` | `981C3C0287A7902DB922A99132EF46E777580ACF3FF6C251210FEFDA06FF90C1` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_83.png` | `C3E9F80758450758290954913F9E0DF3CAFDE013556E6FA398B46233BAB4506A` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_84.png` | `8E2DECD877DF7F004EE6A0A3A73A05FC4832F19F55D5071CC0F0C51791324452` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_85.png` | `12AAC8BC56CCA6A454750C6878CC8E5FF891386C0780D83C39712494680842EF` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_86.png` | `67ECBBE11241C31E135A04186302B5AAE08FFEE1A17B4D15C7ED0A6C851D0207` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_87.png` | `1B54A2512955B6BB20FEBB16AA4582262C9A4F9E6BA033EEAEFEAA52CEAFDAF3` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_88.png` | `A79D8AE6E8A573F7DDAFB23F5D9406B7363264CF3A1DAD5E8B4BFAA8D0862A81` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_89.png` | `25DCA8DC0D3BE9D48C818161801EE0B9F4F36A3E659D42DC73AB1C9B5E4E1C37` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_9.png` | `3A323A3017BCA322FB9F62246FA242B1B262FF350000847A49AA1F6235406257` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_90.png` | `F2F854128C49D40C4B23A81CD220D8158CBDAF665297D17597B434A45D49BDD8` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_91.png` | `AF04CC95681CE14E74E997719FCC778DD9558DC6696E984F4B894BA2F2B06C3C` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_92.png` | `EA274DC300DD9482D00FF0E763CFC5F557481E3E42417E62E7089F42BABA0773` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_93.png` | `CABA8ECDF16F1A6083472222A32C6FA00CBCAC3695AC6D6360156D0637DCBC77` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_94.png` | `BD437139130F9AFD449362DE01E06065EFB58DF8A64D80D06BE28D04D88AA928` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_95.png` | `7EFB51D607763CD20245751FEA30AF8D770CD28FF37062E9507F65B228A5087E` |
| `assets/puzzles/0e5968b6-d782-4392-ab59-306637ee1419_parts/0e5968b6-d782-4392-ab59-306637ee1419_Part_96.png` | `EE2B39423DEC05C6E141AB53A38A89A6FC6B68A3E9D030CC03B5A95259F46CAB` |
| `assets/puzzles/orbital_garden.png` | `C2D6EE4A468E1D57A0619AF9FC928AD747748829672A9DD3CE720FDB02AB3D6F` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_1.png` | `6D6B8832E70A2BA34AEA0B962ECA6F47093D22F7D1383B2CE1200C1CC933B239` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_10.png` | `214B8B5FE8507AC013A8F64C5FB88806938832F9DAD44E1E7F1BF75CDE95CEDE` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_11.png` | `DBD200E6D573A6087E3C2B6792D5E781ECC1FC71284B67F2A8FA03FB1F9B7C54` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_12.png` | `41E0ECDD6FD6C3C646BEBDA23E2FB4D85D3EAD082602F462889483175ABE4049` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_13.png` | `86DC646CF78CEDC76DF44FE28398CABD3BC79453C9605073701DCED19A843AB9` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_14.png` | `30FDEFA5E4FA920DB8401A9A91D7607FEB8EACB9BFB6EC0602F7D056CCA0EA48` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_15.png` | `86DC646CF78CEDC76DF44FE28398CABD3BC79453C9605073701DCED19A843AB9` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_16.png` | `86DC646CF78CEDC76DF44FE28398CABD3BC79453C9605073701DCED19A843AB9` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_17.png` | `8F830ECB9C3B73FCA39BC86D2A68AF21EF04DE000B371BA7FFC860195D6ED829` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_18.png` | `86DC646CF78CEDC76DF44FE28398CABD3BC79453C9605073701DCED19A843AB9` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_19.png` | `86DC646CF78CEDC76DF44FE28398CABD3BC79453C9605073701DCED19A843AB9` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_2.png` | `4D0FC074E5C674032351960944002CE276E442B611DBABC9319DEBF7AC365164` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_20.png` | `1B56D907371B5C4DB073125C50D1BE479D0EEF3D98F37466BFD2EB0D70E8B354` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_21.png` | `1293726439513E304062026ABD41BE41AE12097DB2C2CD1B12240C0AC75D67D4` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_22.png` | `3AE728B068A3CAD2B642F9CFE0016530BCFE5CB3851049561AD039946EBE1BA3` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_23.png` | `6E5FAE5303AB912B6A1268966E4ED5741872F6500A3286ACCFBAB9DA3BC78218` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_24.png` | `B240D4721FFE0F2E0ED20CDA7700B99C3AD3377C04D0F0FA4F9B3963CCF5C19A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_25.png` | `FF8AA24F730DE5133D8B88CB3C63590EEF3E6725A1F83E3D1B3BF77408F02291` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_26.png` | `ABFF6C11D94229F5F12366564FADCD37BCDF140A4ACF318376F60D0CE06A894C` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_27.png` | `801D4912377B747ABFAA5B09F58D6ACD3E59A56E2228B4F3221B62BCB6BF3E09` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_28.png` | `FF8AA24F730DE5133D8B88CB3C63590EEF3E6725A1F83E3D1B3BF77408F02291` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_29.png` | `78A3C373476597C2051A28F037BC3BDD74C95F9F9A3CE7B31447E8C7A6E91C55` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_3.png` | `424D88597D4A08417A1A5A34B93E551CDAD385C8D7C3EE7653C959FD35A1EBCF` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_30.png` | `12E2451788E386929E70C5EBF33405DB2BEA085F4B92CA14CC33116B407AF5F5` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_31.png` | `06B700FDAB4D90EF40E96DFFD83DF789FD2E6767AA130DEF39696358A0D83F1D` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_32.png` | `A91FF74B87397BA9A2A3BD060CB51101D196A294A0AF196D78E35C661C04E2FD` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_33.png` | `9011529A5A3320C4D3FBD950B7CC48CF16CFDE7C10610583665D9FF268C4C288` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_34.png` | `6417DCF026CE639EF96EA92B390D479057BCCE0ACDA2B06851EEB2404A141308` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_35.png` | `FF8AA24F730DE5133D8B88CB3C63590EEF3E6725A1F83E3D1B3BF77408F02291` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_36.png` | `E12018D2D28DCE45F67F790E8AFD45EC5DED7100776ED82336A6FFF9CD144622` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_37.png` | `918D24A12BF5F4A27AB3E5BAD563EAD07DBCBE55DB915FE93CAD78A8D1401FD5` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_38.png` | `823A5AC0148BFB8270545D455213C03EDFCB248FCCF1CC911829601957FF7FF3` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_39.png` | `021D79C24D70F0B428BD2C09B087F1402A4C06D46B245AAE8F7A3B5D2B444828` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_4.png` | `41E0ECDD6FD6C3C646BEBDA23E2FB4D85D3EAD082602F462889483175ABE4049` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_40.png` | `1A3AB833C1AA8E72007F456A58DCF4FCA0B7949DD3F3BBC05D3C2C3ADBBD88EB` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_41.png` | `A200E128E410AF7A68CDE52CBB71DDA26337DE4FBF54C5604686F415F34345CB` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_42.png` | `708F3C03D9F128C9DDF20972EF17435BCC45678B2EC37FBF353E3AB6201DC9B2` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_43.png` | `4C869AF638EF03A5554EB706B88561C00D31ABE0B91DF79F70956A3DD2D5526F` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_44.png` | `90EE85DF309B4528E1716B834D7777A4CE37AA158CA1CDC78594452999960B3A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_45.png` | `8D6E14685CD860AEA8B6A9BBA9F0013C3ED7FF843406007B1A064529D4C793BF` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_46.png` | `FB0CBC65D14CEC6F0E9E43AB8BF1901B7E5C8967026D3B889E5766665682850A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_47.png` | `DC910821D342D255E6FBF399B6A208133C076C38DE8916861D0E7CD3B997EA9C` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_48.png` | `7BD66CDC0A4555E2EBC5D50F8BC2D067FB1D0DE05E7CDC0046952BF2E94200DE` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_49.png` | `E0C9CEB04A4C0F633874E60AC4C81172270B9C9E6A3DB5B6F9C2C41A6465B9B2` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_5.png` | `A1F9F26A0E4939AE9926B1661300ECE06A2AAC5C50777CC2FB2EA2A592DB4578` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_50.png` | `80B358BE9F668ECEEA6C62F638D186DF377A5B89992CB35D5838A83AB606EBB1` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_51.png` | `A80DEB0231625DEA494613FC29750DE53A7E3A8912A4523BF69A3FAB8A0345F6` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_52.png` | `4EE12C5479EE7A025D1B9DC0FE217280FAFB30FE144C69B2113CC7988234AC88` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_53.png` | `AA5D7AC0F36F7A228372C86448B64DB30F5AA2C3B7557596B2669C8680E4816A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_54.png` | `9183765C3B90E4A5AF204BC2D19510EFA625BFE25A30C8845ED11B30CEFE317C` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_55.png` | `A6A155D58072A624CDA9F105F004CE0364BE5AA26B5A6CE553BD791F8FFBFA02` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_56.png` | `2F13E98F68CFA045BB8ACCDA8B31FDF2E8FB0323E4D1B8C19D757FABCF72E16D` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_57.png` | `CBBE09482215BCC8AA908FBDD26B4537FC4CDC5CC9036C658B2AE6A7F39DF2C5` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_58.png` | `12A91D7697B75F3B0BF3864C333D734ABCE3DCB3BEF9CE00FCB1F8EB45293C62` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_59.png` | `DD4B8E7CAF34822C18425CA7A9D14C92A270BC491C4851A258C805071A4E8EB0` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_6.png` | `FF4FE26069C87183C3C051203C7A90513637881145E50C98BC2E1F373F1B24CD` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_60.png` | `09B277D6137964E4EA94FD76A866000E95610C2074D80B6FFE694831EB8898FB` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_61.png` | `7F5AD9F18AA214017A4E75ABCD8900E9DD3DE45D83755C2F5F9FAE93DD581D07` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_62.png` | `B8AEE28221B72C70EA7A5F47C810AF7F2E040B28B6D30E2D43A4E356834B65F6` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_63.png` | `89AD4DD843297BDCCC76C20B71F6A14929D950A2A6EBDE43D58E069EDCB5A90D` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_64.png` | `00DB8E7AF930CA4ED8DF4C588A13686FACCBEC007566449FFBD43B931ACDB788` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_65.png` | `3766DA67142FE3F64ACD7A688321DEB8E5CAD4C6510F3B85626BEEFF97D396FE` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_66.png` | `676E74E8179DCD387F0D0523C1561F6BF8AAA6B3BFE6197DD60D93D0C746331D` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_67.png` | `1C333C38B435A9D3CFD17B71EA2F4CF75863B55D39133C72307121F47F5E578A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_68.png` | `41A88583E3C31DCF20840F43067679AB3F7359F8F5690B6FBC2564334A81366C` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_69.png` | `3E0A624E497D1451380A3D2812E116B4480B675D5D82BF13ED4B43CD4FB27797` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_7.png` | `41E0ECDD6FD6C3C646BEBDA23E2FB4D85D3EAD082602F462889483175ABE4049` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_70.png` | `CCB40917F5152B529B0190461E36789A7EB8D2190A5121C825E9889AB833D6A0` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_71.png` | `73A39D8AE56A964A7D57D627D2C6DCDB9C3873BEE1F5843DF9FC2D8FA16D4685` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_72.png` | `9F503A5F5EEC0E8421433A6CCE70A540B4D3FEE71258990C7E8E0AA6776A0A05` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_73.png` | `5654F7F8139D234C8F99D7A8049C5228303D2628BF7729A7809F9EDE225E0D36` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_74.png` | `EF9290479A15404EF53853A4C90330DCC7C90B75710C02D5E1020D325BB2F616` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_75.png` | `41257F260256F0FE7F480E91BB60D0DAE875049D9AE448017081ED7E7CBFCE16` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_76.png` | `8F8DF93ACFA16E1B075598C7262A858D4E75999EB0902F7BC14F603FF02EB93A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_77.png` | `A5F616FB9E95460C33C5DF075D5619444CB3711077B1499F6C27E040BEC8771A` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_78.png` | `CB3F6F9382CB97A711F37EF4A7CC5856E0A6914C0C99F22AD6979BBD9A6EFDDB` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_79.png` | `1999E8C69E8A139757A8D1B34F5D13F781088FA70FB4953CEB5E68C4909CE01B` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_8.png` | `41E0ECDD6FD6C3C646BEBDA23E2FB4D85D3EAD082602F462889483175ABE4049` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_80.png` | `24F3E985B2EE7169157CF89381D51EDB92517034DAED6772B29CABD12F7148E1` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_81.png` | `7E7B0CBC2249F6E4B69A081C91637D048FE7765EB468D236706BEBDB28984E79` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_82.png` | `9610CD68C8BD22339BCBD2EFF487A625E823381FA61F377505E491EDA205AEE4` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_83.png` | `E8E4B753E7404E8A66AAC0D5235B26A3AC79D348758730D4F1F8A3DC5BA1F744` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_84.png` | `73B8EAFF4C40DCE7A455E9B7FD9DC1E93217D43C48DB00CDB710E287F0A7C4C6` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_85.png` | `9C39113701F7CFF6B8BCA34998E2788F30FC5DA663514B875B28A0CE054D37C6` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_86.png` | `5B2966C7CC71693D6693E5DEC1E73D624C959D5E9539612558C197B4451CF6B3` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_87.png` | `AC87BBB60DDC7221A706F76514AB622817588F735F5D5347E6C962CDB1B3BE18` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_88.png` | `422BC37A99146992E4C6E38D11AF7E1CA37EF814F394A039407B52A2B713EFA4` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_89.png` | `2F5A41E62F1D65E2DFC004A8C25C1D7A75A7137BDFAB64CC642321E810066439` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_9.png` | `522E7E6BF75B00391B6E64CDABBFF8658A9B52FBC0BC0CACC99CEB9A11B0B7A5` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_90.png` | `C43B69740AEDB9447081F71C0D5DA3D8C9E2B503B2F01E477FAF71E9F68A004B` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_91.png` | `A11F1CC2E1DA0EEE5E7BE9D8941FA9D7776251617962EBF8518CBEEC3163C204` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_92.png` | `88D92803E5C3A4C7A6E9B97323D42DED5092B3AEE302F31C1689DF2D56432756` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_93.png` | `5A6C6523BECDC74EE1491823D463D441CE317BAC34A9D28D8C343DED22CCBB59` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_94.png` | `7AF31AB708F74F3F605BFBAD14BEB0605F2C1718A29E330C06C5BB133A3C3127` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_95.png` | `C6782E853D9B7BBCD49D57887B7483865F4602F70A7956616EE31BE2BF445F9F` |
| `assets/puzzles/orbital_garden_parts/orbital_garden_Part_96.png` | `C8AD1D752E578E8D417BFFCA8E3749FA60B509DC8150C4349F9E0FFF03358DA4` |
| `assets/screen/home/home_01.png` | `DF1E1BE14790B33F9729AD9D3B4EE47428B991F6C2CE94F228FE2FD82B22C1FE` |
| `assets/screen/splash/splash_01.png` | `789DDEA3865E1EAD3C7CE359DFCF80433B12CB4EDCAE4DDEDFD870DEE4BD011C` |
| `build.gradle.kts` | `B2DE004828062302CAFAD6AD8FE6C6CE1BCD1218C05A968ABBE9A7F501DB520F` |
| `config.example.properties` | `C47EB0376714BA7354567BDAA7F5ABEB64B1D374E8E4F7D69606F3AE3CAA343B` |
| `content/collaborative_puzzle.xml` | `449068CE785E14892F3EFBDFF270A521F38C3F6C6A845EC93511B5E20205AACF` |
| `content/image_guess.xml` | `79FCD7E134FB0CA286DF1A493EEABCCFFFA804778E9E26F30CF001E8F7320E75` |
| `content/precision_tap.xml` | `CA46BC603EF950DB24A2C837454511F7B83B05B947362093862E809A012730A5` |
| `content/question_round.xml` | `396A50F74A38051EF797218778B3AF0DDAD93F73E1A97380CFBE87E01409DD0A` |
| `content/reactions.xml` | `BEDDB84DBE769EABFB470306FC05C8AF310F31A296A8757CFA82EEBCE33EAF19` |
| `content/roll_the_dice.xml` | `3D8F29E21588F4733CFE9EBF0009128D13F1D8F8633F7E53215F668D505DFA95` |
| `content/speed_sort.xml` | `D3408F213D67D2B2019FBFF86322F0CFA99545775568934313394039EDD57E4E` |
| `content/word_scramble.xml` | `BDDAE22FBC67E0F67D8C2C9A99A015BDBB10AECF1D98816CD387FDD890C814CA` |
| `deliverables/README.md` | `9219DBC1EC2C3F67A2CB07A05FE11FEFAB40910CA239B0267EC7D382517A2D4F` |
| `docs/ACCOUNT_MODES_UPDATE.md` | `2B52D10CF39E0502EE4DEB105106BE891EFF3F824C640F3FE7B0FAFCF3B38C91` |
| `docs/ASSET_CREDITS.md` | `7FA51EB56EBFC260A463CADF2C40F9C57B9AE6B6EC5E43A0106B2DADD2B4B390` |
| `docs/IMPLEMENTATION_STATUS.md` | `06D51507D63AC841109F6635E09C5470D476661195EE65918EB1985C812C4DCB` |
| `docs/ONLINE_SETUP.md` | `8928E9F08A9335C5007285568F7E2C2186195AF758F46BD83C0E7279C6C62EAF` |
| `docs/PRODUCTION_DEPLOYMENT_REVIEW.md` | `D4F91D2C624F22B2B8E5F0430969DE76F71A8A227B102E9ED8AFBE959779B861` |
| `docs/PUZZLE_BACKGROUND_UPDATE.md` | `A230ACFEC87855631C690CC885BFD7C0D4ABF0A0090C4C1B848BC2788AF9D559` |
| `docs/RELEASE_CHECKLIST.md` | `37D8ED02D17C9FD6B415DF131B7AE7ED8BE8B79A9C57045CF5478A9B42C4F2ED` |
| `docs/SESSION_REPORT.md` | `A9367C11196377FCABE29686464BD769103096E0B3876EB4C3A71B547964B84E` |
| `docs/UI_REDESIGN.md` | `1DA5626BC2A2A8727FBD9E3828A48FE53686A90C7FC186CA484C0570142FE42B` |
| `docs/VISUAL_QA_RESULTS.md` | `EDBC5BCFC06A023B877A29D73E261117A62A2F6DD41A3B249F29CD0BB1BA10E1` |
| `docs/ai-build-pack/10_MODES_DUO.md` | `0D22B497BBBEC3DD43E743860C11C7F92767098C8F1223D552C9352C61D7C8B5` |
| `docs/ai-build-pack/11_MODES_SQUAD_SOLO.md` | `B5EFDDA6025EE1714573385CC39B1CA3A043E5F67DDEEA4B8306C0D77CC07C83` |
| `docs/ai-build-pack/12_MINIGAMES.md` | `45F37D424507F0AFFA714A9712D8C93CAD97653D7C7FFC2F81445A8D33719BEB` |
| `docs/ai-build-pack/17_DECISION_REGISTER.md` | `E45CF5BB40FE4A8B1924C1DF15B3E8C59DEA84C866E2983B5A7EDA009C202CCC` |
| `gradle/libs.versions.toml` | `19DF3B89F6FF4440BF41F3E6720C6048E0D1E36B0C3200225A8F140D0E3ED0F3` |
| `supabase/README.md` | `67BCCF75173EB4C3984D59933AB3960B73B2C8A367D7D1E514B8D4495F6A21B0` |
| `supabase/config.toml` | `234110CEF7070A7EEDF6C2B3E160884353A99C6205233AE09826A4E30AF74A04` |
| `supabase/functions/game-action/body.mjs` | `C870363B1576781CED51E6F796EB2C6E312C6BF2762018513BF04E84BB075DFE` |
| `supabase/functions/game-action/index.ts` | `8BC814C554A13F06078F38D1AF4C2C5698EDCB5925D530DA429AAD82611F6BBB` |
| `supabase/migrations/202609190001_core.sql` | `D094656EBC2700D66425A59825A64B3A583FCD9778B1DC5A4215DE67C0AB903F` |
| `supabase/migrations/202609190002_operations.sql` | `F59115C4F18522B9281E67EE942530976CE214BEF64BD7D47A3ECD0D469AEC46` |
| `supabase/migrations/202609190003_duel_engine.sql` | `9F6E5FFE10D4339B965F3B6ECB7EE1EC8C688D8DFB703583CBE9D6792E0B1820` |
| `supabase/migrations/202609190004_player_snapshots.sql` | `38D0832DC7E963B16738AB246083E3EBE596EAF092C3E05EA14F3D5A6D08FC27` |
| `supabase/migrations/202609190005_store_snapshot.sql` | `DB31470C99D59FD021A87F53109579E110C6803233175C70FCFD60B654000D03` |
| `supabase/migrations/202609190006_room_recovery.sql` | `4F75A2337074B5C8ACCE502B14EFFDFC6A81A634EF22ED51410F98E67A64F98C` |
| `supabase/migrations/202609190007_match_visibility.sql` | `AD9466061FDDD8A3CC093B6D38A5B16587EE4C0A4F56D40F887C5BD81C4C8805` |
| `supabase/migrations/202609190008_leaderboards.sql` | `8F340763695D52D377DC7098F2340E0E2B5E4D379B641DCD720EBB757CEC852C` |
| `supabase/migrations/202609190009_squad_draft_draws.sql` | `197E57A3A9118277CCA66D66752675F5DC9C0D792CA4D3B89189ED9F264D0FCD` |
| `supabase/migrations/202609190010_team_actions.sql` | `DA85167403652775CA60C3623ADAE170C728D9F767B87E3199F26C9E75FA5A54` |
| `supabase/migrations/202609190011_reactions.sql` | `E00B6B90FAA4C3EB987DBADC096F03758FFAFEAE7628480AD9F0F7DA2642E499` |
| `supabase/migrations/202609190012_snapshot_read_stability.sql` | `A499EF2F986E32443B4FBE26ABD82F74186DC69D4C869C7B23B6F672EEFDB4DF` |
| `supabase/migrations/202609190013_relay_actions.sql` | `0D5747D6AE082C01DAA0FAA4C2BAD98CD14E8EBB5629BEB5D9039E4C6DE38C22` |
| `supabase/migrations/202609190014_team_orchestration.sql` | `23C103787A68241277C5267212DE78BFA2B239FF24E01D538A96948B18D779CE` |
| `supabase/migrations/202609190015_team_start_draft.sql` | `7B69169ED71B267CCA3E9ACB14817570B907C7FC9AFC0C6A84582776F57F8119` |
| `supabase/migrations/202609190016_team_question_scoring.sql` | `A6036C277E5EE064671CDBE1CE9355EB583FBF2FF875B9D52D8E31D9ECCCA708` |
| `supabase/migrations/202609190017_team_snapshots.sql` | `EF8E210980CE7CD8BDCA5518C514BA7DFFF5DFE0B7BFF3F6103AECB41496E5F0` |
| `supabase/migrations/202609190018_action_hardening.sql` | `F29FD5FEC8E3A17C912EF767BA4EE3457FA9CB9B6988B1B4C06DFF58A2A4B5FC` |
| `supabase/migrations/202609190019_profile_details.sql` | `37CA88F8D67426BEC738C7596721A8E7558BF68AD011EBE4BBD5579B6E40BF6B` |
| `supabase/migrations/202609190020_friend_preview.sql` | `E7FA9D5DC47E4E4E5F011C75191B02E4A3E2117F4D83530E531BD35E4B2EFBE6` |
| `supabase/migrations/202609190021_catalog_labels.sql` | `7F568892B1D6D98EE5624C7919D504A3658ECD4F3D8FC16B69E3D705C3F4CE87` |
| `supabase/migrations/202609190022_duel_timing_hardening.sql` | `7413C3AD7916C5F17F0992C645266F8608FEE165D9EC3E45BFD9BFDC5665FEFE` |
| `supabase/migrations/202609190023_public_room_join.sql` | `DB1B7DD38A302ED2F9DD72DD81B695BD87B0CA414E86D9C93465A067723C01FF` |
| `supabase/migrations/202609190024_levels_admin.sql` | `ED884872B6679DAFBF2705C5D47F71EB6BCFA4D54F845A58F807336FB928382B` |
| `supabase/migrations/202609190025_multilingual_answers.sql` | `381DF5FA105F6FD3BB029380CEAC425755F8F98B0E17FFF39BFE733F33CD6981` |
| `supabase/migrations/202609190026_profile_photos.sql` | `2A5DDCF72D2A20F2A33693B822A85848A5EE74A4E05DEB260A45AB8D33341F4C` |
| `supabase/migrations/202609190027_account_requests.sql` | `380C65DA5AE2FC4E15D26E1E84C8B92EEC342974A8904688BE3D388C119120A9` |
| `supabase/migrations/202609190028_optional_launch_boosts.sql` | `204B4CA8D15CDC2BB80013F986D4819D319FF22AE2B56E25BD4F96053A08E236` |
| `supabase/migrations/202609190029_solo_online.sql` | `5B9732CAFF274E77C1875F52415BE474528936E0BADCBF244696E978A21C93AE` |
| `supabase/migrations/202609190030_grid_puzzle_180.sql` | `BF8F8BF0FC3DE46F890B41C68E73875582EDD2025A2748C29C455618F3EF73C4` |
| `tools/asset-requirements.txt` | `31864229D2DD8A2C4FEDDB7CC04E989EA6643D88158090384A80F69F951052A8` |
| `tools/backend-requirements.txt` | `C01EC0924583348416691C38521D75ED41AAE13C506AAAA7780D866DD565BDA2` |
| `tools/backend-tests/edge-body.test.mjs` | `0139D340420F562AEE7F97196E787BFFDE883B260CCEE2A095937A11D5C0285F` |
| `tools/backend-tests/package-lock.json` | `7A8D846C8374552BF2568DDFD503B376E76098A21B2F892F22F1210D371CE741` |
| `tools/backend-tests/package.json` | `9CCA30F279F7BAF3CB18E2719D93948E22847453048F0A1F6C9731B8B96AD779` |
| `tools/backend-tests/security.test.mjs` | `825882095F1BD0D11DF816ABF2643C4FE20FE94A7B907B7F814CC501660400FB` |
| `tools/bootstrap_admin.py` | `CE2042283302DF1C04EF482D56938962400F3ABA27EAC75F31756582AA0B0690` |
| `tools/content_pipeline.py` | `A2CEC6467EF67B9C8C5FFC4A4C060471084841F669056BBC2120F373B9D97F34` |
| `tools/database_connection.py` | `9400165DD5AF1FC7D8A02E1A2C0A13445586BF1AD107123A922253502D7937EE` |
| `tools/deploy_backend.py` | `197CFE0AD78953C0B5C66FBF0D3E64620CDC473376E0776FD9A1AD85CEC289BE` |
| `tools/enrich_content.py` | `A74B13164353B66D81C16B6F8F285B640958D82C378F04E0CFAF36EF9AD6504D` |
| `tools/local_config.py` | `109AC2BA602416D7086D160220DE33DF6CA9B98D003B2E8570820246171797FB` |
| `tools/localize_game_content.py` | `45EDEB7F3BDD8FFC2FFB0731E438019547A863E0ABD927BAA2FC506543BAA2B0` |
| `tools/localize_seed_content.py` | `C9D68A4C189E8B7FA7AABE6D08C0B875172F7AF586A50B6F60A5888F2C1C2799` |
| `tools/package_changed_files.py` | `5F519917A5768585A4173AC479E52DF990A65ED1F95F6F96C1AE9478DC3CB7FF` |
| `tools/prepare_backend.py` | `9442971A2011D20873E5CA8F19C2B463C544E02AA605C6270180443A4EF3A4B7` |
| `tools/puzzle_image_cutter.py` | `CE55D07C2DF5C813A4ECD1938404028478F5FA17EC88BD29AA823AE95A06C5FB` |
| `tools/release_preflight.py` | `3D2B12F50900BEE9BEFDCFBAB37E03AA7494D6D4C6351A35DC68605EECD429D9` |
| `tools/review_launch_content.py` | `4DE916057A411234E8454A0678C940874E33179E9A25E2415A418C6887A0722F` |
| `tools/test_content_pipeline.py` | `B50B94E78DD14438E80973CC9DEA58AF181C2D8A3E63D5D24722EF9B24287295` |
| `tools/test_multilingual_bank.py` | `7069A9F01547D2629B868BB83DC743789FFE1F714AAD26C32BA17D19015AD138` |
| `tools/test_puzzle_assets.py` | `1FABF9E3E060582C5E9BB064D459D60B7AE9EB7E914396A286D8E7AD4A7A63CD` |
| `tools/verify_backend.py` | `31D485B17B28C267EFDD67307E362A69277081EB6C3322EADE5BF469D7CB79B3` |
