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
- `app/src/androidTest/java/com/brainybrawl/app/AppearanceRepositoryTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/AvatarRepositoryTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/LocalAccountNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/ModeHeaderUiTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/ProfileCustomizationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/ScreenArtworkUiTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/StoreOffersTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt`
- `app/src/main/assets/licenses/Lucide-LICENSE.txt`
- `app/src/main/assets/licenses/Poppins-OFL.txt`
- `app/src/main/assets/licenses/Tajawal-OFL.txt`
- `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt`
- `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt`
- `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/AccountComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/AccountConnectionPanel.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameArtwork.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameNavigationBar.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ModeBento.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/PlayerHeader.kt`
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
- `app/src/main/java/com/brainybrawl/app/feature/nearby/BluetoothTransport.kt`
- `app/src/main/java/com/brainybrawl/app/feature/nearby/NearbyGame.kt`
- `app/src/main/java/com/brainybrawl/app/feature/nearby/NearbyScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/nearby/NearbySession.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AccountDetailsCard.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AccountPrivacy.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AppearanceRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AvatarRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/LocalProfileScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerCard.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerId.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerIdRow.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/ProfilePhoto.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/SupabaseAppearanceRemote.kt`
- `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/CurrencyOffers.kt`
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
- `app/src/main/res/drawable/ic_copy.xml`
- `app/src/main/res/drawable/ic_lucide_check.xml`
- `app/src/main/res/drawable/ic_lucide_chevron_down.xml`
- `app/src/main/res/drawable/ic_lucide_contact_round.xml`
- `app/src/main/res/drawable/ic_lucide_gamepad_2.xml`
- `app/src/main/res/drawable/ic_lucide_house.xml`
- `app/src/main/res/drawable/ic_lucide_image.xml`
- `app/src/main/res/drawable/ic_lucide_key_round.xml`
- `app/src/main/res/drawable/ic_lucide_link.xml`
- `app/src/main/res/drawable/ic_lucide_log_out.xml`
- `app/src/main/res/drawable/ic_lucide_mail.xml`
- `app/src/main/res/drawable/ic_lucide_pencil.xml`
- `app/src/main/res/drawable/ic_lucide_puzzle.xml`
- `app/src/main/res/drawable/ic_lucide_scan.xml`
- `app/src/main/res/drawable/ic_lucide_settings.xml`
- `app/src/main/res/drawable/ic_lucide_shopping_bag.xml`
- `app/src/main/res/drawable/ic_lucide_star.xml`
- `app/src/main/res/drawable/ic_lucide_trash.xml`
- `app/src/main/res/drawable/ic_lucide_trophy.xml`
- `app/src/main/res/drawable/ic_lucide_user_round.xml`
- `app/src/main/res/drawable/ic_lucide_users_round.xml`
- `app/src/main/res/drawable/ic_lucide_wifi_off.xml`
- `app/src/main/res/drawable/ic_lucide_x.xml`
- `app/src/main/res/drawable/launcher_brand.xml`
- `app/src/main/res/font/poppins_bold.ttf`
- `app/src/main/res/font/poppins_extrabold.ttf`
- `app/src/main/res/font/poppins_regular.ttf`
- `app/src/main/res/font/tajawal_bold.ttf`
- `app/src/main/res/font/tajawal_extrabold.ttf`
- `app/src/main/res/font/tajawal_regular.ttf`
- `app/src/main/res/values-ar/store_strings.xml`
- `app/src/main/res/values-ar/strings.xml`
- `app/src/main/res/values-fr/store_strings.xml`
- `app/src/main/res/values-fr/strings.xml`
- `app/src/main/res/values-v31/themes.xml`
- `app/src/main/res/values/store_strings.xml`
- `app/src/test/java/com/brainybrawl/app/AppearanceSelectionTest.kt`
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
- `app/src/test/java/com/brainybrawl/app/NearbyGameTest.kt`
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
- `assets/avatars/avatar_01.png`
- `assets/avatars/avatar_02.png`
- `assets/avatars/avatar_03.png`
- `assets/avatars/avatar_04.png`
- `assets/avatars/avatar_05.png`
- `assets/avatars/avatar_06.png`
- `assets/avatars/avatar_07.png`
- `assets/avatars/avatar_08.png`
- `assets/avatars/avatar_09.png`
- `assets/avatars/avatar_10.png`
- `assets/frames/frame_01.png`
- `assets/frames/frame_02.png`
- `assets/frames/frame_03.png`
- `assets/frames/frame_04.png`
- `assets/frames/frame_05.png`
- `assets/frames/frame_06.png`
- `assets/frames/frame_07.png`
- `assets/frames/frame_08.png`
- `assets/frames/frame_09.png`
- `assets/frames/frame_10.png`
- `assets/frames/frame_11.png`
- `assets/frames/frame_12.png`
- `assets/frames/frame_13.png`
- `assets/frames/frame_14.png`
- `assets/frames/frame_15.png`
- `assets/frames/frame_16.png`
- `assets/frames/frame_17.png`
- `assets/frames/frame_18.png`
- `assets/frames/frame_19.png`
- `assets/frames/frame_20.png`
- `assets/icons/LUCIDE_SOURCES.json`
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
- `assets/ui/1v1_card.png`
- `assets/ui/1v1_icon.png`
- `assets/ui/coin_count.png`
- `assets/ui/coin_icon.png`
- `assets/ui/coin_more.png`
- `assets/ui/duo_card.png`
- `assets/ui/duo_icon.png`
- `assets/ui/flame_count.png`
- `assets/ui/flame_icon.png`
- `assets/ui/flame_more.png`
- `assets/ui/games_icon.png`
- `assets/ui/gem_count.png`
- `assets/ui/gem_icon.png`
- `assets/ui/gem_more.png`
- `assets/ui/home_icon.png`
- `assets/ui/offline_card.png`
- `assets/ui/offline_icon.png`
- `assets/ui/profile_icon.png`
- `assets/ui/solo_card.png`
- `assets/ui/solo_icon.png`
- `assets/ui/squad_card.png`
- `assets/ui/squad_icon.png`
- `assets/ui/start_game_icon.png`
- `assets/ui/store_icon.png`
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
- `docs/BLUETOOTH_PLAY.md`
- `docs/CONTENT_AUTHORING_GUIDE.md`
- `docs/GAMES_HEADER_UPDATE.md`
- `docs/IMPLEMENTATION_STATUS.md`
- `docs/ONLINE_SETUP.md`
- `docs/PRODUCTION_DEPLOYMENT_REVIEW.md`
- `docs/PROFILE_CUSTOMIZATION_UPDATE.md`
- `docs/PUZZLE_BACKGROUND_UPDATE.md`
- `docs/RELEASE_CHECKLIST.md`
- `docs/SESSION_REPORT.md`
- `docs/STORE_REDESIGN.md`
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
- `tools/add_puzzle_content.py`
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
- `tools/test_add_puzzle_content.py`
- `tools/test_content_pipeline.py`
- `tools/test_multilingual_bank.py`
- `tools/test_puzzle_assets.py`
- `tools/validate_appearance_assets.py`
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
| `app/build.gradle.kts` | `618FF0719407EBE01478C33577E14BA2A68B84E2930FEC26CE631E5FA2F55582` |
| `app/proguard-rules.pro` | `FE1AB388082D4E267293CA2CA9DD0A6D43CDBA89ED2751F72B923C72156B45BE` |
| `app/src/androidTest/java/com/brainybrawl/app/AppearanceRepositoryTest.kt` | `AB0A33D0F38CF479DF7B84A3399882D992CBF22DEF8143ECCD3C11BE05010D76` |
| `app/src/androidTest/java/com/brainybrawl/app/AvatarRepositoryTest.kt` | `A8FE400361029BC0BF8689BBCFDFF2F1B6D79B92313880FF6F3FA4C8EA4B2CF2` |
| `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt` | `B939E75E13266D46659ACC7676B8CEB21DC5B6C6A3E1B9B471A93E98EC1F29A4` |
| `app/src/androidTest/java/com/brainybrawl/app/ExampleInstrumentedTest.kt` | `595493E8EEFAEB410B5B727BEE0E6750F8141CC8C47FF401AADE09B7206FCEF8` |
| `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt` | `5869010F89E7494A1EE5C4B598A3540127DBF89CF2CDDD12B2E7A890A1DB07AB` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalAccountNavigationTest.kt` | `AEBCAE710D7521BBCA5ECD502802ECC968442AF81613BDD32E9B1C66E2AED668` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt` | `91133145B05F87E599DC5B0A98A26FA8B39C67F83F1097E63CF0748752DE93C4` |
| `app/src/androidTest/java/com/brainybrawl/app/ModeHeaderUiTest.kt` | `69C6AB51AF2567012E4F1DCE9E04C3EFBDEB441B3A5CEF8AACCBAA7631B979A9` |
| `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt` | `A13357F7CD6851DF16D4B29F5F4C4B4469FC0F40E3E863020691EE4D4BD57322` |
| `app/src/androidTest/java/com/brainybrawl/app/ProfileCustomizationTest.kt` | `F973BCC0FD360F5B2B3823E5B08FB57F9000BFF2A6164C334077CB827BA147BE` |
| `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt` | `201D0C6BEEBF77AACE541FB95BDAA9A8504591467035BF5F8043E5A6EB7503ED` |
| `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt` | `E3DF3ED07D7269E43DCF1FD906125F8C8E5907C56F5D0DEFB62513685FDED67A` |
| `app/src/androidTest/java/com/brainybrawl/app/ScreenArtworkUiTest.kt` | `13BD23EBCEE899C341F6A561F825167C2C948565C4A33E9D3CD8B13D27B67A90` |
| `app/src/androidTest/java/com/brainybrawl/app/StoreOffersTest.kt` | `FF1CC4A7939DFF43C67CE0253DE8BB9F05C2BC6C1D0B2A0ADE38414296945DFE` |
| `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt` | `9AD6843D6335D15D30FFEF94F86B00DD8F4BD8B49FF39E45D4A526949F02FEBD` |
| `app/src/main/AndroidManifest.xml` | `B1EEE6D4932098FFA054BA6C55D27AA5EE5C8BDF8C1CF8C88A4499CF0A19A4B2` |
| `app/src/main/assets/licenses/Lucide-LICENSE.txt` | `B495047BD93A9B06913511076F504DABA17D5BBEB3E0650F3BB53A4220329C57` |
| `app/src/main/assets/licenses/Poppins-OFL.txt` | `B741A4319A716FA488D77425010697A0990798F7647E07C606F46CF0B55957C7` |
| `app/src/main/assets/licenses/Tajawal-OFL.txt` | `352A1811DC77A6270BFEF7C1BB302C9D5CEFC1234AA23718EC0F2A77F9BBDC5D` |
| `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt` | `148F92C567B4E8EB6D4D5723D8690CFA66392FA0B57596C303359E8BAC2DD269` |
| `app/src/main/java/com/brainybrawl/app/MainActivity.kt` | `73D303BF073B247BA3EB15BE47BBDE55EA9847F7B34672825AF17E216139ED8A` |
| `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt` | `E132A0C562EC8DAF92CDA695B192BA22D39C6B58CAD7C33B1FE941686907C1F4` |
| `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt` | `0556A343B3A7FBD991097893B4A8EA23986FC9BBA9F832099C0978C4BABC61F6` |
| `app/src/main/java/com/brainybrawl/app/core/design/AccountComponents.kt` | `F1BCCAB2E26D83EAA08CADC06C0C2F4B25DCEB5C1DEC79DCF003679C2EE9592D` |
| `app/src/main/java/com/brainybrawl/app/core/design/AccountConnectionPanel.kt` | `18EB16C60BC48480118834454D87B0D2CCA93D362285636ACFEC84E1205755D4` |
| `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt` | `B41A4EF1F4BD3D776DDBCEFEA5DF107D32E05CC340CB6ADA8300729BCD664C50` |
| `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt` | `D5BE7D18498459A551E5199926AAD00B51FFB9AA7FF262EE551AD26FC079622B` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameArtwork.kt` | `11421506FD783198AE2BCC9BBC118C292BF1F573FD9004C51F9A719A8717172D` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt` | `0EFB01B1EF6CBB36ECB3BF180A1F4423D776C4C97A8DA45BEE433AD58EA7E49A` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameNavigationBar.kt` | `ED60D08E6AA2AD5BE4356AEE2549285E52DC7369C49CF17DE1BF0B23CC58C1E6` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt` | `D119FFE12F04EC01987A4976C5FF1F788C322B84096D17462ED421F25663DB39` |
| `app/src/main/java/com/brainybrawl/app/core/design/ModeBento.kt` | `D9247A468B4213778139447582D4CBD30EBFFA6566D82071561E34C20A6F9BBC` |
| `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt` | `74B080E88F85D6190B2E8967275507A94FFFA2B7C7E29DE711C213C19DE69CF8` |
| `app/src/main/java/com/brainybrawl/app/core/design/PlayerHeader.kt` | `B3BC81F6360B282DBAA9B9D3ED5E27664E0729E0B1E3240E2D920EB9C066A766` |
| `app/src/main/java/com/brainybrawl/app/core/design/ProviderMark.kt` | `8450EDB1C5E3052EE56717FDC7C12C9A4FD91998A3261CF1DBF7A8E28B341FDC` |
| `app/src/main/java/com/brainybrawl/app/core/design/ScreenArtwork.kt` | `FC413903EC5FF0D889467496F01772A1A7755226FBB4C1B1B0C7A152F9ADE0DA` |
| `app/src/main/java/com/brainybrawl/app/core/diagnostics/Diagnostics.kt` | `104444D2CE72092AC45983829673DB836BBF93A4655EB531F4B220BD82DE8E51` |
| `app/src/main/java/com/brainybrawl/app/core/localization/CatalogLabels.kt` | `710369AF6CD41EA1E3507EC0DA44EFF97F40AA0059F403C4295FE8156738A913` |
| `app/src/main/java/com/brainybrawl/app/core/localization/LocalizedContent.kt` | `7D610FE326E0A9F207F3F9C085F8F62EB4B8493648F63034130C127670C25727` |
| `app/src/main/java/com/brainybrawl/app/core/localization/NamedStrings.kt` | `5E897474202C6695F0C841EF1B820847EC924A86C539D8B035E2B216DF87BDA2` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/BrawlApp.kt` | `FE616F15092EA6ABE5098CDC3AEB52E4A9B84303A59770831C2A4F47C90A133F` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/LegalLinks.kt` | `8C1FD64219666E66FDAF9D6D3153FEA4376B5A8BFE158851BF09FB57C2BD4242` |
| `app/src/main/java/com/brainybrawl/app/core/network/BackendConfig.kt` | `BC9974E0D2936DF516E2F04EBAF55C28C3201C109F059C8A320E2BE986622FA6` |
| `app/src/main/java/com/brainybrawl/app/core/network/ConnectivityMonitor.kt` | `58E7B0F3E01C07A7414C3C40FCC2B36C6F9F24592B2DBDF3534BC23AA31377AC` |
| `app/src/main/java/com/brainybrawl/app/core/network/ServerClock.kt` | `4381897A4729519788A98B15BAF31137D032E6F441F4384BC4572280524A0588` |
| `app/src/main/java/com/brainybrawl/app/core/security/EncryptedAuthStore.kt` | `2064FE1C49670DECCA95A5FF45A475F4EC08228186979DA91248864DB603940E` |
| `app/src/main/java/com/brainybrawl/app/core/security/PackagedSvg.kt` | `CA6D9FE0BD821A5552BF9A3C2A22D84C5B735C5D2CB81658EEDDB5EFACB017EB` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthRepository.kt` | `8E8052994500CDB574844EBEA7441EE066A53A159650E4B338F9FC52201415B6` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthScreen.kt` | `A17099D6C0FE8F5B27101A5CB03960C729A02C9E13C075150F71EF161E640F58` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthViewModel.kt` | `C7E8A31DA180793703700D1B9D76B76C35E80423AD57AE52222B4A5083155469` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/HybridAuthRepository.kt` | `9E94D5C3AA8942CCD10A3E9AE3C58012333166D43CF5E14E63363953CAD6DD0B` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/LocalAccounts.kt` | `0AA5BADC09B3F1E31C7032EDE05A464FA84BF8548BA11501497C1BF1474FA49F` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/SupabaseAuthRepository.kt` | `F21C3C8AF573F28E1EEC2FA6211CA6801CB258B2424276A7FB6B785B54296F23` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/FriendsScreen.kt` | `BE1BD528BAA4BF5CBAA20F59BBE5C001279A50BCBEB67B012A5B2F0142D217BE` |
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
| `app/src/main/java/com/brainybrawl/app/feature/match/PuzzleGame.kt` | `7EB98EEF66A666EF0BD492A3A3885C4DFA86048D1D1D10619F0D9811C4590E59` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RouletteWheel.kt` | `0EA9DD955E92043A0FA44832A194A3C8E9D18CBC52C1BF032C178EAEE73A3736` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RoundReveal.kt` | `346782B495FC85C97F6A8096729C2A8AA1B0E2E2A41075544C37CA5AAC90D33A` |
| `app/src/main/java/com/brainybrawl/app/feature/nearby/BluetoothTransport.kt` | `839B03253D21F0300A65C2CD760D37B3ECC1973A2DC3159E95DC9A0737D058DB` |
| `app/src/main/java/com/brainybrawl/app/feature/nearby/NearbyGame.kt` | `915F89AA08B080F8D8761CA7FBBAEA4255E3CAD1AFA3F5AA8016DF0F9DC7689B` |
| `app/src/main/java/com/brainybrawl/app/feature/nearby/NearbyScreen.kt` | `DE65696A8A2753518E2B1A71B72FB3A49047C3F23CA730C7BCC2B2F39D6A4032` |
| `app/src/main/java/com/brainybrawl/app/feature/nearby/NearbySession.kt` | `9F5D15B88E3942DA751FBAF5F5953E229F09EBAA1115926C8D07DE03280B2BD5` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageScreen.kt` | `077BDB4231287C2D6DAE7B542BF5270BEE40093BA656A338D41660A57B2220FB` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageViewModel.kt` | `7135D6BC0CACDC59B6B9C8CDBB8A136BC82FDE86219B9884C8548EBA2E5C6DE6` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleScreen.kt` | `0ABB027656112E67F6269455C0019B39ECE108984757CA03D2B175BDD86E948E` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflinePuzzleViewModel.kt` | `2BD26D68DA932C2AF8D94ED297E256EB9E02D703F29519C4BA39ABA312A4C869` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt` | `92F688824D920BD657D3115CBE3388A6F802F36135B60A77C60C6C787D2E322D` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt` | `422308837EF1598CFBD3577AE7996FB74D6625D51D6B7F290B363C8A14027CD0` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AccountDetailsCard.kt` | `D9ECD7BD61A4E3052CB029990ADEC9FBD2C0650C94789BE09D5D55E2B9672E7F` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AccountPrivacy.kt` | `A7467178BB00687B600958101EA1E556AF0F3D8EAA69AC89953A26B6048FBA7C` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AppearanceRepository.kt` | `F4671F8604856B00A85CE9BAC9CEC2E963BEA35BEB38BA6EEFF00FA8B4988CD2` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AvatarRepository.kt` | `DB9BF41A1E8B2E0F5F88524C5D5AA980C64086EB24EAB1F64A242DA84FF36C40` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/LocalProfileScreen.kt` | `E22641E1E7D9108468FB687B1204A713A44ABC10F8EB8EB3F9EA04EE44BBE6B1` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerCard.kt` | `BB1B37DF524DBBFB94BF49824728F886A8FB14B83032C74E4FC9018C321F21F0` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerId.kt` | `1DFC6056EA698CC59D4A1B1A046A00EC6CDB670B0D40A69525CEDE0B00D0607E` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerIdRow.kt` | `C56E5FC5C91BE653B34E0816C0AED886DD64E3705097249B04FC2285B32224F3` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt` | `FBF98407D877821FDC77886D7445D8D5E14B7B3786A31F546E23C05AD110E78E` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt` | `3AE04A9166A7C70FF05767AA0F51AAAE9CEA3E4A06053ECBEC027C3FF50BF302` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfilePhoto.kt` | `A9CC0BF7639991E9E26C34C71370DF321F3568F0000AFDD783F3A8FB40174759` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt` | `DA722293D1D104B4DEB32F94AC61D20EEA83291E71E3CB718BE2AB2BB005BF30` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/SupabaseAppearanceRemote.kt` | `D87CB2C009924DBAB1946458A165DE760B1C104D0FE7458288B1E7E9AF51B2A8` |
| `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt` | `7AC85BA540168DAE9DAC2D4ACFED8139E345601B7DA13F9D77BAE7BFF6B49727` |
| `app/src/main/java/com/brainybrawl/app/feature/store/CurrencyOffers.kt` | `0791A771E4FB24B4FFABE35F1E16251F1130B323E0C6EED39519F9AB437E6338` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreRepository.kt` | `6EF39199C992AB15E26253535578832577F53492F8A09C5176EB7A80D1DAF566` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreScreen.kt` | `A010EB02B1778BAEF3B77CD4F79F66DBC7D5FBC1793580575868772E60DEEC09` |
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
| `app/src/main/res/drawable/ic_copy.xml` | `8F55D7BAB25FC4E0065DE00646B2C61B89A43FD443E771B8C9FE6A7AB0834884` |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | `3BD90936981FA09C99DA8A4A897ACF894F1CF5A9121DF2BF7D4CE17A9A2AF629` |
| `app/src/main/res/drawable/ic_lucide_check.xml` | `D998BC3FDF2BF1B75D28F77AB2284351334B86968C21C169ECF8642CD7C2A342` |
| `app/src/main/res/drawable/ic_lucide_chevron_down.xml` | `6A4B510BD86F6B04AF90B418D24D3B85EBB497320DA90CD269C14FB620FE8D88` |
| `app/src/main/res/drawable/ic_lucide_contact_round.xml` | `06042F3AF507A958D437A8B7B9FF0839993C3A211BF9638BEEFDD0FEB8DFDBA5` |
| `app/src/main/res/drawable/ic_lucide_gamepad_2.xml` | `437F2B7CC2321EA54593755E0A342D53A15B30FB1BF8230DE379B1452AC37283` |
| `app/src/main/res/drawable/ic_lucide_house.xml` | `01A39D6ECECFAB6B318F6DA49D2C040828A5057069A85678FD18AE1240C6CC88` |
| `app/src/main/res/drawable/ic_lucide_image.xml` | `B578A5A42D4BFC60F8E61B961C24B28123D26154F5B7475332C2E647A3F19CBE` |
| `app/src/main/res/drawable/ic_lucide_key_round.xml` | `4D2DD3C3A49D98DAB302F0E270321925E22DBD89290B3DACDCB5A761320769F2` |
| `app/src/main/res/drawable/ic_lucide_link.xml` | `BB35B63DF5EDFF6E86BEF56D1DDFF6259984990DAEDBD7E5EE3172D08FEC724F` |
| `app/src/main/res/drawable/ic_lucide_log_out.xml` | `98B88A9BB6F61C5338339446777FB9FA4412F0130B51259CF8A5B0B7FAD5DC08` |
| `app/src/main/res/drawable/ic_lucide_mail.xml` | `60B44289C9628566A177A57AFFCA5F016B86942CF54F1C606895C9A9B3E4F4F2` |
| `app/src/main/res/drawable/ic_lucide_pencil.xml` | `98777EC368D1DB2BF23083F4AF7B42BDEDE1A7E2911BBD00CFC8260FE83DE2D0` |
| `app/src/main/res/drawable/ic_lucide_puzzle.xml` | `27F109064E0926785A9DACC065ABD44C0EBB112EB7049A82202B2AB127C15F91` |
| `app/src/main/res/drawable/ic_lucide_scan.xml` | `CC26E7C218C970623DD9EE67D5D595DD074B7531F0A9FEFA5726D826CD28A197` |
| `app/src/main/res/drawable/ic_lucide_settings.xml` | `34E3256E4A4F9C896CA4A968C8F72294B3E7482B54AF0F87EB5E6290E59429AF` |
| `app/src/main/res/drawable/ic_lucide_shopping_bag.xml` | `9B2C0C2C461AF03B17821A56BA38552C479E89E7D77EC1A8E0C76CFF052C8A51` |
| `app/src/main/res/drawable/ic_lucide_star.xml` | `94F978FAD69C34A1415480FE185A651F1DD712774B23B44829F8D32FC66A1FF9` |
| `app/src/main/res/drawable/ic_lucide_trash.xml` | `B0D66E8BABC15548ECBAC4D4D99E4A86E5D05AE5D9D5D76A8B0A5D9E57F0D6CF` |
| `app/src/main/res/drawable/ic_lucide_trophy.xml` | `AB3A33D00EF5550E29CB6832D53A7F6DD94C53C57A049F3CDACB2CBB946A7F6F` |
| `app/src/main/res/drawable/ic_lucide_user_round.xml` | `58D00D15C8FAE4EB0697D2DA6ADC2FA6263D099ED46EF5D1D5B1B2485A5DD4D5` |
| `app/src/main/res/drawable/ic_lucide_users_round.xml` | `5E845F4BB773F4E1C3873C73381D6C62EB1F94D56BED21210F759EB0BDC1A321` |
| `app/src/main/res/drawable/ic_lucide_wifi_off.xml` | `0E5340C663635DBF29BD547FB33C2F9D9C8051146499A805D08D52B8AC01D5B8` |
| `app/src/main/res/drawable/ic_lucide_x.xml` | `51B6C325D8A3318D7CEE604E5FC26072B662F7BB260B380CE06C8CEEEB96963F` |
| `app/src/main/res/drawable/launcher_brand.xml` | `D976C61A48351FF680DC099346E5234C183C08100812D8AE79081F6C1D710D0A` |
| `app/src/main/res/font/poppins_bold.ttf` | `983676516167748B74DE6F4771FB384C664FD913ACB8B471122ECACF5DA5EA6C` |
| `app/src/main/res/font/poppins_extrabold.ttf` | `F2AB17C1A63A0ECC12C2461848FC8A469395E3CD2D641803E889C643D9F958E1` |
| `app/src/main/res/font/poppins_regular.ttf` | `7E65201E9B79159E2300267CC885E16C8DCEF2424CDFA09A29BFB0980A94A7BA` |
| `app/src/main/res/font/tajawal_bold.ttf` | `0342AB6B74B6BD1B4B5BF7BEDA45A9734A2B3147A31E8A3A45B6A3417A52D9D7` |
| `app/src/main/res/font/tajawal_extrabold.ttf` | `3D1298B747C22579F9E1BDEB0D4165064CB17F7E66145E2F43F9DEB12AD7A2CF` |
| `app/src/main/res/font/tajawal_regular.ttf` | `6882892DA3E03527D5DB2BBAB3B48BDE6EF2E878A43F522D1A4EEBDA90010A19` |
| `app/src/main/res/mipmap-anydpi/ic_launcher.xml` | `CADB3BAC61CC5ACDA7B7D811FF9C1292A8FD06D697A9CBFC7063E335B3966202` |
| `app/src/main/res/mipmap-anydpi/ic_launcher_round.xml` | `CADB3BAC61CC5ACDA7B7D811FF9C1292A8FD06D697A9CBFC7063E335B3966202` |
| `app/src/main/res/values-ar/store_strings.xml` | `B8553CAEAF7E599F4EAC6E48F094D7CA4E643DD0C17F1E5E6762AFFE02F6CC3C` |
| `app/src/main/res/values-ar/strings.xml` | `382D93F3ADB9EFC738C25477B3B8E4CFB93E38DEC89B906BF6CAAE2E1D020195` |
| `app/src/main/res/values-fr/store_strings.xml` | `1377A9A6E2B0A2D3E61D93EBA86D0D9872880C12C26504C9BE1BE3B46EBF7B02` |
| `app/src/main/res/values-fr/strings.xml` | `CF7B94053E90613D0109C76199CE235193A9CD31102A1B913396E9712D017E8C` |
| `app/src/main/res/values-v31/themes.xml` | `264008CEEFC2859C615DB958CC87DE19D4ACB40B98DEE34CE677E208AB84F9A6` |
| `app/src/main/res/values/store_strings.xml` | `9F08F9BB77CE96BB06FC66822BD97D7EFAA1A0258CE0026CFE74545651891EF6` |
| `app/src/main/res/values/strings.xml` | `50538B39DAC0FDD795591788A59DEBCEF60B20FBFF46688261067B5AF3C887DC` |
| `app/src/main/res/values/themes.xml` | `25DE0C97304ADD8E091A5B3EE115F69C93F5783065C185B5E92E45996F61EE15` |
| `app/src/main/res/xml/backup_rules.xml` | `8323B76B990361057A98B9C6B6D74E746CF130D2D6F7A83EB0472B8897E233F9` |
| `app/src/main/res/xml/data_extraction_rules.xml` | `ECEDC62FADC831524352C9FD9B77400375AAA762AD16A4991C5B083CCB82AA01` |
| `app/src/test/java/com/brainybrawl/app/AppearanceSelectionTest.kt` | `AB0C633E6B85258116179AC010B8772163993DFE231B6BC9F16EF99320036F8B` |
| `app/src/test/java/com/brainybrawl/app/AuthValidationTest.kt` | `8F9B30E1AECBA120D95F2AD21AD7DFBFBFD9740382B3C13E8CB18A88AE2433C4` |
| `app/src/test/java/com/brainybrawl/app/BackendConfigTest.kt` | `A380405187046B6687F4BD34F23C80D6125F1D3BA1790187908824B8E2FFCD4F` |
| `app/src/test/java/com/brainybrawl/app/ContentTest.kt` | `76FBBAE4557853DADAE166474F3DC448B9A3EE8D3AD99FB0BD0D35F31603390C` |
| `app/src/test/java/com/brainybrawl/app/DiagnosticsTest.kt` | `4B01661B5CA891899386461DB3E2F657825A95807D63C8DA46D4AAAB7534A632` |
| `app/src/test/java/com/brainybrawl/app/GameRulesTest.kt` | `10922C63A9ECA28ED4B90692C85E142A0F099CD98B5AB1A24D21C3B87111B6D6` |
| `app/src/test/java/com/brainybrawl/app/HybridAuthRepositoryTest.kt` | `33A7D17A1C9E44A4FD42E575E955B31E62EC1BB845CFBC8A9C84E16AF27B0B42` |
| `app/src/test/java/com/brainybrawl/app/LocalAccountsTest.kt` | `B2A7FE467D202238CCECC6C4BCE9E9939B4FB686CE3F25A641B3E3CAB8755DCB` |
| `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt` | `7570992AA79B8995ED49872C566E6D2884CF29BB1D52AFA9A625A15DC13F4792` |
| `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt` | `46F0F9888CEC32976351AD6893982A8384173E66141C7BD914DF273FD61DCF5D` |
| `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt` | `C6094B4C190E25B20955BC8233985FF3AD13934E58F602121C463061DFAE948C` |
| `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt` | `74BED84D096CEF658836B53145F058A7066360484FA5450901A0DDC9421F2891` |
| `app/src/test/java/com/brainybrawl/app/NearbyGameTest.kt` | `0B8734EE0820A00B681E2876C83C9E7C4E2ED9B668A67DA7B2D940380A7FF09E` |
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
| `assets/avatars/avatar_01.png` | `DB4A63CF88318A88C944526829E5F9F52CEC1C603D4A4ED7B5A370EE845B5282` |
| `assets/avatars/avatar_02.png` | `7038D83AE72FEFCEED5DD58DA31C3FA057085EA36BD64DD0618E8D607B802A39` |
| `assets/avatars/avatar_03.png` | `1E95D4BD6930FF998368F621366035D011EE6E3A890071194ED44560FFD9F15C` |
| `assets/avatars/avatar_04.png` | `B55A157A0FE288A324583C561FCF9F97CFC8665E7288D0FFE906DEDB98EE59B8` |
| `assets/avatars/avatar_05.png` | `1EE22B9FE22BA9FAF7083EF1D811703115D3F424989FE2FEEC9856AE66F119E0` |
| `assets/avatars/avatar_06.png` | `383A96A95B9215994DB5C5F9715C47D0336B2F8B4067420AF5961F3825AA79FD` |
| `assets/avatars/avatar_07.png` | `9A0956F64F0289BF1768926C9F7E3C6E4242C240895CC7B4EB7D40A61D952874` |
| `assets/avatars/avatar_08.png` | `AA606DFE9984D39AB94FAFDBF134859B9417FA1F18D5C61354EDEF1764915314` |
| `assets/avatars/avatar_09.png` | `57AE39B10A09E26143DD3552CE9732D9773DE593E4748CD9639C672CE8B087A2` |
| `assets/avatars/avatar_10.png` | `7F7CA7FB3089367EE1DA9BAD4A0F4297DF809CDA9346AEEE197554535926CE69` |
| `assets/frames/frame_01.png` | `E13ED8461D4C35E813DC28EDEBFA568497F37C30793A5E23A103B88B30E47788` |
| `assets/frames/frame_02.png` | `8162CF74F0A9E0E46D81BEAC7C8E9DD2FE8428A5056DED2D9D04F13950AB6DCC` |
| `assets/frames/frame_03.png` | `7A003939C1E25D7CA3A09EF72BF0F643D1C8B51048EE1405D431020F3C38BF54` |
| `assets/frames/frame_04.png` | `CD448C407E143C4A32F6E2F3AD10196EC5244CD92D3389D0B35AB9097132EF56` |
| `assets/frames/frame_05.png` | `23152AD33D8D1F9A2A849C34FF3637F113963DF38C0B66116070EE7172F629F7` |
| `assets/frames/frame_06.png` | `9B45788AA798F8659A1DCA151DFEDAB76D10081AAFDAB16E5ED1157ADAFE5B9D` |
| `assets/frames/frame_07.png` | `360BBA90DC112A423D11EB9CA3660FD2FC9E33D8C5BB4F827C03AB443A39D4EF` |
| `assets/frames/frame_08.png` | `9E9390E82009D5B915ECB879F6DB9BA54A5D22A5E2F2132A92B1DD57D984A396` |
| `assets/frames/frame_09.png` | `93640480B4EE2E46DF7A940485B97D3AF79F0EAB825667F39026F9BDBBEADCC0` |
| `assets/frames/frame_10.png` | `6FBD5B37A5BB992CDE999A1A184DA6EBA88A275D8EB541531A1413F0D3031503` |
| `assets/frames/frame_11.png` | `963D86CDFF6494F58C8B801E5ED0F3AC3B4FB27658BF942777BDEF46C3005438` |
| `assets/frames/frame_12.png` | `A5F9784312784FF5A82307FE0F83B2DF38836B07B6B74D0BA05202A954C4D30B` |
| `assets/frames/frame_13.png` | `9AF21DC7501F5036C4408F546D1EAF90F7928B5AEB4BC19C83ACE107280C3004` |
| `assets/frames/frame_14.png` | `A215104CF6D26E712D6370C46E16401AF8C186C2F76D3DAE13774DB1EC294D32` |
| `assets/frames/frame_15.png` | `EF24F4B2997118BEC4FA833AF254E1490D975C8D4BB969A9EA68F33461D6D09A` |
| `assets/frames/frame_16.png` | `0510AF37040DE3C938F262A0139291590ADDC0509B44138BFB8CECE600C5E468` |
| `assets/frames/frame_17.png` | `139D3052F18A9DD8C7669548CAA9B8C26C05BA4751447661AEC807F977159D48` |
| `assets/frames/frame_18.png` | `3B4998D8EE462484BBB425F641F0B3B23A3EAF7BEE5F51FD0D566D204689B566` |
| `assets/frames/frame_19.png` | `549B6C36212FC4DA8BF4A7161B1E0096FAAC9B83BDCA63F9FDCF7AD931927B32` |
| `assets/frames/frame_20.png` | `0A0F71E96F9367505E7F7B8076C6011B2164502F4D2039C9081BB09DC14A6695` |
| `assets/icons/LUCIDE_SOURCES.json` | `6834E229B5BCFC58886340BD52485E516B2F72A6949F8E97471C8D150404DCCF` |
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
| `assets/ui/1v1_card.png` | `064C83ADAAB2139C51952685FCA2170C3C7DCD964EE68638E991241A91B83EEE` |
| `assets/ui/1v1_icon.png` | `48A1002D376F9D7BF5C45538C22F25B8F9CD7611FE40E37B35750A387DE8645F` |
| `assets/ui/coin_count.png` | `FCF5991FDCA323712B4EC9521823BF4367EB323EC51A7B5DB6CFF6D15DC7A107` |
| `assets/ui/coin_icon.png` | `44C3B6980D6787AF2B6E199B97D4AB8259A10305C8DF1E6D5EC3AC5C781D5BB9` |
| `assets/ui/coin_more.png` | `CDE3690756F3625D2D697F8F0D1EEF72CCC5F17407E2BE6FE9155DC7ACF28E46` |
| `assets/ui/duo_card.png` | `4B9F41B5F97E2E81DFBECBF0F50459A80028FE4558C604D87B837B30FE346D76` |
| `assets/ui/duo_icon.png` | `B988BA1FEB801117103662231F21EAF9927C7F391A4D0035FCA73575A17276F9` |
| `assets/ui/flame_count.png` | `644B6FA726D6941CA4D8576521D0AA18BB64CD0127F458951F8C176FF47FE806` |
| `assets/ui/flame_icon.png` | `81AD5B27B23F157FBB0454DCD11C632DB86E15EB5A73A0F1B1536F0AA7053C73` |
| `assets/ui/flame_more.png` | `00F045796CB3757A951F9C739B7BAB1B80FB7EA6DF4C3C1B3874E1B65632458C` |
| `assets/ui/games_icon.png` | `7332713DCE81FE265973B70245D42385878AA6592141E0136E18091F9D98F77D` |
| `assets/ui/gem_count.png` | `EECBB5186FD8536E598B22FCD8316B16F547C7E8CBF11AF5591D86F33132D57C` |
| `assets/ui/gem_icon.png` | `06C31D3A4E9381B38B58BC91FF6F63E2DA61AAD85BD0DCDFAE2936A2565BB0B0` |
| `assets/ui/gem_more.png` | `74346F51460694EAEAF0FC4FD500FD83FEA1A0DB461C5A83A070B38B23E16287` |
| `assets/ui/home_icon.png` | `C706982CF7D32C1ADD700CF67CAA931CF6125FE07342305AC09DE5CE4B1D03FE` |
| `assets/ui/offline_card.png` | `954ED01D71EF605395FA3A78C38C60A11CDBAEE192BAE5ED1ABDE50BA357F9B3` |
| `assets/ui/offline_icon.png` | `D89B8DEE03D82B68BF4DFFED39DB409C1E9212C531A7143650F4C1358FAA8384` |
| `assets/ui/profile_icon.png` | `566CADE01299DC04EC9D6609B501ED22DEAD51B1DB009FF91A3B1F77E335E88A` |
| `assets/ui/solo_card.png` | `4A1F0269B291DC25584758C6819B7BB060D9BE5F1D892AD81817FA37119CD2C9` |
| `assets/ui/solo_icon.png` | `C8C3C43233CBEDFE08B9F188ABEBDDF1BB6CABCB86F4D1702324929C4ECF4592` |
| `assets/ui/squad_card.png` | `13640765C9F48FDF91E61792824065E8879F45B7E8623FADFD1105967E5B10B9` |
| `assets/ui/squad_icon.png` | `C227AD6615A465F2384A468601FEBFB10EA01BA578520234528DCBD2D8DFDA36` |
| `assets/ui/start_game_icon.png` | `DBCDAEAFF4623990A80C6FB6AE006A6E9EFED1648A5022A8A577DA62B8087595` |
| `assets/ui/store_icon.png` | `C8449C9B5D9D67FA2F2FACEEF11DB5200AEB48C6AA0E2AA2E4C08550A3F5E79D` |
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
| `deliverables/README.md` | `13AFD4CD3FE2A7040FA165FCF74FA5F49172F34221BD7B501429398B7BE9B906` |
| `docs/ACCOUNT_MODES_UPDATE.md` | `2B52D10CF39E0502EE4DEB105106BE891EFF3F824C640F3FE7B0FAFCF3B38C91` |
| `docs/ASSET_CREDITS.md` | `C426B25800757D590978EEC0516E57458DB979CF9162FCA7CAB4659CC37AE9A2` |
| `docs/BLUETOOTH_PLAY.md` | `DA86915E02F5A9F149A6EAC9B43EF157D41310448CAA3F5B7369FA5927A941AC` |
| `docs/CONTENT_AUTHORING_GUIDE.md` | `34EE1AA9CF939358980AD82211BDD3FBD6207B37D34B89715718CA73CD16AFEA` |
| `docs/GAMES_HEADER_UPDATE.md` | `8B4FF4CD128FCFD2CAE184EF4E7605C931092C5C68AC55E3BF156197A1BA635F` |
| `docs/IMPLEMENTATION_STATUS.md` | `06D51507D63AC841109F6635E09C5470D476661195EE65918EB1985C812C4DCB` |
| `docs/ONLINE_SETUP.md` | `8928E9F08A9335C5007285568F7E2C2186195AF758F46BD83C0E7279C6C62EAF` |
| `docs/PRODUCTION_DEPLOYMENT_REVIEW.md` | `D4F91D2C624F22B2B8E5F0430969DE76F71A8A227B102E9ED8AFBE959779B861` |
| `docs/PROFILE_CUSTOMIZATION_UPDATE.md` | `7813D8E7DAB7314BF7D088E4BE629F80A602AA4F6DFF4A1E5E6AC23CDF82B4E2` |
| `docs/PUZZLE_BACKGROUND_UPDATE.md` | `A230ACFEC87855631C690CC885BFD7C0D4ABF0A0090C4C1B848BC2788AF9D559` |
| `docs/RELEASE_CHECKLIST.md` | `82D3722E7B2E2A1522E56B93D3929FB01AB8A0E1AF6FE85A98A1D7D7EADAA412` |
| `docs/SESSION_REPORT.md` | `AC1094D472F0720C7867E81674F84037DA3F23148CB28B40E7B071DD66FCD467` |
| `docs/STORE_REDESIGN.md` | `F9E0CC2D9088F0A72CD41F132096CEDFF6D906631EA7340C29D92DCEFA91AC89` |
| `docs/UI_REDESIGN.md` | `1DA5626BC2A2A8727FBD9E3828A48FE53686A90C7FC186CA484C0570142FE42B` |
| `docs/VISUAL_QA_RESULTS.md` | `302E29C016C7E9A37F2282E367183E4A723CC94E0C47D7E33A4FD0C882F921C2` |
| `docs/ai-build-pack/10_MODES_DUO.md` | `0D22B497BBBEC3DD43E743860C11C7F92767098C8F1223D552C9352C61D7C8B5` |
| `docs/ai-build-pack/11_MODES_SQUAD_SOLO.md` | `B5EFDDA6025EE1714573385CC39B1CA3A043E5F67DDEEA4B8306C0D77CC07C83` |
| `docs/ai-build-pack/12_MINIGAMES.md` | `45F37D424507F0AFFA714A9712D8C93CAD97653D7C7FFC2F81445A8D33719BEB` |
| `docs/ai-build-pack/17_DECISION_REGISTER.md` | `4DA3ADA5E681E8CB75B2783AAA3C9F8D52BCAACF54D625F5CECE3B28E3FB748C` |
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
| `tools/add_puzzle_content.py` | `091D12870AACAD6BCCDC18C2FD9A1C24A4CF7979ED60DCA547530A0B8C9A5E2C` |
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
| `tools/test_add_puzzle_content.py` | `4C9410F15A613F5D2536E070F95BDB95A3A16AA4A1A310C5B64DB50DA0381C39` |
| `tools/test_content_pipeline.py` | `B50B94E78DD14438E80973CC9DEA58AF181C2D8A3E63D5D24722EF9B24287295` |
| `tools/test_multilingual_bank.py` | `7069A9F01547D2629B868BB83DC743789FFE1F714AAD26C32BA17D19015AD138` |
| `tools/test_puzzle_assets.py` | `1FABF9E3E060582C5E9BB064D459D60B7AE9EB7E914396A286D8E7AD4A7A63CD` |
| `tools/validate_appearance_assets.py` | `915501774D28CC324791B9B78E79D18FF1F36D49AF0B3CE3E9175477D56F1287` |
| `tools/verify_backend.py` | `31D485B17B28C267EFDD67307E362A69277081EB6C3322EADE5BF469D7CB79B3` |
