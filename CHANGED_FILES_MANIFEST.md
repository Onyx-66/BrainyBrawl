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
No production deployment, credentials or signing material is included.

## Added files

- `.github/workflows/android.yml`
- `app/proguard-rules.pro`
- `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt`
- `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt`
- `app/src/main/assets/licenses/Poppins-OFL.txt`
- `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt`
- `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt`
- `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt`
- `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt`
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
- `app/src/main/java/com/brainybrawl/app/feature/auth/SupabaseAuthRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/friends/FriendsScreen.kt`
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
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/StoreRepository.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/StoreScreen.kt`
- `app/src/main/java/com/brainybrawl/app/feature/store/StoreViewModel.kt`
- `app/src/main/java/com/brainybrawl/app/game/content/ContentModels.kt`
- `app/src/main/java/com/brainybrawl/app/game/content/ContentRepository.kt`
- `app/src/main/java/com/brainybrawl/app/game/content/XmlContentParser.kt`
- `app/src/main/java/com/brainybrawl/app/game/engine/GameRules.kt`
- `app/src/main/java/com/brainybrawl/app/game/engine/OfflineQuestions.kt`
- `app/src/main/res/font/poppins_bold.ttf`
- `app/src/main/res/font/poppins_extrabold.ttf`
- `app/src/main/res/font/poppins_regular.ttf`
- `app/src/main/res/values-ar/strings.xml`
- `app/src/main/res/values-fr/strings.xml`
- `app/src/main/res/values-v31/themes.xml`
- `app/src/test/java/com/brainybrawl/app/AuthValidationTest.kt`
- `app/src/test/java/com/brainybrawl/app/BackendConfigTest.kt`
- `app/src/test/java/com/brainybrawl/app/ContentTest.kt`
- `app/src/test/java/com/brainybrawl/app/DiagnosticsTest.kt`
- `app/src/test/java/com/brainybrawl/app/GameRulesTest.kt`
- `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt`
- `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt`
- `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt`
- `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt`
- `app/src/test/java/com/brainybrawl/app/OfflineQuestionsTest.kt`
- `app/src/test/java/com/brainybrawl/app/PackagedSvgTest.kt`
- `app/src/test/java/com/brainybrawl/app/PlayerStateTest.kt`
- `app/src/test/java/com/brainybrawl/app/RealtimeStateTest.kt`
- `app/src/test/java/com/brainybrawl/app/TeamSnapshotContractTest.kt`
- `app/src/test/resources/server/duo_draft.json`
- `app/src/test/resources/server/duo_initial.json`
- `app/src/test/resources/server/duo_results.json`
- `app/src/test/resources/server/duo_scramble.json`
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
- `tools/backend-tests/edge-body.test.mjs`
- `tools/backend-tests/package-lock.json`
- `tools/backend-tests/package.json`
- `tools/backend-tests/security.test.mjs`
- `tools/content_pipeline.py`
- `tools/package_changed_files.py`
- `tools/test_content_pipeline.py`

## Modified files

- `.gitignore`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/brainybrawl/app/MainActivity.kt`
- `app/src/main/java/com/brainybrawl/app/ui/theme/Color.kt`
- `app/src/main/java/com/brainybrawl/app/ui/theme/Theme.kt`
- `app/src/main/java/com/brainybrawl/app/ui/theme/Type.kt`
- `app/src/main/res/drawable/ic_launcher_foreground.xml`
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
| `.github/workflows/android.yml` | `B9E9564405AF9CC7016B1C908AE829180A53469EAD4444BE4BA8BECD3CEAAA56` |
| `.gitignore` | `B615D273E0F288ACB482A2CA253F44D9FA2A4689E0B15848C6E1F7F85956F24E` |
| `app/build.gradle.kts` | `CFB9332BC943014EA33AB0757AB7F8C535AD5595C4119D066C095E4726145E1B` |
| `app/proguard-rules.pro` | `FE1AB388082D4E267293CA2CA9DD0A6D43CDBA89ED2751F72B923C72156B45BE` |
| `app/src/androidTest/java/com/brainybrawl/app/EncryptedAuthStoreTest.kt` | `2643004D3DF8321AB85C87B73EBE08DC4FD560548B7ED1451B7BE962BAD109CA` |
| `app/src/androidTest/java/com/brainybrawl/app/GameHudVisualTest.kt` | `31F2B6E4645A224154C4CAA11867E5BEF57A9B716EE8A77E44200BE06FB102C4` |
| `app/src/androidTest/java/com/brainybrawl/app/LocalizationUiTest.kt` | `91133145B05F87E599DC5B0A98A26FA8B39C67F83F1097E63CF0748752DE93C4` |
| `app/src/androidTest/java/com/brainybrawl/app/NavigationSmokeTest.kt` | `D9E62DB06E52016D4D948D97E4BC0B25AA5BD56931C96A21398716C988BFE3E9` |
| `app/src/androidTest/java/com/brainybrawl/app/RecoveryNavigationTest.kt` | `201D0C6BEEBF77AACE541FB95BDAA9A8504591467035BF5F8043E5A6EB7503ED` |
| `app/src/androidTest/java/com/brainybrawl/app/RedesignedNavigationTest.kt` | `CC096ECF0A1A00C8414C4484E2C9C78852D6316BD2BED8D63A0F6C201A92DCB9` |
| `app/src/androidTest/java/com/brainybrawl/app/TeamMiniGameUiTest.kt` | `73E688534BAB386AE9FED88C395C6B0827331D0B8D3D1AD3F2065C1C44F288C1` |
| `app/src/main/AndroidManifest.xml` | `277EC70B7CC7069BD70805F6536A3F7628989D2FE5F907E3ED131F74B96ED6EA` |
| `app/src/main/assets/licenses/Poppins-OFL.txt` | `B741A4319A716FA488D77425010697A0990798F7647E07C606F46CF0B55957C7` |
| `app/src/main/java/com/brainybrawl/app/BrainyBrawlApplication.kt` | `148F92C567B4E8EB6D4D5723D8690CFA66392FA0B57596C303359E8BAC2DD269` |
| `app/src/main/java/com/brainybrawl/app/MainActivity.kt` | `73D303BF073B247BA3EB15BE47BBDE55EA9847F7B34672825AF17E216139ED8A` |
| `app/src/main/java/com/brainybrawl/app/core/AppContainer.kt` | `52166578F617FC6C93FCAEFDA6080677645B5F4625BA0C2A9F4253659F7A1509` |
| `app/src/main/java/com/brainybrawl/app/core/common/Outcome.kt` | `0556A343B3A7FBD991097893B4A8EA23986FC9BBA9F832099C0978C4BABC61F6` |
| `app/src/main/java/com/brainybrawl/app/core/design/BrawlComponents.kt` | `B71E21736E0EF2A6A823BD746ABBF7AE4B1EDBA6379904B7472E707E719991FE` |
| `app/src/main/java/com/brainybrawl/app/core/design/ContentImage.kt` | `D1F004FE2975B8AA22BF0A2B7E463E89A7AF3EB9262CB24EC4B5C8250FBA9133` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameIdentity.kt` | `E5C6E04A861CA156520A6C3097C025B3CD5D2CBE3CF2A8A71674A4FFBC9FBEBD` |
| `app/src/main/java/com/brainybrawl/app/core/design/GameplayFeedback.kt` | `D119FFE12F04EC01987A4976C5FF1F788C322B84096D17462ED421F25663DB39` |
| `app/src/main/java/com/brainybrawl/app/core/design/NavigationSymbol.kt` | `EA75AD12AE157822A1987C84D031194B20D3B8F18B03E69A1AD440ED6FE55F72` |
| `app/src/main/java/com/brainybrawl/app/core/diagnostics/Diagnostics.kt` | `104444D2CE72092AC45983829673DB836BBF93A4655EB531F4B220BD82DE8E51` |
| `app/src/main/java/com/brainybrawl/app/core/localization/CatalogLabels.kt` | `710369AF6CD41EA1E3507EC0DA44EFF97F40AA0059F403C4295FE8156738A913` |
| `app/src/main/java/com/brainybrawl/app/core/localization/LocalizedContent.kt` | `4C9F48A73347448637DAB7D5D6A05855DE6EF64C4CB2D957640A28B37B697C42` |
| `app/src/main/java/com/brainybrawl/app/core/localization/NamedStrings.kt` | `5E897474202C6695F0C841EF1B820847EC924A86C539D8B035E2B216DF87BDA2` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/BrawlApp.kt` | `B8DF55E344C80180A02AC93C4648B317D8F66F14AC9664AECADB5E33C301C414` |
| `app/src/main/java/com/brainybrawl/app/core/navigation/LegalLinks.kt` | `8C1FD64219666E66FDAF9D6D3153FEA4376B5A8BFE158851BF09FB57C2BD4242` |
| `app/src/main/java/com/brainybrawl/app/core/network/BackendConfig.kt` | `BC9974E0D2936DF516E2F04EBAF55C28C3201C109F059C8A320E2BE986622FA6` |
| `app/src/main/java/com/brainybrawl/app/core/network/ServerClock.kt` | `4381897A4729519788A98B15BAF31137D032E6F441F4384BC4572280524A0588` |
| `app/src/main/java/com/brainybrawl/app/core/security/EncryptedAuthStore.kt` | `A5A3C75076AA993DCCAB5E3A2CB03C732DAC06EAE597795CF5A862917D1318A6` |
| `app/src/main/java/com/brainybrawl/app/core/security/PackagedSvg.kt` | `CA6D9FE0BD821A5552BF9A3C2A22D84C5B735C5D2CB81658EEDDB5EFACB017EB` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthRepository.kt` | `944654F83B3690ED693F4CBA81BD5EC8453398EDF44B6A700532EFED8597E8EF` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthScreen.kt` | `A797C085FEC04047234123A520C0CDE45490C52742ABCE6BA055A0BA8CA85044` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/AuthViewModel.kt` | `B81453826F585D253F9FD1760A2B415401C60032BA5EF846B62C9072B54C4020` |
| `app/src/main/java/com/brainybrawl/app/feature/auth/SupabaseAuthRepository.kt` | `6E49440581C94F111D62A7866E77CCF7BBA21F9E36C6B21E0D1901819F26D903` |
| `app/src/main/java/com/brainybrawl/app/feature/friends/FriendsScreen.kt` | `3B70F33191EE4D0F0F5AE3173D475D0B5358DE4C3594ADA8CCF5E7C3A8BFBE00` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardRepository.kt` | `0D56A53E6308E23934BFD43BAD344C29C159B180B668DE8A6CBAA5C4AD5BC852` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardScreen.kt` | `A8CD0690539975EECAC6B214280086D105198AD13CB54DC09E31C3C4C3D0F426` |
| `app/src/main/java/com/brainybrawl/app/feature/leaderboard/LeaderboardViewModel.kt` | `6B988A2AB72D2D2C92B31123121C9209FC4C1CA926DDD2A8E5BCCE4CA87762B2` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/LobbyScreen.kt` | `FD5EDFF6B62F987B1E7A0FE60ECF8762D68C12F6D1D90771684DEA835B3A3953` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/ReactionPanel.kt` | `C3A039F1E51DD7545957EC0F53F754A114DD9D0F68308ADBDAF0D99580B17354` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomModels.kt` | `E9AB5CEC635E5E488F585F9CB8A1A3FCD49DDAA6B9D346B3D1E98C699F158BDE` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomRepository.kt` | `00C2FE1E6DF36251165BB5A7C7FDA3F6E94A4875D5D4DFFC90BB6A46161E42B7` |
| `app/src/main/java/com/brainybrawl/app/feature/lobby/RoomViewModel.kt` | `5592511637F962EA0AFC0D38509C1BFA8800A5D9E19D2D348B7B61845EDEA31B` |
| `app/src/main/java/com/brainybrawl/app/feature/match/BoardModels.kt` | `AE9C2903D83C127D5AF9A47408C6A2B97F87E2258E7D5F9A3C3B1BE73DA62193` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchModels.kt` | `405CC1495D3CF07AEBD27952CFCF04E9791DC3AD4065D1C59A4F114AAA399721` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchRepository.kt` | `018F87A53A49C7BCFFA53BA714C26FBAC2671B5179D1F0DFB50840ACD8C97338` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchScreen.kt` | `EF4F7A7D1CF8A42FBA49AD3AA3DBA73D96E4D3070CA5718E62652B3EDAAC51CC` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MatchViewModel.kt` | `BE071B88FADD78B5BD0BAF0BEB10E4F9EEB0EC674BE802213E4B3307FD007365` |
| `app/src/main/java/com/brainybrawl/app/feature/match/MiniGameComponents.kt` | `A580FF8A5822CEF58367F7A3E2690B253DC6F6CB3E2541EF0E7C1A8A47EB784C` |
| `app/src/main/java/com/brainybrawl/app/feature/match/PuzzleGame.kt` | `5E9577A7C0E3B51D7CDC9EC3CCF1431D021E684EB27ABD0CD6D5607EC6D685DE` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RouletteWheel.kt` | `0EA9DD955E92043A0FA44832A194A3C8E9D18CBC52C1BF032C178EAEE73A3736` |
| `app/src/main/java/com/brainybrawl/app/feature/match/RoundReveal.kt` | `346782B495FC85C97F6A8096729C2A8AA1B0E2E2A41075544C37CA5AAC90D33A` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineScreen.kt` | `83EB033A156E2E1BDF3AC4B3601D2E58DD427608DB2C9C76B670440E5A845003` |
| `app/src/main/java/com/brainybrawl/app/feature/offline/OfflineViewModel.kt` | `CF1F70DAEF71314F7267E869956B83BD8AE92C844D0C403E058B45812C688A46` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerRepository.kt` | `D11D8F00174D8FC61EABDB0A35E9D30C46EADD52CE7403037696563E970FC5C2` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/PlayerViewModel.kt` | `B1BAB9BFE40F94A3D497D92F79813046FBC20A7FBD5306B6AEB47B706E5AE636` |
| `app/src/main/java/com/brainybrawl/app/feature/profile/ProfileScreen.kt` | `FAF780C1EDF4EAEE0E51C6B1D6CA0D782DA99134B411703B3391494ACD8B7812` |
| `app/src/main/java/com/brainybrawl/app/feature/settings/SettingsRepository.kt` | `7AC85BA540168DAE9DAC2D4ACFED8139E345601B7DA13F9D77BAE7BFF6B49727` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreRepository.kt` | `E7AD4E2E76F422E519FE994EEB8B27C8958DDFAACED2DDC84503893A92977E76` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreScreen.kt` | `089A11A9B4B4A7273F313015B44B1EF9F9A67037AF8A3032B062A421DF1994A9` |
| `app/src/main/java/com/brainybrawl/app/feature/store/StoreViewModel.kt` | `A295B0C18E51E43474BD6C1E2F7DC0DD7CD8A46E825EEFC0C009923439A1192D` |
| `app/src/main/java/com/brainybrawl/app/game/content/ContentModels.kt` | `4EA4AEAB0CB7446C9ADD5ABF86514DA5FAE83AA60B360F0412CB9E7FD1548B0C` |
| `app/src/main/java/com/brainybrawl/app/game/content/ContentRepository.kt` | `BA80810911DDF321FA8C0C803B44C60DBA3F81E92DFECC649C0BCF6262DF1868` |
| `app/src/main/java/com/brainybrawl/app/game/content/XmlContentParser.kt` | `E289381B671FC30163E7E2D9D4D7199954C8F72F1E50FFA518D880884FA1700D` |
| `app/src/main/java/com/brainybrawl/app/game/engine/GameRules.kt` | `C77A4D56242742CCB9EC72560047FD87EAB25FEB2B8C028A413FD9E0158399B2` |
| `app/src/main/java/com/brainybrawl/app/game/engine/OfflineQuestions.kt` | `2A1F2804A99455696AAA127CF8EE7836B40D7CE8EC07A7643F57D3A25C695C68` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Color.kt` | `2A914C945292C99B3635E0A4B319E3448AD7B23B1961F69B7C31706EED47BBDC` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Theme.kt` | `07AE9AE0018EB95405E058A2B42A3CE632A12574C09358996A8E209AB935B936` |
| `app/src/main/java/com/brainybrawl/app/ui/theme/Type.kt` | `34DD063BB9D6A7E14B8CE8256CAF280A1524929AD2DFDD5C72F83D735CDF074F` |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | `3BD90936981FA09C99DA8A4A897ACF894F1CF5A9121DF2BF7D4CE17A9A2AF629` |
| `app/src/main/res/font/poppins_bold.ttf` | `983676516167748B74DE6F4771FB384C664FD913ACB8B471122ECACF5DA5EA6C` |
| `app/src/main/res/font/poppins_extrabold.ttf` | `F2AB17C1A63A0ECC12C2461848FC8A469395E3CD2D641803E889C643D9F958E1` |
| `app/src/main/res/font/poppins_regular.ttf` | `7E65201E9B79159E2300267CC885E16C8DCEF2424CDFA09A29BFB0980A94A7BA` |
| `app/src/main/res/values-ar/strings.xml` | `CB5C37FF17176F6D7280AB462E57BB967E0EBEF12A01F30A46C7CCD5573F3811` |
| `app/src/main/res/values-fr/strings.xml` | `9ED03CB659A1077AD7C8E99048B8CAA0C6B6DE36FFB1B53747C99D91B0C7A137` |
| `app/src/main/res/values-v31/themes.xml` | `264008CEEFC2859C615DB958CC87DE19D4ACB40B98DEE34CE677E208AB84F9A6` |
| `app/src/main/res/values/strings.xml` | `DA301B43D0CFFBAABC089DA7311BCC571846613C657C1790516F5F912F1273CD` |
| `app/src/main/res/values/themes.xml` | `25DE0C97304ADD8E091A5B3EE115F69C93F5783065C185B5E92E45996F61EE15` |
| `app/src/test/java/com/brainybrawl/app/AuthValidationTest.kt` | `8F9B30E1AECBA120D95F2AD21AD7DFBFBFD9740382B3C13E8CB18A88AE2433C4` |
| `app/src/test/java/com/brainybrawl/app/BackendConfigTest.kt` | `A380405187046B6687F4BD34F23C80D6125F1D3BA1790187908824B8E2FFCD4F` |
| `app/src/test/java/com/brainybrawl/app/ContentTest.kt` | `9F77E69E9C467C7B4F469595D58E98CDC7B1DA8822C142CD90FA8479DBBD44F5` |
| `app/src/test/java/com/brainybrawl/app/DiagnosticsTest.kt` | `4B01661B5CA891899386461DB3E2F657825A95807D63C8DA46D4AAAB7534A632` |
| `app/src/test/java/com/brainybrawl/app/GameRulesTest.kt` | `340E2F976516C817EAAD4DF34BDAB80F870EF4AF40CAC8632024A4B64916E06C` |
| `app/src/test/java/com/brainybrawl/app/LocalizationContractTest.kt` | `7570992AA79B8995ED49872C566E6D2884CF29BB1D52AFA9A625A15DC13F4792` |
| `app/src/test/java/com/brainybrawl/app/MatchLifecycleTest.kt` | `46F0F9888CEC32976351AD6893982A8384173E66141C7BD914DF273FD61DCF5D` |
| `app/src/test/java/com/brainybrawl/app/MatchSnapshotTest.kt` | `C6094B4C190E25B20955BC8233985FF3AD13934E58F602121C463061DFAE948C` |
| `app/src/test/java/com/brainybrawl/app/NamedStringsTest.kt` | `74BED84D096CEF658836B53145F058A7066360484FA5450901A0DDC9421F2891` |
| `app/src/test/java/com/brainybrawl/app/OfflineQuestionsTest.kt` | `D5581DDCD918B7FF7D0FF31095B94A4A6860F49584AF4BD4A418121D1CC54DD7` |
| `app/src/test/java/com/brainybrawl/app/PackagedSvgTest.kt` | `CC675F11BBABEAE2E743EB942FFF87375EBC30FC3C67CAB6996B43EFE4E4D332` |
| `app/src/test/java/com/brainybrawl/app/PlayerStateTest.kt` | `FB48FC0EBCD47CB2E50F657564FFCF0633024CB437BC0160D7B81F3E712618D7` |
| `app/src/test/java/com/brainybrawl/app/RealtimeStateTest.kt` | `6B3CF9609A9F566E124DF50C5889245146913CEA01D51394080EE317101CCB04` |
| `app/src/test/java/com/brainybrawl/app/TeamSnapshotContractTest.kt` | `BD372C45A59A9FF363A9C9414A86921C2DF1453DCE8C9AEC2E627391827C9F57` |
| `app/src/test/resources/server/duo_draft.json` | `159DA645D83A97A3CE9B2DA00527D2A9C2A9BBFDA92E54F4B0098E9F4F5A1585` |
| `app/src/test/resources/server/duo_initial.json` | `B6FE8B464CEA31DE8B06183267BA4B934733BF962BBF9E89210D039484D096CF` |
| `app/src/test/resources/server/duo_results.json` | `5EC268776F96797DE3550AE477E3405DD0DA88694A12EFF3EA0A0E6618440284` |
| `app/src/test/resources/server/duo_scramble.json` | `B893291DD559D44EE679C8BE8D2A70C7E80A4714C2D60E40CEEF91E6DC7BAF41` |
| `app/src/test/resources/server/squad_draft.json` | `3FDCF5005C83F9420088C8B9F099045E9EACB95E1374F49E6CDA4CC340767CDA` |
| `app/src/test/resources/server/squad_initial.json` | `BEEA8CCEFAA7CDAD29EB7C937B21DCD9D4555DD597F2971D22001E6BF9BAFB85` |
| `app/src/test/resources/server/squad_results.json` | `06F7FA66084975B9AC11B09BA367DADCA8FEF99990B166044BE1B7FB2B538579` |
| `app/src/test/resources/server/squad_sort.json` | `F8D1337CAD2975C1EB0A4F843FC8C00F09256CD56701CC649956460B94146421` |
| `assets/images/asset_desert_001.svg` | `353D9411B497C909FE8C198C3D8D1442F415C24809C00D87EDE41C70742A4A77` |
| `assets/images/asset_history_ship_001.svg` | `E5BE2FFB1947E3E0161451473E25E79C3C1CD8C1D79D4396E535D79FF8F6C20A` |
| `assets/images/asset_lighthouse_001.svg` | `1F282E54261D86457083FA453B0805B2C70D9B73ECFB9C1F01ACC9D3E37E7435` |
| `assets/images/asset_puzzle_lighthouse_001.svg` | `1F282E54261D86457083FA453B0805B2C70D9B73ECFB9C1F01ACC9D3E37E7435` |
| `assets/images/asset_puzzle_whale_001.svg` | `EB71141ADCAF990CCF497C7FBCEAC4CCD94AA45187CAF8DD82F6199366A58A7F` |
| `assets/images/asset_space_001.svg` | `C31E3E61E500FBF249593AB833B8D364B5859A58B3BB8B0229F67ADB94937868` |
| `assets/images/asset_whale_001.svg` | `EB71141ADCAF990CCF497C7FBCEAC4CCD94AA45187CAF8DD82F6199366A58A7F` |
| `build.gradle.kts` | `B2DE004828062302CAFAD6AD8FE6C6CE1BCD1218C05A968ABBE9A7F501DB520F` |
| `config.example.properties` | `9B4B0A1EBB67D8787E7760EAA9204D7B34E9D90CAC749AF3122F81066C01B771` |
| `content/collaborative_puzzle.xml` | `5EF4C550C51D483F8278A8349BFE1C08A2773CF08266DB8E0B52693540164087` |
| `content/image_guess.xml` | `BE51175D7B45F8D01ED0F0ACA6DDB05F52790280C4E0B58E840B74C4C4412C28` |
| `content/precision_tap.xml` | `D7125166080EE4AE326C8C46EB2491D6E0D093C84A502FBEA286C2830C8F8710` |
| `content/question_round.xml` | `1CABC608C8A0E400C1A86E572DB055C3CE4063A3719F79477E49E54161A8B0A9` |
| `content/reactions.xml` | `C63E10CB011A09948BC413B685CB0C1F9C6D5DE5D9D2CCD39DB79C51DFB7C217` |
| `content/roll_the_dice.xml` | `36224A968FB312AF7FE544BFD9D90C1D405B2B370ADCD8DE82076CE7690F2A4D` |
| `content/speed_sort.xml` | `061E7248F9C7D71FEA21E150EC32DEDD09DE421A9FA5C97108771C52D75F699D` |
| `content/word_scramble.xml` | `69897FA69F34DB6CCA6ACFC148AAF366D5BE0ADE5462E92041E59AAD2F458ED3` |
| `docs/ASSET_CREDITS.md` | `CD5C2F8C5DEFBACE104005FC093DDAB215548D7E574901BDB1C65FFFF6F4362E` |
| `docs/IMPLEMENTATION_STATUS.md` | `AE9D216D9DB59376BAD58D85761E5BD417FE12E2F2D5ADA93805DDF78B0870A0` |
| `docs/RELEASE_CHECKLIST.md` | `23F55DA8CD63C4C28E648C52B9DB616FCB2124C40573B3EFCB8BD3BC34FDE53E` |
| `docs/SESSION_REPORT.md` | `32BE6C96FF24174F6B6959297D0A1CD7C728D85EAED40C00BEDC4C29CDA9C375` |
| `docs/UI_REDESIGN.md` | `F895E5AE374E3EEE937596FA335464F6678363D4186C4833BF94A4DF8B0FF6A8` |
| `docs/VISUAL_QA_RESULTS.md` | `AD40172D6C9D2607782A4B3349E0E6C23D40704591409B0936B2DB4486F25332` |
| `docs/ai-build-pack/10_MODES_DUO.md` | `8C00F08172FD4940AAA070ECD54AAEF785EAE099FC976210CE8F74FC0C7CE837` |
| `docs/ai-build-pack/11_MODES_SQUAD_SOLO.md` | `57A883AC592362C80A9393B70173970CD210100EDAC07E0D01C5FC2A58B6050F` |
| `docs/ai-build-pack/17_DECISION_REGISTER.md` | `A354ED7D2B41CA9AC1E8CF056632BAA7688AE53C89748A501500B4809684C035` |
| `gradle/libs.versions.toml` | `C8001C5F3F9BB2666BEAB62C963264CB809410E352E5E06BC19BD4E41CEFF659` |
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
| `tools/backend-tests/edge-body.test.mjs` | `0139D340420F562AEE7F97196E787BFFDE883B260CCEE2A095937A11D5C0285F` |
| `tools/backend-tests/package-lock.json` | `7A8D846C8374552BF2568DDFD503B376E76098A21B2F892F22F1210D371CE741` |
| `tools/backend-tests/package.json` | `9CCA30F279F7BAF3CB18E2719D93948E22847453048F0A1F6C9731B8B96AD779` |
| `tools/backend-tests/security.test.mjs` | `973A6B11A0AC0AABB8ED6AD958A1B8F18C1BA518BDABDEC82BB3F1A18E1C5B46` |
| `tools/content_pipeline.py` | `10A001D9077477A235B470DD14D5C6EF7DF002EEB7089B71ED120E414EB2D353` |
| `tools/package_changed_files.py` | `DB1F23DF48A0F177CA2FBCCE4890F39E11059D47B851C9ECB58DAD7278F624CE` |
| `tools/test_content_pipeline.py` | `5B72971CE601FF6E890BA17740290399952152D98B81ADD98D03A8084F42D950` |
