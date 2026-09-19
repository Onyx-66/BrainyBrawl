package com.brainybrawl.app.core.navigation

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.ui.theme.*
import com.brainybrawl.app.feature.auth.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.brainybrawl.app.BrainyBrawlApplication
import com.brainybrawl.app.feature.profile.*
import com.brainybrawl.app.feature.friends.FriendsScreen
import com.brainybrawl.app.feature.store.*
import com.brainybrawl.app.feature.lobby.*
import com.brainybrawl.app.feature.offline.*
import com.brainybrawl.app.feature.match.*
import com.brainybrawl.app.feature.leaderboard.*
import com.brainybrawl.app.core.localization.namedString

/** No tokens, passwords or user objects ever enter the navigation back stack. */
enum class Destination(val label: Int) {
    HOME(R.string.home), MODES(R.string.modes), STORE(R.string.store),
    PROFILE(R.string.profile), FRIENDS(R.string.friends), SETTINGS(R.string.settings),
    SHOWCASE(R.string.design_system), AUTH(R.string.login), PASSWORD(R.string.change_password), LOADOUT(R.string.loadout), LOBBY(R.string.lobby), GAME(R.string.modes), OFFLINE(R.string.offline), LEADERBOARDS(R.string.leaderboards)
}

@Composable
fun BrawlApp(authViewModel: AuthViewModel) {
    val auth by authViewModel.auth.collectAsStateWithLifecycle()
    val authUi by authViewModel.ui.collectAsStateWithLifecycle()
    val container=(LocalContext.current.applicationContext as BrainyBrawlApplication).container
    val playerModel: PlayerViewModel=viewModel(factory=PlayerViewModel.factory(container.players,container.auth))
    val playerState by playerModel.profile.collectAsStateWithLifecycle()
    val storeModel:StoreViewModel=viewModel(factory=StoreViewModel.factory(container.store,container.auth))
    val roomModel:RoomViewModel=viewModel(factory=RoomViewModel.factory(container.rooms,container.auth))
    val roomConnection by roomModel.connection.collectAsStateWithLifecycle()
    val leaderboardModel:LeaderboardViewModel=viewModel(factory=LeaderboardViewModel.factory(container.leaderboards,container.auth))
    val matchModel:MatchViewModel=viewModel(factory=MatchViewModel.factory(container.matches,container.auth))
    val offlineModel:OfflineViewModel=viewModel(factory=OfflineViewModel.factory(container.content,container.offlineStatistics))
    val roomActions by roomModel.actions.collectAsStateWithLifecycle()
    val socialUi by playerModel.social.collectAsStateWithLifecycle()
    var selectedMode by rememberSaveable { mutableStateOf(OnlineMode.DUEL) }
    var quickMatch by rememberSaveable { mutableStateOf(false) }
    var activeMatch by rememberSaveable { mutableStateOf<String?>(null) }
    var offlineSession by rememberSaveable { mutableStateOf(false) }
    if(auth==AuthState.Loading) {
        BrainyBrawlTheme {
            Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(32.dp),
                verticalArrangement=Arrangement.Center) {
                Text(stringResource(R.string.app_name),style=MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(24.dp))
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Text(stringResource(R.string.loading))
            }
        }
        return
    }
    val settings by container.settings.state.collectAsStateWithLifecycle()
    val dark=settings.dark
    val view=LocalView.current
    SideEffect {
        (view.context as? Activity)?.let { activity ->
            WindowCompat.getInsetsController(activity.window,view).apply {
                isAppearanceLightStatusBars=!dark
                isAppearanceLightNavigationBars=!dark
            }
        }
    }
    BrainyBrawlTheme(dark) {
        val nav = rememberNavController()
        val entry by nav.currentBackStackEntryAsState()
        val route = entry?.destination?.route ?: Destination.AUTH.name
        var previousAccount by remember{mutableStateOf<String?>(null)}
        LaunchedEffect(auth,authUi.notice,offlineSession,entry?.destination?.route) {
            if(entry==null) return@LaunchedEffect
            val account=(auth as? AuthState.SignedIn)?.userId
            if(previousAccount!=null&&previousAccount!=account){
                activeMatch=null;offlineSession=false
                previousAccount=account
                nav.navigate(if(account==null)Destination.AUTH.name else Destination.HOME.name){popUpTo(nav.graph.id){inclusive=false};launchSingleTop=true}
                return@LaunchedEffect
            }
            previousAccount=account
            if(auth is AuthState.SignedIn) {
                if(authUi.notice==AuthNotice.PASSWORD_RESET_READY){
                    nav.navigate(Destination.PASSWORD.name){popUpTo(nav.graph.id){inclusive=false};launchSingleTop=true}
                    authViewModel.consumeNotice()
                } else if(nav.currentDestination?.route==Destination.AUTH.name) nav.navigate(Destination.HOME.name) { popUpTo(Destination.AUTH.name) { inclusive=true }; launchSingleTop=true }
            } else if(auth!=AuthState.Loading && !offlineSession && route!=Destination.AUTH.name && route!=Destination.SETTINGS.name) {
                nav.navigate(Destination.AUTH.name) { popUpTo(nav.graph.id) { inclusive=false }; launchSingleTop=true }
            }
        }
        fun go(destination: Destination) { nav.navigate(destination.name) { launchSingleTop = true } }
        Scaffold(containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        BrainMark(Modifier.size(34.dp))
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
                    }
                    TextButton(onClick = { go(Destination.SETTINGS) }) { Text(stringResource(R.string.settings)) }
                }
            }, bottomBar = {
                if(route != Destination.AUTH.name && route != Destination.PASSWORD.name && route != Destination.OFFLINE.name && route != Destination.GAME.name) NavigationBar(containerColor = MaterialTheme.colorScheme.surface,tonalElevation=0.dp) {
                    listOf(Destination.HOME, Destination.MODES, Destination.STORE, Destination.PROFILE).forEach { dest ->
                        NavigationBarItem(selected = route == dest.name, onClick = { go(dest) },
                            colors=NavigationBarItemDefaults.colors(selectedIconColor=Cyan,selectedTextColor=Cyan,
                                indicatorColor=Cyan.copy(alpha=.12f),unselectedIconColor=MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor=MaterialTheme.colorScheme.onSurfaceVariant),
                            icon = { NavigationSymbol(when(dest){Destination.HOME->NavSymbol.HOME;Destination.MODES->NavSymbol.GAMES;Destination.STORE->NavSymbol.STORE;else->NavSymbol.PROFILE}) },
                            label = { Text(stringResource(dest.label)) })
                    }
                }
            }) { padding ->
            NavHost(nav, Destination.AUTH.name, Modifier.padding(padding).imePadding()) {
                Destination.entries.forEach { destination ->
                    composable(destination.name) {
                        Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(
                            MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface.copy(alpha = .4f))))
                            .verticalScroll(rememberScrollState()).padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(when(destination){Destination.GAME->8.dp;Destination.AUTH,Destination.PROFILE->10.dp;else->16.dp})) {
                            when(destination) {
                                Destination.AUTH -> AuthScreen(authViewModel, { offlineSession=true;go(Destination.MODES) })
                                Destination.PASSWORD -> AuthScreen(authViewModel, {}, changePassword=true)
                                Destination.PROFILE -> {
                                    val signedIn=auth as? AuthState.SignedIn
                                    if(signedIn==null) AuthScreen(authViewModel,{offlineSession=true;go(Destination.MODES)})
                                    else ProfileScreen(playerModel,{go(Destination.PASSWORD)},{offlineSession=false;authViewModel.logout()})
                                }
                                Destination.FRIENDS -> FriendsScreen(playerModel)
                                Destination.STORE -> StoreScreen(storeModel,auth is AuthState.SignedIn,playerModel::refresh)
                                Destination.LOADOUT -> LoadoutScreen(storeModel,{roomModel.create(selectedMode,quickMatch);go(Destination.LOBBY)})
                                Destination.LOBBY -> LobbyScreen(roomModel,(auth as? AuthState.SignedIn)?.userId,socialUi.snapshot.friends,
                                    {activeMatch=it;go(Destination.GAME)},{go(Destination.MODES)})
                                Destination.LEADERBOARDS -> LeaderboardScreen(leaderboardModel,auth is AuthState.SignedIn)
                                Destination.OFFLINE -> OfflineScreen(offlineModel,{go(Destination.MODES)})
                                Destination.GAME -> {
                                    MatchScreen(matchModel,activeMatch,{playerModel.refresh();go(Destination.MODES)})
                                    (roomConnection as? RoomConnection.Live)?.snapshot?.takeIf{it.room.status=="playing"&&it.matchId==activeMatch}?.let{
                                        ReactionPanel(roomModel,it,(auth as? AuthState.SignedIn)?.userId)
                                    }
                                }
                                Destination.HOME -> {
                                    (playerState as? PlayerDataState.Ready)?.data?.let { CurrencyBar(it) }
                                    Text(stringResource(R.string.welcome),style=MaterialTheme.typography.headlineLarge)
                                    GameHero{go(Destination.MODES)}
                                    Row(Modifier.height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                                        GameTile(stringResource(R.string.modes),stringResource(R.string.tile_modes),NavSymbol.GAMES,listOf(Color(0xFF2D9859),Color(0xFF16C78A)),Modifier.weight(1f).fillMaxHeight()){go(Destination.MODES)}
                                        GameTile(stringResource(R.string.leaderboards),stringResource(R.string.tile_rankings),NavSymbol.TROPHY,listOf(Color(0xFF7043CF),Color(0xFFAB53F2)),Modifier.weight(1f).fillMaxHeight()){go(Destination.LEADERBOARDS)}
                                    }
                                    Row(Modifier.height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                                        GameTile(stringResource(R.string.store),stringResource(R.string.tile_store),NavSymbol.STORE,listOf(Color(0xFFB96713),Color(0xFFFFAB35)),Modifier.weight(1f).fillMaxHeight()){go(Destination.STORE)}
                                        GameTile(stringResource(R.string.friends),stringResource(R.string.tile_friends),NavSymbol.PROFILE,listOf(Color(0xFF0875B5),Color(0xFF15B3D8)),Modifier.weight(1f).fillMaxHeight()){
                                            go(if(auth is AuthState.SignedIn)Destination.FRIENDS else Destination.PROFILE)
                                        }
                                    }
                                }
                                Destination.MODES -> {
                                    Text(stringResource(R.string.choose_mode),style=MaterialTheme.typography.headlineMedium)
                                    if(roomConnection!=RoomConnection.Empty)BrawlButton(stringResource(R.string.rejoin_room),{go(Destination.LOBBY)},Modifier.fillMaxWidth())
                                    var expandedMode by rememberSaveable{mutableStateOf<OnlineMode?>(null)}
                                    OnlineMode.entries.forEachIndexed{index,mode->
                                        ModeBanner(stringResource(when(mode){OnlineMode.DUEL->R.string.duel;OnlineMode.DUO->R.string.duo;OnlineMode.SQUAD->R.string.squad;OnlineMode.SOLO->R.string.solo}),
                                            stringResource(when(mode){OnlineMode.DUEL->R.string.duel_description;OnlineMode.DUO->R.string.duo_description;OnlineMode.SQUAD->R.string.squad_description;OnlineMode.SOLO->R.string.solo_description}),index,
                                            Modifier.clickable{expandedMode=if(expandedMode==mode)null else mode})
                                        if(expandedMode==mode){
                                            BrawlPanel(Modifier.fillMaxWidth()){
                                                if(auth !is AuthState.SignedIn)Text(stringResource(R.string.online_account_required),color=MaterialTheme.colorScheme.onSurfaceVariant)
                                                BrawlButton(stringResource(R.string.create_private),{
                                                    if(auth !is AuthState.SignedIn)go(Destination.PROFILE)
                                                    else{Diagnostics.record(ProductEvent.MODE_SELECTED);selectedMode=mode;quickMatch=false;storeModel.refresh();go(Destination.LOADOUT)}
                                                },Modifier.fillMaxWidth())
                                                BrawlButton(stringResource(if(mode==OnlineMode.DUEL)R.string.quick_match else R.string.find_public_room),{
                                                    if(auth !is AuthState.SignedIn)go(Destination.PROFILE)
                                                    else{Diagnostics.record(ProductEvent.MODE_SELECTED);selectedMode=mode;quickMatch=true;storeModel.refresh();go(Destination.LOADOUT)}
                                                },Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
                                            }
                                        }
                                    }
                                    BrawlPanel(Modifier.fillMaxWidth()){
                                        Text(stringResource(R.string.offline),style=MaterialTheme.typography.titleLarge)
                                        Text(stringResource(R.string.offline_language_note),style=MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.onSurfaceVariant)
                                        BrawlButton(stringResource(if(settings.language=="en")R.string.offline_questions else R.string.play_english_questions),{
                                            offlineModel.start("en");go(Destination.OFFLINE)
                                        },Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
                                    }
                                    if(auth is AuthState.SignedIn){
                                        BrawlButton(stringResource(R.string.refresh_invites),roomModel::refreshInvites,Modifier.fillMaxWidth())
                                        roomActions.invites.forEach{invite->BrawlButton(namedString(R.string.join_invite,"name" to invite.sender),{roomModel.join(invite.roomId);go(Destination.LOBBY)},Modifier.fillMaxWidth())}
                                    }
                                }
                                Destination.SETTINGS -> {
                                    LegalLinks()
                                    Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(stringResource(R.string.dark_theme),Modifier.weight(1f)); Switch(dark, { container.settings.update(settings.copy(dark=it)) })
                                    }
                                    Text(stringResource(R.string.language),style=MaterialTheme.typography.titleLarge)
                                    listOf("en" to R.string.language_english,"fr" to R.string.language_french,"ar" to R.string.language_arabic).forEach { (code,label) ->
                                        AnswerOption(stringResource(label),settings.language==code,true,{container.settings.update(settings.copy(language=code))})
                                    }
                                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                                        Text(stringResource(R.string.audio),Modifier.weight(1f));Switch(settings.audio,{container.settings.update(settings.copy(audio=it))})
                                    }
                                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
                                        Text(stringResource(R.string.vibration),Modifier.weight(1f));Switch(settings.vibration,{container.settings.update(settings.copy(vibration=it))})
                                    }
                                    BrawlButton(stringResource(R.string.design_system), { go(Destination.SHOWCASE) })
                                }
                                Destination.SHOWCASE -> {
                                    Text(stringResource(R.string.design_system), style = MaterialTheme.typography.headlineMedium)
                                    ActionTone.entries.forEach { tone -> BrawlButton(stringResource(R.string.continue_action), {}, Modifier.fillMaxWidth(), tone = tone) }
                                    AnswerOption(stringResource(R.string.option_example), true, true, {})
                                    TimerBar(stringResource(R.string.timer_example), .65f)
                                    PlayerCard(stringResource(R.string.player), stringResource(R.string.ready), "?", Modifier.fillMaxWidth())
                                    ResourceChip(stringResource(R.string.flames), "?", Gold)
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}
