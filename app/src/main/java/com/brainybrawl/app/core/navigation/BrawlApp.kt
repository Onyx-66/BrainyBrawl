package com.brainybrawl.app.core.navigation

import com.brainybrawl.app.core.diagnostics.Diagnostics
import com.brainybrawl.app.core.diagnostics.ProductEvent
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
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
import androidx.compose.ui.platform.testTag
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
import com.brainybrawl.app.feature.friends.*
import com.brainybrawl.app.feature.store.*
import com.brainybrawl.app.feature.lobby.*
import com.brainybrawl.app.feature.offline.*
import com.brainybrawl.app.feature.match.*
import com.brainybrawl.app.feature.leaderboard.*
import com.brainybrawl.app.core.localization.namedString

/** No tokens, passwords or user objects ever enter the navigation back stack. */
enum class Destination(val label: Int) {
    HOME(R.string.home), MODES(R.string.modes), MODE_OPTIONS(R.string.mode_options), OFFLINE_MODES(R.string.offline), OFFLINE_PUZZLE(R.string.offline_puzzle), STORE(R.string.store),
    PROFILE(R.string.profile), FRIENDS(R.string.friends), SETTINGS(R.string.settings),
    CONNECT_AUTH(R.string.connect_online), SHOWCASE(R.string.design_system), AUTH(R.string.login), PASSWORD(R.string.change_password), LOADOUT(R.string.loadout), LOBBY(R.string.lobby), GAME(R.string.modes), OFFLINE(R.string.offline), OFFLINE_IMAGES(R.string.offline_images), LEADERBOARDS(R.string.leaderboards)
}

