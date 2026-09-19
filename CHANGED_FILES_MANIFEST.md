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
- `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt`
- `app/src/main/assets/licenses/Poppins-OFL.txt`
- `app/src/main/assets/licenses/Tajawal-OFL.txt`
- `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt`
- `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt`
- `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/AccountComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ModeBento.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt`
- `app/src/main/java/com/brainybrawl/app/core/diagnostics/Diagnostics.kt`
- `app/src/main/java/com/brainybrawl/app/core/localization/CatalogLabels.kt`
- `app/src/main/java/com/brainybrawl/app/core/localization/LocalizedContent.kt`
- `app/src/main/java/com/brainybrawl/app/core/localization/NamedStrings.kt`
- `app/src/main/java/com/brainybrawl/app/core/navigation/BrawlApp.kt`
- `app/src/main/java/com/brainybrawl/app/core/navigation/LegalLinks.kt`
- `app/src/main/java/com/brainybrawl/app/core/network/BackendConfig.kt`
- `app/src/main/java/com/brainybrawl/app/core/network/ServerClock.kt`
- `app/src/main/java/com/brainybrawl/app/core/security/EncryptedAuthStore.kt`
- `app/src/main/java/com/brainybrawl/app/core/security/PackagedSvg.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/AuthRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/AuthScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/auth/AuthViewModel.kt`
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
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AccountPrivacy.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/AvatarRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/LocalProfileScreen.kt`
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
- `app/src/main/java/com/brainybrawl/app/game/engine/OfflineQuestions.kt`
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
- `app/src/test/java/com/brainybrawl/app/LocalAccountsTest.kt`
- `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt`
- `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt`
- `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt`
- `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt`
- `app/src/test/java/com/brainybrawl/app/OfflineImagesTest.kt`
- `app/src/test/java/com/brainybrawl/app/OfflineQuestionsTest.kt`
- `app/src/test/java/com/brainybrawl/app/PackagedSvgTest.kt`
- `app/src/test/java/com/brainybrawl/app/PlayerStateTest.kt`
- `app/src/test/java/com/brainybrawl/app/RealtimeStateTest.kt`
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
- `assets/images/asset_desert_001.svg`
- `assets/images/asset_history_ship_001.svg`
- `assets/images/asset_lighthouse_001.svg`
- `assets/images/asset_puzzle_lighthouse_001.svg`
- `assets/images/asset_puzzle_whale_001.svg`
- `assets/images/asset_space_001.svg`
- `assets/images/asset_whale_001.svg`
- `config.example.properties`
- `content/collaborative_puzzle.xml`
- `content/image_guess.xml`
- `content/precision_tap.xml`
- `content/question_round.xml`
- `content/reactions.xml`
- `content/roll_the_dice.xml`
- `content/speed_sort.xml`
- `content/word_scramble.xml`
- `docs/ASSET_CREDITS.md`
- `docs/IMPLEMENTATION_STATUS.md`
- `docs/ONLINE_SETUP.md`
- `docs/PRODUCTION_DEPLOYMENT_REVIEW.md`
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
- `tools/release_preflight.py`
- `tools/review_launch_content.py`
- `tools/test_content_pipeline.py`
- `tools/test_multilingual_bank.py`
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
- `build.gradle.kts`
- `docs/ai-build-pack/10_MODES_DUO.md`
- `docs/ai-build-pack/11_MODES_SQUAD_SOLO.md`
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
| `.github/workflows/android.yml` | `2EFAB21CE479E82688C603056500F842A56067F69806902725F6FD25BA3B9965` |
| `.gitignore` | `B615D273E0F288ACB482A2CA253F44D9FA2A4689E0B15848C6E1F7F85956F24E` |
| `app/build.gradle.kts` | `111813BE1B931E698418647040A1128BA90A79335A6F9D44196DEB4927D8568B` |
| `app/proguard-rules.pro` | `FE1AB388082D4E267293CA2CA9DD0A6D43CDBA89ED2751F72B923C72156B45BE` |
| `app/src/androidTest/java/com/brainybrawl/app/AvatarRepositoryTest.kt` | `A8FE400361029BC0BF8689BBCFDFF2F1B6D79B92313880FF6F3FA4C8EA4B2CF2` |
| `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt` | `2643004D3DF8321AB85C87B73EBE08DC4FD560548B7ED1451B7BE962BAD109CA` |
| `app/src/androidTest/java/com/brainybrawl/app/ExampleInstrumentedTest.kt` | `595493E8EEFAEB410B5B727BEE0E6750F8141CC8C47FF401AADE09B7206FCEF8` |
| `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt` | `5869010F89E7494A1EE5C4B598A3540127DBF89CF2CDDD12B2E7A890A1DB07AB` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalAccountNavigationTest.kt` | `31C941FD336FAEDE69C458DB64A5E4FC59672344451DD7222008A071677B0C89` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt` | `91133145B05F87E599DC5B0A98A26FA8B39C67F83F1097E63CF0748752DE93C4` |
| `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt` | `D9E62DB06E52016D4D948D97E4BC0B25AA5BD56931C96A21398716C988BFE3E9` |
| `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt` | `201D0C6BEEBF77AACE541FB95BDAA9A8504591467035BF5F8043E5A6EB7503ED` |
| `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt` | `C9A23A2BBFAA5AA8AC9BF59415C0685390D6B18DD4F68DDAF7B57B0814D68632` |
| `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt` | `73E688534BAB386AE9FED88C395C6B0827331D0B8D3D1AD3F2065C1C44F288C1` |
| `app/src/main/AndroidManifest.xml` | `277EC70B7CC7069BD70805F6536A3F7628989D2FE5F907E3ED131F74B96ED6EA` |
| `app/src/main/assets/licenses/Poppins-OFL.txt` | `B741A4319A716FA488D77425010697A0990798F7647E07C606F46CF0B55957C7` |
| `app/src/main/assets/licenses/Tajawal-OFL.txt` | `352A1811DC77A6270BFEF7C1BB302C9D5CEFC1234AA23718EC0F2A77F9BBDC5D` |
| `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt` | `148F92C567B4E8EB6D4D5723D8690CFA66392FA0B57596C303359E8BAC2DD269` |
| `app/src/main/java/com/brainybrawl/app/MainActivity.kt` | `73D303BF073B247BA3EB15BE47BBDE55EA9847F7B34672825AF17E216139ED8A` |
| `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt` | `2322BBE9B664902FA833B74B64A2E27D0DCDA60972C842E3CD4500E210DCF11E` |
| `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt` | `0556A343B3A7FBD991097893B4A8EA23986FC9BBA9F832099C0978C4BABC61F6` |
| `app/src/main/java/com/brainybrawl/app/core/design/AccountComponents.kt` | `78E3B47BFFC0BEF7AFB8EA1AF8C23F1A51C8DC89C3247128C4A4A753C3D19072` |
| `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt` | `B71E21736E0EF2A6A823BD746ABBF7AE4B1EDBA6379904B7472E707E719991FE` |
| `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt` | `D1F004FE2975B8AA22BF0A2B7E463E89A7AF3EB9262CB24EC4B5C8250FBA9133` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt` | `68FE201D7DA5E5E50329BC5DED1335BE511CC30D060C0A97A09C73268A81E90C` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt` | `D119FFE12F04EC01987A4976C5FF1F788C322B84096D17462ED421F25663DB39` |
| `app/src/main/java/com/brainybrawl/app/core/design/ModeBento.kt` | `997CD6FC4BEA129916ADA6A992944AC38DF64D78309F9C1A336D2E190F9FF78A` |
| `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt` | `76597A2DF90E4F977457678C7B2A1F84D1935D811D44CBC6D4F02B1AA0376DA5` |
| `app/src/main/java/com/brainybrawl/app/core/diagnostics/Diagnostics.kt` | `104444D2CE72092AC45983829673DB836BBF93A4655EB531F4B220BD82DE8E51` |
| `app/src/main/java/com/brainybrawl/app/core/localization/CatalogLabels.kt` | `710369AF6CD41EA1E3507EC0DA44EFF97F40AA0059F403C4295FE8156738A913` |
| `app/src/main/java/com/brainybrawl/app/core/localization/LocalizedContent.kt` | `7D610FE326E0A9F207F3F9C085F8F62EB4B8493648F63034130C127670C25727` |
| `app/src/main/java/com/brainybrawl/app/core/localization/NamedStrings.kt` | `5E897474202C6695F0C841EF1B820847EC924A86C539D8B035E2B216DF87BDA2` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/BrawlApp.kt` | `DA281791268558E64A023FB30DE75BCD7BFD18550E432CF16BF2F377D52352AF` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/LegalLinks.kt` | `8C1FD64219666E66FDAF9D6D3153FEA4376B5A8BFE158851BF09FB57C2BD4242` |
| `app/src/main/java/com/brainybrawl/app/core/network/BackendConfig.kt` | `BC9974E0D2936DF516E2F04EBAF55C28C3201C109F059C8A320E2BE986622FA6` |
| `app/src/main/java/com/brainybrawl/app/core/network/ServerClock.kt` | `4381897A4729519788A98B15BAF31137D032E6F441F4384BC4572280524A0588` |
| `app/src/main/java/com/brainybrawl/app/core/security/EncryptedAuthStore.kt` | `D6278FEA2465ABDFCE4E5BD3BE8457EC0F3FBC66D192D607617EF3D2ECBD3917` |
| `app/src/main/java/com/brainybrawl/app/core/security/PackagedSvg.kt` | `CA6D9FE0BD821A5552BF9A3C2A22D84C5B735C5D2CB81658EEDDB5EFACB017EB` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthRepository.kt` | `C715DC6B2BA206AD8C96D565E25CEDF20EB97E653E9CDFF74770D6399EA9A667` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthScreen.kt` | `97750B475936BA9F059CB70B6B5D81AEC46137A2A555F0739606054B40C465F9` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthViewModel.kt` | `E281AC756240F1D57F7B131E3E186A3619DBC3E29466CE8CA93F81AD3CABA2DC` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/LocalAccounts.kt` | `D907995FA1A09791C7805E344503FEBEDADEC11DA883DD910BDA0D5050608794` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/SupabaseAuthRepository.kt` | `6E49440581C94F111D62A7866E77CCF7BBA21F9E36C6B21E0D1901819F26D903` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/FriendsScreen.kt` | `3B70F33191EE4D0F0F5AE3173D475D0B5358DE4C3594ADA8CCF5E7C3A8BFBE00` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/LocalFriendsScreen.kt` | `9857CA801CA5BCD768BE9089F8587FCBC89E2AD29C7080509A92212008EAEAE0` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/LocalSocialCoordinator.kt` | `1EC26D4FB03D9ADC4ADA3512E456446D28486C3F1FEA709D2EED7F85FD7104D0` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardRepository.kt` | `0D56A53E6308E23934BFD43BAD344C29C159B180B668DE8A6CBAA5C4AD5BC852` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardScreen.kt` | `A8CD0690539975EECAC6B214280086D105198AD13CB54DC09E31C3C4C3D0F426` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardViewModel.kt` | `6D8DCBE76486AD698A100C082A9E5894099A1BE298740B5F9E1F63C24137B8DB` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/LobbyScreen.kt` | `F0A14AC4B2CA799625832C73EAEF97DFC2C631C2455C4867892DF179A5F57BAB` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/ReactionPanel.kt` | `C3A039F1E51DD7545957EC0F53F754A114DD9D0F68308ADBDAF0D99580B17354` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomModels.kt` | `E9AB5CEC635E5E488F585F9CB8A1A3FCD49DDAA6B9D346B3D1E98C699F158BDE` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomRepository.kt` | `00C2FE1E6DF36251165BB5A7C7FDA3F6E94A4875D5D4DFFC90BB6A46161E42B7` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomViewModel.kt` | `EBC988455BB42C2617C9A89AB2E547100A6F9CB2387A5D2491FFA6F1D9B4DE42` |
| `app/src/main/java/com/brainybrawl/app/feature/match/BoardModels.kt` | `AE9C2903D83C127D5AF9A47408C6A2B97F87E2258E7D5F9A3C3B1BE73DA62193` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchModels.kt` | `405CC1495D3CF07AEBD27952CFCF04E9791DC3AD4065D1C59A4F114AAA399721` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchRepository.kt` | `018F87A53A49C7BCFFA53BA714C26FBAC2671B5179D1F0DFB50840ACD8C97338` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchScreen.kt` | `E484B902E52EB3979CF5B16473B1E31E501344A8F999729ED8D241AC5DF341E9` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchViewModel.kt` | `11A886942DD789B85FA3FD282132D0DF6907670E3838BA94FA989EF5141927F1` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MiniGameComponents.kt` | `A580FF8A5822CEF58367F7A3E2690B253DC6F6CB3E2541EF0E7C1A8A47EB784C` |
| `app/src/main/java/com/brainybrawl/app/feature/match/PuzzleGame.kt` | `5E9577A7C0E3B51D7CDC9EC3CCF1431D021E684EB27ABD0CD6D5607EC6D685DE` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RouletteWheel.kt` | `0EA9DD955E92043A0FA44832A194A3C8E9D18CBC52C1BF032C178EAEE73A3736` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RoundReveal.kt` | `346782B495FC85C97F6A8096729C2A8AA1B0E2E2A41075544C37CA5AAC90D33A` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageScreen.kt` | `077BDB4231287C2D6DAE7B542BF5270BEE40093BA656A338D41660A57B2220FB` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineImageViewModel.kt` | `7135D6BC0CACDC59B6B9C8CDBB8A136BC82FDE86219B9884C8548EBA2E5C6DE6` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt` | `92F688824D920BD657D3115CBE3388A6F802F36135B60A77C60C6C787D2E322D` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt` | `CD1E187A7EED204DDA9908B9239D19F7539899EAE39E6C4E4AA78C5D7CE3E0C5` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AccountPrivacy.kt` | `624ADB332B4C11820E4236872AA5E21F20F73C2958E229F10A52DC5D2BFE3ECF` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/AvatarRepository.kt` | `DB9BF41A1E8B2E0F5F88524C5D5AA980C64086EB24EAB1F64A242DA84FF36C40` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/LocalProfileScreen.kt` | `46F7B87E8366BAEE5FA7ECE904522755F9FA7FE17BE5E0967A6A97BA3634CCEA` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt` | `BC997BC64EA8EE8F7D4CF3C683E240F142EDFAF3706B236B380238826FAF2F2C` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt` | `79D5C38FE4EA2351B9E2A95FB826C782EDB1224E618841EAB5F75FD4153164A0` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfilePhoto.kt` | `F81FB2D765AB26C35938E25AA5828C2A68E295D5B118FAF299C8C91C4036BC0C` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt` | `42BCB945153E52F079F690EB21FE4DA3789C21C72172EDC7A290D1BA80731FB6` |
| `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt` | `7AC85BA540168DAE9DAC2D4ACFED8139E345601B7DA13F9D77BAE7BFF6B49727` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreRepository.kt` | `6EF39199C992AB15E26253535578832577F53492F8A09C5176EB7A80D1DAF566` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreScreen.kt` | `9BEE944DDD12BF58823ABC322BAB803F272829575DCDF67B7413EAA947B7BD02` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreViewModel.kt` | `BC6B837A23B40A3F2C85196C72C7EAC2A25AD7542353AE8440BD71E0781E0609` |
| `app/src/main/java/com/brainybrawl/app/game/content/ContentModels.kt` | `1B9B837E67A4A3C87CBAB4841CC6A946D101732F2D39017B462A68C72C8D8016` |
| `app/src/main/java/com/brainybrawl/app/game/content/ContentRepository.kt` | `73DA6AF65EC4554043DCDFFA0B396ABA8AF1FBCF9C6A5E6456F959A0CB4184F1` |
| `app/src/main/java/com/brainybrawl/app/game/content/XmlContentParser.kt` | `7B00538C49C3F1F075E7E2F1F8259D27ADCC7F2145B357B8A19018AFF52C5CA3` |
| `app/src/main/java/com/brainybrawl/app/game/engine/GameRules.kt` | `88F6777CDC7C1D3294242E5703DF3AD70D92F91D77313DA52136825C32D43BDA` |
| `app/src/main/java/com/brainybrawl/app/game/engine/OfflineImages.kt` | `7E9D3957E6290BF2FC0185F8996FD131E8C6242A6A1561540317C86C7A622FD5` |
| `app/src/main/java/com/brainybrawl/app/game/engine/OfflineQuestions.kt` | `E8B7A7BAD2AFE95A22E3E7A58864E94D7F7201FDE64755AE8F61D94A9FA02B61` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Color.kt` | `98C8059B00783FCAB7015B114C534C0B5A28EB720EE2302D7310A0A040F86755` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Theme.kt` | `892AD055D442F3A7B06AA02B84096230EB3E9B20CE1412A3616EEC74208DD728` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Type.kt` | `AD32AECDAC25E3DA283776834DAFF04D4E6F2265004975C4CDD57F1CF29399FF` |
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
| `app/src/main/res/values-ar/strings.xml` | `C0593957D3146FE20B38C5DE6D81D77CAC5BDC1F5E22549F7F0CE2B52F9D1530` |
| `app/src/main/res/values-fr/strings.xml` | `2CBFE8843308743835EEDEB45B25E5A59ABE556307E4431DBE8109EA377472D3` |
| `app/src/main/res/values-v31/themes.xml` | `264008CEEFC2859C615DB958CC87DE19D4ACB40B98DEE34CE677E208AB84F9A6` |
| `app/src/main/res/values/strings.xml` | `32B54922C4B617EF2838B27F94B0BC8E871527E001D35C84112A4E68A3BB03CF` |
| `app/src/main/res/values/themes.xml` | `25DE0C97304ADD8E091A5B3EE115F69C93F5783065C185B5E92E45996F61EE15` |
| `app/src/test/java/com/brainybrawl/app/AuthValidationTest.kt` | `8F9B30E1AECBA120D95F2AD21AD7DFBFBFD9740382B3C13E8CB18A88AE2433C4` |
| `app/src/test/java/com/brainybrawl/app/BackendConfigTest.kt` | `A380405187046B6687F4BD34F23C80D6125F1D3BA1790187908824B8E2FFCD4F` |
| `app/src/test/java/com/brainybrawl/app/ContentTest.kt` | `76FBBAE4557853DADAE166474F3DC448B9A3EE8D3AD99FB0BD0D35F31603390C` |
| `app/src/test/java/com/brainybrawl/app/DiagnosticsTest.kt` | `4B01661B5CA891899386461DB3E2F657825A95807D63C8DA46D4AAAB7534A632` |
| `app/src/test/java/com/brainybrawl/app/GameRulesTest.kt` | `10922C63A9ECA28ED4B90692C85E142A0F099CD98B5AB1A24D21C3B87111B6D6` |
| `app/src/test/java/com/brainybrawl/app/LocalAccountsTest.kt` | `B2A7FE467D202238CCECC6C4BCE9E9939B4FB686CE3F25A641B3E3CAB8755DCB` |
| `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt` | `7570992AA79B8995ED49872C566E6D2884CF29BB1D52AFA9A625A15DC13F4792` |
| `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt` | `46F0F9888CEC32976351AD6893982A8384173E66141C7BD914DF273FD61DCF5D` |
| `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt` | `C6094B4C190E25B20955BC8233985FF3AD13934E58F602121C463061DFAE948C` |
| `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt` | `74BED84D096CEF658836B53145F058A7066360484FA5450901A0DDC9421F2891` |
| `app/src/test/java/com/brainybrawl/app/OfflineImagesTest.kt` | `B7AECB11A1C86BE2F0258A70A284CCE679EA5A0DBD0040A235F6F455F4C06CC6` |
| `app/src/test/java/com/brainybrawl/app/OfflineQuestionsTest.kt` | `784968C6FE99AD8F0152B4B1A118B4352D559BE77B33D614E91F630CF4B75B44` |
| `app/src/test/java/com/brainybrawl/app/PackagedSvgTest.kt` | `CC675F11BBABEAE2E743EB942FFF87375EBC30FC3C67CAB6996B43EFE4E4D332` |
| `app/src/test/java/com/brainybrawl/app/PlayerStateTest.kt` | `FB48FC0EBCD47CB2E50F657564FFCF0633024CB437BC0160D7B81F3E712618D7` |
| `app/src/test/java/com/brainybrawl/app/RealtimeStateTest.kt` | `6B3CF9609A9F566E124DF50C5889245146913CEA01D51394080EE317101CCB04` |
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
| `assets/images/asset_desert_001.svg` | `E5D247F3E2156C34355B05E7EB2F4B97CD3EE392EC200ADC564339B1DEA584CA` |
| `assets/images/asset_history_ship_001.svg` | `24E9992DB129891A5EE6FE13DA6DC15EB03A0975152E2FFE5E9A5E44ADB571C9` |
| `assets/images/asset_lighthouse_001.svg` | `733DDC6107DF4D675B4BB4B114B19A7E67EF43A435CE03C4165D2DD74D8D8781` |
| `assets/images/asset_puzzle_lighthouse_001.svg` | `733DDC6107DF4D675B4BB4B114B19A7E67EF43A435CE03C4165D2DD74D8D8781` |
| `assets/images/asset_puzzle_whale_001.svg` | `AE34770DA493B0E4E0908D75D9ECF7C6C8DF3EF017DA7DE09626461C01F99FA2` |
| `assets/images/asset_space_001.svg` | `46001FC615EF59BB71A6CC36AECE509446306D6FB9BAD89E4AFDA0EFF3727138` |
| `assets/images/asset_whale_001.svg` | `AE34770DA493B0E4E0908D75D9ECF7C6C8DF3EF017DA7DE09626461C01F99FA2` |
| `build.gradle.kts` | `B2DE004828062302CAFAD6AD8FE6C6CE1BCD1218C05A968ABBE9A7F501DB520F` |
| `config.example.properties` | `C47EB0376714BA7354567BDAA7F5ABEB64B1D374E8E4F7D69606F3AE3CAA343B` |
| `content/collaborative_puzzle.xml` | `9A38A377418690D4A0BC7E103028404AF21A61BECF3DC06534341334283A49FD` |
| `content/image_guess.xml` | `79FCD7E134FB0CA286DF1A493EEABCCFFFA804778E9E26F30CF001E8F7320E75` |
| `content/precision_tap.xml` | `CA46BC603EF950DB24A2C837454511F7B83B05B947362093862E809A012730A5` |
| `content/question_round.xml` | `396A50F74A38051EF797218778B3AF0DDAD93F73E1A97380CFBE87E01409DD0A` |
| `content/reactions.xml` | `BEDDB84DBE769EABFB470306FC05C8AF310F31A296A8757CFA82EEBCE33EAF19` |
| `content/roll_the_dice.xml` | `3D8F29E21588F4733CFE9EBF0009128D13F1D8F8633F7E53215F668D505DFA95` |
| `content/speed_sort.xml` | `D3408F213D67D2B2019FBFF86322F0CFA99545775568934313394039EDD57E4E` |
| `content/word_scramble.xml` | `BDDAE22FBC67E0F67D8C2C9A99A015BDBB10AECF1D98816CD387FDD890C814CA` |
| `docs/ASSET_CREDITS.md` | `BF0FAF2669A4B4AAAC495CE96426ADC94C3C508DC8EECD5735BFE7A3DCBE85C9` |
| `docs/IMPLEMENTATION_STATUS.md` | `06D51507D63AC841109F6635E09C5470D476661195EE65918EB1985C812C4DCB` |
| `docs/ONLINE_SETUP.md` | `8928E9F08A9335C5007285568F7E2C2186195AF758F46BD83C0E7279C6C62EAF` |
| `docs/PRODUCTION_DEPLOYMENT_REVIEW.md` | `D4F91D2C624F22B2B8E5F0430969DE76F71A8A227B102E9ED8AFBE959779B861` |
| `docs/RELEASE_CHECKLIST.md` | `E3D6569147041236FFDDEA9B492A2FC2173DF5F2C1EF1A141144831FF31AAEA8` |
| `docs/SESSION_REPORT.md` | `FE3AF8555CAC526BDA9D70006F7440266807889E607D575B65E1386BB3844B5B` |
| `docs/UI_REDESIGN.md` | `1DA5626BC2A2A8727FBD9E3828A48FE53686A90C7FC186CA484C0570142FE42B` |
| `docs/VISUAL_QA_RESULTS.md` | `C52C503BED7A14163B60686D5D3377D3F6309855E88C6E475746B728EBD98FB2` |
| `docs/ai-build-pack/10_MODES_DUO.md` | `8C00F08172FD4940AAA070ECD54AAEF785EAE099FC976210CE8F74FC0C7CE837` |
| `docs/ai-build-pack/11_MODES_SQUAD_SOLO.md` | `B5EFDDA6025EE1714573385CC39B1CA3A043E5F67DDEEA4B8306C0D77CC07C83` |
| `docs/ai-build-pack/17_DECISION_REGISTER.md` | `3896F8EA8F5F0C1464D48A7F96D831CFA8F9E2F348E2EE6A48F860E7697FDB4A` |
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
| `tools/backend-requirements.txt` | `C01EC0924583348416691C38521D75ED41AAE13C506AAAA7780D866DD565BDA2` |
| `tools/backend-tests/edge-body.test.mjs` | `0139D340420F562AEE7F97196E787BFFDE883B260CCEE2A095937A11D5C0285F` |
| `tools/backend-tests/package-lock.json` | `7A8D846C8374552BF2568DDFD503B376E76098A21B2F892F22F1210D371CE741` |
| `tools/backend-tests/package.json` | `9CCA30F279F7BAF3CB18E2719D93948E22847453048F0A1F6C9731B8B96AD779` |
| `tools/backend-tests/security.test.mjs` | `66144E7AD8C8CBFB2859587E90FCAC8C88135FBCD2AD1EF6D4DA7E33D9354024` |
| `tools/bootstrap_admin.py` | `CE2042283302DF1C04EF482D56938962400F3ABA27EAC75F31756582AA0B0690` |
| `tools/content_pipeline.py` | `048570D73D4C53E3C197FEBD0180A15BE64DA56E47C71EBC1EBCA81A07A18FB2` |
| `tools/database_connection.py` | `9400165DD5AF1FC7D8A02E1A2C0A13445586BF1AD107123A922253502D7937EE` |
| `tools/deploy_backend.py` | `197CFE0AD78953C0B5C66FBF0D3E64620CDC473376E0776FD9A1AD85CEC289BE` |
| `tools/enrich_content.py` | `A74B13164353B66D81C16B6F8F285B640958D82C378F04E0CFAF36EF9AD6504D` |
| `tools/local_config.py` | `109AC2BA602416D7086D160220DE33DF6CA9B98D003B2E8570820246171797FB` |
| `tools/localize_game_content.py` | `45EDEB7F3BDD8FFC2FFB0731E438019547A863E0ABD927BAA2FC506543BAA2B0` |
| `tools/localize_seed_content.py` | `C9D68A4C189E8B7FA7AABE6D08C0B875172F7AF586A50B6F60A5888F2C1C2799` |
| `tools/package_changed_files.py` | `5F519917A5768585A4173AC479E52DF990A65ED1F95F6F96C1AE9478DC3CB7FF` |
| `tools/prepare_backend.py` | `9442971A2011D20873E5CA8F19C2B463C544E02AA605C6270180443A4EF3A4B7` |
| `tools/release_preflight.py` | `3D2B12F50900BEE9BEFDCFBAB37E03AA7494D6D4C6351A35DC68605EECD429D9` |
| `tools/review_launch_content.py` | `4DE916057A411234E8454A0678C940874E33179E9A25E2415A418C6887A0722F` |
| `tools/test_content_pipeline.py` | `32F7310566B45858B1E1339017B171611CDCAC3035821E67DF924BC45B51E345` |
| `tools/test_multilingual_bank.py` | `7069A9F01547D2629B868BB83DC743789FFE1F714AAD26C32BA17D19015AD138` |
| `tools/verify_backend.py` | `31D485B17B28C267EFDD67307E362A69277081EB6C3322EADE5BF469D7CB79B3` |