@Composable
fun BrawlApp(authViewModel: AuthViewModel) {
    val auth by authViewModel.auth.collectAsStateWithLifecycle()
    val authUi by authViewModel.ui.collectAsStateWithLifecycle()
    val container=(LocalContext.current.applicationContext as BrainyBrawlApplication).container
    val playerModel: PlayerViewModel=viewModel(factory=PlayerViewModel.factory(container.players,container.auth))
    val playerState by playerModel.profile.collectAsStateWithLifecycle()
    val localState by container.localAccounts.state.collectAsStateWithLifecycle()
    val online=(auth as? AuthState.SignedIn)?.local==false
    val internet by container.connectivity.online.collectAsStateWithLifecycle()
    val connectionNotice by container.auth.connection.collectAsStateWithLifecycle()
    val storeModel:StoreViewModel=viewModel(factory=StoreViewModel.factory(container.store,container.auth))
    val roomModel:RoomViewModel=viewModel(factory=RoomViewModel.factory(container.rooms,container.auth))
    val roomConnection by roomModel.connection.collectAsStateWithLifecycle()
    val leaderboardModel:LeaderboardViewModel=viewModel(factory=LeaderboardViewModel.factory(container.leaderboards,container.auth))
    val matchModel:MatchViewModel=viewModel(factory=MatchViewModel.factory(container.matches,container.auth))
    val offlineModel:OfflineViewModel=viewModel(factory=OfflineViewModel.factory(container.content,container.offlineStatistics))
    val offlineImages:OfflineImageViewModel=viewModel(factory=OfflineImageViewModel.factory(container.content,container.offlineStatistics))
    val offlinePuzzle:OfflinePuzzleViewModel=viewModel(factory=OfflinePuzzleViewModel.factory(container.content,container.offlineStatistics))
    var pendingRoomAction by rememberSaveable{mutableStateOf<Boolean?>(null)}
    val roomActions by roomModel.actions.collectAsStateWithLifecycle()
    val socialUi by playerModel.social.collectAsStateWithLifecycle()
    var selectedMode by rememberSaveable { mutableStateOf(OnlineMode.DUEL) }
    var storeRequest by rememberSaveable { mutableIntStateOf(0) }
    var storeCurrency by rememberSaveable { mutableStateOf(OfferCurrency.GEMS) }
    var quickMatch by rememberSaveable { mutableStateOf(false) }
    var activeMatch by rememberSaveable { mutableStateOf<String?>(null) }
    var offlineSession by rememberSaveable { mutableStateOf(false) }
    val screenArt by container.screenArt.state.collectAsStateWithLifecycle()
    var startupPresented by rememberSaveable{mutableStateOf(false)}
    LaunchedEffect(Unit){container.screenArt.prepare()}
    LaunchedEffect(screenArt.ready,auth==AuthState.Loading){
        if(screenArt.ready&&auth!=AuthState.Loading){
            // Brief completion transition; the progress bar reflects actual loading work.
            kotlinx.coroutines.delay(350);startupPresented=true
        }
    }
    if(!startupPresented||!screenArt.ready||auth==AuthState.Loading){
        BrainyBrawlTheme{StartupSplash(screenArt,auth==AuthState.Loading)}
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
        var previousIdentity by remember{mutableStateOf<AuthState.SignedIn?>(null)}
        LaunchedEffect(auth,authUi.notice,offlineSession,entry?.destination?.route) {
            if(entry==null) return@LaunchedEffect
            val identity=auth as? AuthState.SignedIn
            val account=identity?.userId
            val promotion=previousIdentity?.local==true&&identity?.local==false&&previousIdentity?.email!=null&&previousIdentity?.email.equals(identity.email,true)
            if(previousIdentity!=null&&previousIdentity?.userId!=account&&!promotion){
                activeMatch=null;offlineSession=false;offlineModel.stop();offlineImages.stop();offlinePuzzle.stop();pendingRoomAction=null
                previousIdentity=identity
                nav.navigate(if(account==null)Destination.AUTH.name else Destination.HOME.name){popUpTo(nav.graph.id){inclusive=false};launchSingleTop=true}
                return@LaunchedEffect
            }
            previousIdentity=identity
            if(auth is AuthState.SignedIn) {
                if(authUi.notice==AuthNotice.PASSWORD_RESET_READY){
                    nav.navigate(Destination.PASSWORD.name){popUpTo(nav.graph.id){inclusive=false};launchSingleTop=true}
                    authViewModel.consumeNotice()
                } else if(nav.currentDestination?.route==Destination.AUTH.name) nav.navigate(Destination.HOME.name) { popUpTo(Destination.AUTH.name) { inclusive=true }; launchSingleTop=true }
                else if(identity?.local==false&&pendingRoomAction==null&&nav.currentDestination?.route==Destination.CONNECT_AUTH.name) nav.navigate(Destination.PROFILE.name){popUpTo(Destination.CONNECT_AUTH.name){inclusive=true};launchSingleTop=true}
            } else if(auth!=AuthState.Loading && !offlineSession && route!=Destination.AUTH.name && route!=Destination.SETTINGS.name) {
                nav.navigate(Destination.AUTH.name) { popUpTo(nav.graph.id) { inclusive=false }; launchSingleTop=true }
            }
        }
        fun go(destination: Destination) { nav.navigate(destination.name) { launchSingleTop = true } }
        LaunchedEffect(pendingRoomAction,online,internet){
            if(pendingRoomAction!=null&&online&&internet){
                quickMatch=pendingRoomAction!!;pendingRoomAction=null;storeModel.refresh();go(Destination.LOADOUT)
            }
        }
        LaunchedEffect(route){if(route !in setOf(Destination.MODE_OPTIONS.name,Destination.CONNECT_AUTH.name))pendingRoomAction=null}
        fun enterRoom(join:Boolean){
            pendingRoomAction=join
            if(auth !is AuthState.SignedIn){go(Destination.CONNECT_AUTH);return}
            if(!online||!internet)authViewModel.connectOnline()
        }
        val settingsLabel=stringResource(R.string.settings)
        val profileLabel=stringResource(R.string.profile)
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
        ScreenBackdrop(screenArt.home,.75f,Modifier.testTag("app-scene-background"))
        Scaffold(containerColor = Color.Transparent,
            contentColor=MaterialTheme.colorScheme.onBackground,
            topBar = {
                val identity=auth as? AuthState.SignedIn
                val player=(playerState as? PlayerDataState.Ready)?.data
                PlayerHeader(container.appearance,identity?.userId,
                    localState.current?.takeIf{!online}?.username ?: player?.profile?.username ?: stringResource(R.string.app_name),
                    player?.level?:1,
                    if(identity?.local==true)listOf(Balance("gold",0),Balance("gems",0),Balance("flames",0))else player?.currencies,
                    {go(Destination.PROFILE)},{go(Destination.SETTINGS)},{storeCurrency=it;storeRequest++;if(route!=Destination.STORE.name)go(Destination.STORE)})
            }, bottomBar = {
                if(route != Destination.AUTH.name && route != Destination.PASSWORD.name && route != Destination.OFFLINE.name && route != Destination.OFFLINE_IMAGES.name && route != Destination.OFFLINE_PUZZLE.name && route != Destination.GAME.name) GameNavigationBar(
                    listOf(Destination.HOME,Destination.MODES,Destination.STORE,Destination.PROFILE).map{dest->
                        GameNavItem(dest.name,stringResource(dest.label),when(dest){Destination.HOME->"home_icon";Destination.MODES->"games_icon";Destination.STORE->"store_icon";else->"profile_icon"})
                    },activeBottomDestination(route).name,{go(Destination.valueOf(it))})
            }) { padding ->
            NavHost(nav, Destination.AUTH.name, Modifier.padding(padding).imePadding()) {
                Destination.entries.forEach { destination ->
                    composable(destination.name) {
                        Column(Modifier.fillMaxSize()
                            .verticalScroll(rememberScrollState()).padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(when(destination){Destination.GAME->8.dp;Destination.AUTH,Destination.PROFILE->10.dp;else->16.dp})) {
                            when(destination) {
                                Destination.CONNECT_AUTH -> AuthScreen(authViewModel,{go(Destination.MODES)},onlineOnly=true)
                                Destination.AUTH -> AuthScreen(authViewModel, { offlineSession=true;go(Destination.OFFLINE_MODES) })
                                Destination.PASSWORD -> AuthScreen(authViewModel, {}, changePassword=true)
                                Destination.PROFILE -> {
                                    val signedIn=auth as? AuthState.SignedIn
                                    if(signedIn==null) AuthScreen(authViewModel,{offlineSession=true;go(Destination.OFFLINE_MODES)})
                                    else if(signedIn.local) LocalProfileScreen(container,authViewModel,{go(Destination.FRIENDS)},{go(Destination.PASSWORD)},{authViewModel.logout()})
                                    else ProfileScreen(playerModel,container,authViewModel,{go(Destination.FRIENDS)},{go(Destination.PASSWORD)},{offlineSession=false;authViewModel.logout()})
                                }
                                Destination.FRIENDS -> {
                                    if(online) FriendsScreen(playerModel)
                                    if(localState.current!=null && (!online || localState.current?.email.equals((auth as? AuthState.SignedIn)?.email,true)))
                                        LocalFriendsScreen(container.localAccounts,container.socialQueue,online,{go(Destination.CONNECT_AUTH)})
                                }
                                Destination.STORE -> StoreScreen(storeModel,online,playerModel::refresh,storeCurrency,storeRequest)
                                Destination.LOADOUT -> LoadoutScreen(storeModel,{roomModel.create(selectedMode,quickMatch);go(Destination.LOBBY)})
                                Destination.LOBBY -> LobbyScreen(roomModel,(auth as? AuthState.SignedIn)?.userId,socialUi.snapshot.friends,
                                    {activeMatch=it;go(Destination.GAME)},{go(Destination.MODES)})
                                Destination.LEADERBOARDS -> if(online)LeaderboardScreen(leaderboardModel,true) else AccountConnectionPanel(auth,internet,authUi.busy,if(authUi.notice!=AuthNotice.NONE)authUi.notice else connectionNotice,{authViewModel.connectOnline()},{go(Destination.CONNECT_AUTH)})
                                Destination.OFFLINE_IMAGES -> OfflineImageScreen(offlineImages){go(Destination.OFFLINE_MODES)}
                                Destination.OFFLINE -> OfflineScreen(offlineModel,{go(Destination.OFFLINE_MODES)})
                                Destination.OFFLINE_PUZZLE -> OfflinePuzzleScreen(offlinePuzzle){go(Destination.OFFLINE_MODES)}
                                Destination.GAME -> {
                                    MatchScreen(matchModel,activeMatch,{playerModel.refresh();go(Destination.MODES)})
                                    (roomConnection as? RoomConnection.Live)?.snapshot?.takeIf{it.room.status=="playing"&&it.matchId==activeMatch}?.let{
                                        ReactionPanel(roomModel,it,(auth as? AuthState.SignedIn)?.userId)
                                    }
                                }
                                Destination.HOME -> {
                                    if(auth is AuthState.SignedIn&&!online)AccountConnectionPanel(auth,internet,authUi.busy,if(authUi.notice!=AuthNotice.NONE)authUi.notice else connectionNotice,{authViewModel.connectOnline()},{go(Destination.CONNECT_AUTH)})
                                    Text(stringResource(R.string.welcome),style=MaterialTheme.typography.headlineLarge.copy(shadow=androidx.compose.ui.graphics.Shadow(Color(0xFF061228),androidx.compose.ui.geometry.Offset(0f,2f),8f)),color=Color.White)
                                    GameHero{go(Destination.MODES)}
                                    Row(Modifier.height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                                        GameTile(stringResource(R.string.modes),stringResource(R.string.tile_modes),NavSymbol.GAMES,listOf(Color(0xFF2D9859),Color(0xFF16C78A)),Modifier.weight(1f).fillMaxHeight()){go(Destination.MODES)}
                                        GameTile(stringResource(R.string.leaderboards),stringResource(R.string.tile_rankings),NavSymbol.TROPHY,listOf(Color(0xFF7043CF),Color(0xFFAB53F2)),Modifier.weight(1f).fillMaxHeight()){go(Destination.LEADERBOARDS)}
                                    }
                                    Row(Modifier.height(IntrinsicSize.Min),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                                        GameTile(stringResource(R.string.store),stringResource(R.string.tile_store),NavSymbol.STORE,listOf(Color(0xFFB96713),Color(0xFFFFAB35)),Modifier.weight(1f).fillMaxHeight()){go(Destination.STORE)}
                                        GameTile(stringResource(R.string.friends),stringResource(R.string.tile_friends),NavSymbol.FRIENDS,listOf(Color(0xFF0875B5),Color(0xFF15B3D8)),Modifier.weight(1f).fillMaxHeight()){
                                            go(if(auth is AuthState.SignedIn)Destination.FRIENDS else Destination.PROFILE)
                                        }
                                    }
                                }
                                Destination.MODES -> {
                                    Text(stringResource(R.string.choose_mode),style=MaterialTheme.typography.headlineMedium)
                                    if(roomConnection!=RoomConnection.Empty)BrawlButton(stringResource(R.string.rejoin_room),{go(Destination.LOBBY)},Modifier.fillMaxWidth())
                                    ModeBento(null){selectedMode=it;go(Destination.MODE_OPTIONS)}
                                    OfflineModeCard{go(Destination.OFFLINE_MODES)}
                                }
                                Destination.MODE_OPTIONS -> {
                                    Text(stringResource(when(selectedMode){OnlineMode.DUEL->R.string.duel;OnlineMode.DUO->R.string.duo;OnlineMode.SQUAD->R.string.squad;OnlineMode.SOLO->R.string.solo}),style=MaterialTheme.typography.headlineMedium)
                                    Text(stringResource(R.string.mode_options))
                                    ActionBento(listOf(
                                        BentoAction(stringResource(R.string.create_private),NavSymbol.HOME){enterRoom(false)},
                                        BentoAction(stringResource(R.string.join_a_room),NavSymbol.GAMES){enterRoom(true)}))
                                    AccountConnectionPanel(auth,internet,authUi.busy,if(authUi.notice!=AuthNotice.NONE)authUi.notice else connectionNotice,
                                        {authViewModel.connectOnline()},{go(Destination.CONNECT_AUTH)})
                                    if(online){
                                        BrawlButton(stringResource(R.string.refresh_invites),roomModel::refreshInvites,Modifier.fillMaxWidth())
                                        roomActions.invites.forEach{invite->BrawlButton(namedString(R.string.join_invite,"name" to invite.sender),{roomModel.join(invite.roomId);go(Destination.LOBBY)},Modifier.fillMaxWidth())}
                                    }
                                    BrawlButton(stringResource(R.string.back_to_modes),{pendingRoomAction=null;go(Destination.MODES)})
                                }
                                Destination.OFFLINE_MODES -> {
                                    Text(stringResource(R.string.offline),style=MaterialTheme.typography.headlineMedium)
                                    BrawlPanel(Modifier.fillMaxWidth()){
                                        Text(stringResource(R.string.offline_no_flames))
                                        ActionBento(listOf(
                                            BentoAction(stringResource(R.string.offline_questions),NavSymbol.GAMES){offlineModel.start(settings.language);go(Destination.OFFLINE)},
                                            BentoAction(stringResource(R.string.offline_images),NavSymbol.IMAGE){offlineImages.start(settings.language);go(Destination.OFFLINE_IMAGES)},
                                            BentoAction(stringResource(R.string.offline_puzzle),NavSymbol.PUZZLE){offlinePuzzle.start(settings.language);go(Destination.OFFLINE_PUZZLE)}))
                                    }
                                    BrawlButton(stringResource(R.string.back_to_modes),{go(Destination.MODES)})
                                }
                                Destination.SETTINGS -> {
                                    LegalLinks()
                                    Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(stringResource(R.string.dark_theme),Modifier.weight(1f)); Switch(dark, { container.settings.update(settings.copy(dark=it)) })
                                    }
                                    Text(stringResource(R.string.language),style=MaterialTheme.typography.titleLarge)
                                    LanguageDropdown(settings.language){container.settings.update(settings.copy(language=it))}
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
}

/** Nested screens retain their parent tab selection. */
internal fun activeBottomDestination(route:String?):Destination=when(route){
    Destination.MODES.name,Destination.MODE_OPTIONS.name,Destination.OFFLINE_MODES.name,Destination.OFFLINE.name,Destination.OFFLINE_IMAGES.name,Destination.OFFLINE_PUZZLE.name,Destination.LOADOUT.name,Destination.LOBBY.name,Destination.GAME.name->Destination.MODES
    Destination.STORE.name->Destination.STORE
    Destination.PROFILE.name,Destination.FRIENDS.name,Destination.PASSWORD.name,Destination.CONNECT_AUTH.name->Destination.PROFILE
    else->Destination.HOME
}
